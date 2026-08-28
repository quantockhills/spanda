package com.madhav.bhairava.notify

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppSettings {
    private const val PREFS = "app_settings"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_NOTES = "notes"
    private const val KEY_SYNC_REFRESH = "sync_refresh_token"
    private const val KEY_SYNC_ACCESS = "sync_access_token"
    private const val KEY_SYNC_ACCESS_EXPIRES = "sync_access_expires_at"
    private const val KEY_SYNC_LAST = "sync_last_synced"
    private const val KEY_SYNC_SNAPSHOT = "sync_snapshot"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // ----- Theme -----
    fun getThemeMode(context: Context): String =
        prefs(context).getString(KEY_THEME_MODE, "system") ?: "system"

    fun setThemeMode(context: Context, mode: String) {
        if (mode in setOf("system", "light", "dark")) {
            prefs(context).edit().putString(KEY_THEME_MODE, mode).apply()
        }
    }

    // ----- Favorites (string set routes like "sivabodha/3" or "amrta/12") -----
    fun getFavorites(context: Context): Set<String> {
        return prefs(context).getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun isFavorite(context: Context, route: String): Boolean {
        return getFavorites(context).contains(route)
    }

    fun addFavorite(context: Context, route: String) {
        val current = getFavorites(context).toMutableSet()
        current.add(route)
        prefs(context).edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    fun removeFavorite(context: Context, route: String) {
        val current = getFavorites(context).toMutableSet()
        current.remove(route)
        prefs(context).edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    fun toggleFavorite(context: Context, route: String): Boolean {
        return if (isFavorite(context, route)) {
            removeFavorite(context, route)
            false
        } else {
            addFavorite(context, route)
            true
        }
    }

    // ----- Notes (route -> note text, JSON-encoded) -----
    fun getNotes(context: Context): Map<String, String> {
        val raw = prefs(context).getString(KEY_NOTES, null) ?: return emptyMap()
        return try {
            val obj = JSONObject(raw)
            val out = mutableMapOf<String, String>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                out[k] = obj.optString(k)
            }
            out
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getNote(context: Context, route: String): String = getNotes(context)[route] ?: ""

    fun saveNote(context: Context, route: String, text: String) {
        val notes = getNotes(context).toMutableMap()
        if (text.isBlank()) notes.remove(route) else notes[route] = text.trim()
        writeNotes(context, notes)
    }

    fun deleteNote(context: Context, route: String) {
        val notes = getNotes(context).toMutableMap()
        notes.remove(route)
        writeNotes(context, notes)
    }

    private fun writeNotes(context: Context, notes: Map<String, String>) {
        val obj = JSONObject()
        notes.forEach { (k, v) -> obj.put(k, v) }
        prefs(context).edit().putString(KEY_NOTES, obj.toString()).apply()
    }

    // ----- OneDrive sync -----

    fun getSyncRefreshToken(context: Context): String? =
        prefs(context).getString(KEY_SYNC_REFRESH, null)

    fun getSyncAccessToken(context: Context): String? =
        prefs(context).getString(KEY_SYNC_ACCESS, null)

    fun getSyncAccessExpiresAt(context: Context): Long =
        prefs(context).getString(KEY_SYNC_ACCESS_EXPIRES, null)?.toLongOrNull() ?: 0L

    fun setSyncTokens(context: Context, refresh: String, access: String, expiresAtMs: Long) {
        prefs(context).edit()
            .putString(KEY_SYNC_REFRESH, refresh)
            .putString(KEY_SYNC_ACCESS, access)
            .putString(KEY_SYNC_ACCESS_EXPIRES, expiresAtMs.toString())
            .apply()
    }

    fun clearSync(context: Context) {
        prefs(context).edit()
            .remove(KEY_SYNC_REFRESH)
            .remove(KEY_SYNC_ACCESS)
            .remove(KEY_SYNC_ACCESS_EXPIRES)
            .remove(KEY_SYNC_LAST)
            .remove(KEY_SYNC_SNAPSHOT)
            .apply()
    }

    fun getLastSyncTime(context: Context): Long =
        prefs(context).getString(KEY_SYNC_LAST, null)?.toLongOrNull() ?: 0L

    fun setLastSyncTime(context: Context, ms: Long) {
        prefs(context).edit().putString(KEY_SYNC_LAST, ms.toString()).apply()
    }

    /** Notes map from the last sync snapshot (used for 3-way merge). */
    fun getSyncSnapshot(context: Context): Map<String, String> {
        val raw = prefs(context).getString(KEY_SYNC_SNAPSHOT, null) ?: return emptyMap()
        return try {
            val obj = JSONObject(raw).optJSONObject("notes") ?: return emptyMap()
            val out = mutableMapOf<String, String>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                out[k] = obj.getString(k)
            }
            out
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun setSyncSnapshot(context: Context, payload: String) {
        prefs(context).edit().putString(KEY_SYNC_SNAPSHOT, payload).apply()
    }

    fun setFavorites(context: Context, favs: Set<String>) {
        prefs(context).edit().putStringSet(KEY_FAVORITES, favs).apply()
    }

    fun setNotes(context: Context, notes: Map<String, String>) {
        writeNotes(context, notes)
    }

    // ----- Local backups (safety net; works without sync) -----

    private const val KEY_BACKUP_MAX = 20

    /** Snapshot notes+favorites to app-local backups dir. Returns backup path or null. */
    fun backupNow(context: Context): String? {
        return try {
            val dir = File(context.getFilesDir(), "backups").apply { mkdirs() }
            val ts = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
            val payload = JSONObject()
            payload.put("favorites", JSONArray(getFavorites(context).toList()))
            val notesObj = JSONObject()
            getNotes(context).forEach { (k, v) -> notesObj.put(k, v) }
            payload.put("notes", notesObj)
            payload.put("version", 1)
            payload.put("backedUpAt", System.currentTimeMillis())
            val file = File(dir, "spanda-backup-$ts.json")
            file.writeText(payload.toString())
            pruneBackups(dir)
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /** Restore notes+favorites from the most recent local backup. Returns status text. */
    fun restoreLatestBackup(context: Context): String {
        val dir = File(context.getFilesDir(), "backups")
        val files = dir.listFiles { f -> f.name.startsWith("spanda-backup-") && f.name.endsWith(".json") }
            ?.sortedByDescending { it.lastModified() }
            ?: return "No backup found"
        if (files.isEmpty()) return "No backup found"
        return try {
            val obj = JSONObject(files.first().readText())
            val favs = obj.optJSONArray("favorites")?.let { arr ->
                (0 until arr.length()).map { arr.getString(it) }.toSet()
            } ?: emptySet()
            val notes = mutableMapOf<String, String>()
            obj.optJSONObject("notes")?.let { n ->
                val keys = n.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    notes[k] = n.getString(k)
                }
            }
            setFavorites(context, favs)
            setNotes(context, notes)
            "Restored ${favs.size} favorites + ${notes.size} notes from ${files.first().name}"
        } catch (e: Exception) {
            "Restore failed: ${e.message}"
        }
    }

    private fun pruneBackups(dir: File) {
        val files = dir.listFiles { f -> f.name.startsWith("spanda-backup-") && f.name.endsWith(".json") }
            ?.sortedByDescending { it.lastModified() }
            ?: return
        if (files.size > KEY_BACKUP_MAX) {
            files.drop(KEY_BACKUP_MAX).forEach { it.delete() }
        }
    }
}
