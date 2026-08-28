package com.madhav.bhairava.sync

import android.content.Context
import com.madhav.bhairava.notify.AppSettings
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * OneDrive sync for Spanda notes & favorites.
 *
 * Uses the OAuth 2.0 device code flow against the /consumers endpoint (personal
 * Microsoft accounts) and stores a JSON payload in the app's own private OneDrive
 * folder (special/approot) via Microsoft Graph.
 *
 * Merge strategy (3-way merge against last-synced snapshot, no timestamps needed):
 *  - favorites: union (never lose a favorite)
 *  - notes:     per-route — if local is unchanged since last sync, remote wins;
 *               if remote is unchanged, local wins; both changed -> local wins.
 */
object OneDriveSync {
    private const val CLIENT_ID = "6e3bed75-409b-4ae9-845c-bc3f476b8e18"
    private const val SCOPE = "Files.ReadWrite.AppFolder offline_access"
    private const val DEVICE_CODE_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode"
    private const val TOKEN_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token"
    private const val GRAPH_APPROOT = "https://graph.microsoft.com/v1.0/me/drive/special/approot"
    private const val SYNC_FILE = "spanda-sync.json"

    data class DeviceCodeInfo(
        val userCode: String,
        val verificationUri: String,
        val deviceCode: String,
        val intervalSec: Int,
        val expiresInSec: Int
    )

    fun isSignedIn(context: Context): Boolean =
        AppSettings.getSyncRefreshToken(context) != null

    // ----- Device code sign-in -----

    fun requestDeviceCode(): DeviceCodeInfo {
        val body = "client_id=$CLIENT_ID&scope=${enc(SCOPE)}"
        val (code, resp) = postForm(DEVICE_CODE_URL, body)
        if (code != 200) throw RuntimeException("device code request failed ($code): $resp")
        val json = JSONObject(resp)
        return DeviceCodeInfo(
            userCode = json.getString("user_code"),
            verificationUri = json.getString("verification_uri"),
            deviceCode = json.getString("device_code"),
            intervalSec = json.optInt("interval", 5),
            expiresInSec = json.optInt("expires_in", 900)
        )
    }

    /** Polls until the user approves. Returns null on success, error message otherwise. */
    fun pollForToken(
        context: Context,
        info: DeviceCodeInfo,
        cancelled: java.util.concurrent.atomic.AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean(false)
    ): String? {
        val deadline = System.currentTimeMillis() + info.expiresInSec * 1000L
        while (System.currentTimeMillis() < deadline) {
            if (cancelled.get()) return "Sign-in cancelled."
            Thread.sleep(info.intervalSec * 1000L)
            val body = "grant_type=urn:ietf:params:oauth:grant-type:device_code" +
                "&client_id=$CLIENT_ID&device_code=${enc(info.deviceCode)}"
            val (code, resp) = postForm(TOKEN_URL, body)
            if (code == 200) {
                val json = JSONObject(resp)
                val refresh = json.optString("refresh_token")
                val access = json.optString("access_token")
                if (refresh.isNotEmpty() && access.isNotEmpty()) {
                    AppSettings.setSyncTokens(
                        context, refresh, access,
                        System.currentTimeMillis() + json.optLong("expires_in", 3600) * 1000L
                    )
                    return null
                }
            } else {
                val err = try { JSONObject(resp).optString("error") } catch (e: Exception) { "" }
                when (err) {
                    "authorization_declined" -> return "Sign-in was declined."
                    "expired_token" -> return "Code expired. Please try again."
                    "bad_verification_code" -> return "Code error. Please try again."
                }
                // authorization_pending -> keep waiting
            }
        }
        return "Timed out waiting for sign-in."
    }

    fun signOut(context: Context) = AppSettings.clearSync(context)

    // ----- Sync -----

    fun syncNow(context: Context): String {
        // Safety net: snapshot local notes+favorites BEFORE merging remote changes,
        // so a botched sync can always be undone from ~/.spanda/backups/.
        try { AppSettings.backupNow(context) } catch (e: Exception) { }

        val access = ensureAccessToken(context) ?: return "Not signed in."
        var remote: JSONObject? = null
        val (getCode, getResp) = graphGet("$GRAPH_APPROOT:/$SYNC_FILE:/content", access)
        if (getCode == 200) {
            remote = try { JSONObject(getResp) } catch (e: Exception) { null }
        }

        val snapshot = AppSettings.getSyncSnapshot(context)
        val localFavs = AppSettings.getFavorites(context)
        val localNotes = AppSettings.getNotes(context)
        val remoteFavs = remote?.optJSONArray("favorites")?.let { arr ->
            (0 until arr.length()).map { arr.getString(it) }.toSet()
        } ?: emptySet()
        val remoteNotes = remote?.optJSONObject("notes")?.let { obj ->
            obj.keys().asSequence().associateWith { obj.getString(it) }
        } ?: emptyMap()

        // favorites: union
        val mergedFavs = localFavs + remoteFavs

        // notes: 3-way merge against snapshot
        val mergedNotes = LinkedHashMap<String, String>()
        val allKeys = localNotes.keys + remoteNotes.keys + snapshot.keys
        for (key in allKeys) {
            val l = localNotes[key]
            val r = remoteNotes[key]
            val s = snapshot[key]
            mergedNotes[key] = when {
                l == null -> r ?: continue
                r == null -> l
                l == s -> r
                r == s -> l
                else -> l // both changed -> local wins
            }
        }

        AppSettings.setFavorites(context, mergedFavs)
        AppSettings.setNotes(context, mergedNotes)
        val payload = buildPayload(mergedFavs, mergedNotes)
        AppSettings.setSyncSnapshot(context, payload)

        val (putCode, _) = graphPut("$GRAPH_APPROOT:/$SYNC_FILE:/content", payload, access)
        if (putCode !in 200..299) return "Upload failed ($putCode)."
        AppSettings.setLastSyncTime(context, System.currentTimeMillis())
        return "Synced ✓ ${mergedFavs.size} favorites, ${mergedNotes.size} notes"
    }

    private fun ensureAccessToken(context: Context): String? {
        val access = AppSettings.getSyncAccessToken(context)
        val expires = AppSettings.getSyncAccessExpiresAt(context)
        if (access != null && expires > System.currentTimeMillis() + 60_000L) return access
        val refresh = AppSettings.getSyncRefreshToken(context) ?: return null
        val body = "grant_type=refresh_token&client_id=$CLIENT_ID" +
            "&refresh_token=${enc(refresh)}&scope=${enc(SCOPE)}"
        val (code, resp) = postForm(TOKEN_URL, body)
        if (code != 200) return null
        val json = JSONObject(resp)
        val newRefresh = json.optString("refresh_token").ifEmpty { refresh }
        val newAccess = json.optString("access_token")
        if (newAccess.isEmpty()) return null
        AppSettings.setSyncTokens(
            context, newRefresh, newAccess,
            System.currentTimeMillis() + json.optLong("expires_in", 3600) * 1000L
        )
        return newAccess
    }

    private fun buildPayload(favs: Set<String>, notes: Map<String, String>): String {
        val notesObj = JSONObject()
        notes.forEach { (k, v) -> notesObj.put(k, v) }
        val obj = JSONObject()
        obj.put("favorites", JSONArray(favs.toList()))
        obj.put("notes", notesObj)
        obj.put("version", 1)
        return obj.toString()
    }

    // ----- HTTP helpers (java.net works on Android + desktop JVM) -----

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")

    private fun postForm(url: String, body: String): Pair<Int, String> {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.connectTimeout = 20_000
            conn.readTimeout = 20_000
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            return code to readStream(if (code in 200..299) conn.inputStream else conn.errorStream)
        } finally {
            conn.disconnect()
        }
    }

    private fun graphGet(url: String, access: String): Pair<Int, String> {
        var current = url
        var hops = 0
        while (true) {
            val conn = URL(current).openConnection() as HttpURLConnection
            try {
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer $access")
                conn.connectTimeout = 20_000
                conn.readTimeout = 20_000
                val code = conn.responseCode
                if (code in 300..399 && hops < 5) {
                    val loc = conn.getHeaderField("Location") ?: return code to ""
                    current = loc
                    hops++
                    continue
                }
                return code to readStream(if (code in 200..299) conn.inputStream else conn.errorStream)
            } finally {
                conn.disconnect()
            }
        }
    }

    private fun graphPut(url: String, json: String, access: String): Pair<Int, String> {
        val conn = URL(url).openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "PUT"
            conn.doOutput = true
            conn.setRequestProperty("Authorization", "Bearer $access")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 20_000
            conn.readTimeout = 30_000
            conn.outputStream.use { it.write(json.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            return code to readStream(if (code in 200..299) conn.inputStream else conn.errorStream)
        } finally {
            conn.disconnect()
        }
    }

    private fun readStream(stream: InputStream?): String {
        if (stream == null) return ""
        return stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
}
