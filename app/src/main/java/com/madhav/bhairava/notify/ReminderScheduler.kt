package com.madhav.bhairava.notify

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.json.JSONArray
import org.json.JSONObject
import java.time.Duration
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

/** One reminder: a time of day, and a verse route (e.g. "amrta/12") or "" for a random verse. */
data class ReminderSlot(val id: String, val hour: Int, val minute: Int, val route: String)

object ReminderScheduler {
    const val CHANNEL_ID = "daily_meditation"
    const val WORK_NAME = "daily_meditation"     // legacy single-time work name; also the prefix for slots
    const val PREFS = "reminder"
    const val KEY_ENABLED = "enabled"
    const val KEY_SLOTS = "slots"
    const val KEY_ROUTE = "route"
    const val KEY_HOUR = "hour"                  // legacy
    const val KEY_MINUTE = "minute"              // legacy
    const val DEFAULT_HOUR = 8
    const val DEFAULT_MINUTE = 0
    const val MAX_SLOTS = 8

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun workName(id: String) = "$WORK_NAME-$id"

    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, false)

    /** Saved reminders sorted by time. Migrates the single-time reminder from earlier versions. */
    fun slots(context: Context): List<ReminderSlot> {
        val p = prefs(context)
        val raw = p.getString(KEY_SLOTS, null)
        if (raw != null) {
            val arr = JSONArray(raw)
            return (0 until arr.length())
                .map { i ->
                    val o = arr.getJSONObject(i)
                    ReminderSlot(o.getString("id"), o.getInt("hour"), o.getInt("minute"), o.optString("route", ""))
                }
                .sortedWith(compareBy({ it.hour }, { it.minute }))
        }
        if (!p.contains(KEY_HOUR)) return emptyList()
        // The old single reminder keeps its time and becomes a random-verse slot.
        val migrated = ReminderSlot(newId(), p.getInt(KEY_HOUR, DEFAULT_HOUR), p.getInt(KEY_MINUTE, DEFAULT_MINUTE), "")
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        saveSlots(context, listOf(migrated))
        if (isEnabled(context)) schedule(context, migrated)
        return listOf(migrated)
    }

    /** Turns reminders on for every saved slot (adds 08:00 random if there are none). */
    fun enable(context: Context) {
        var list = slots(context)
        if (list.isEmpty()) {
            list = listOf(ReminderSlot(newId(), DEFAULT_HOUR, DEFAULT_MINUTE, ""))
            saveSlots(context, list)
        }
        prefs(context).edit().putBoolean(KEY_ENABLED, true).apply()
        list.forEach { schedule(context, it) }
    }

    fun disable(context: Context) {
        val wm = WorkManager.getInstance(context)
        slots(context).forEach { wm.cancelUniqueWork(workName(it.id)) }
        wm.cancelUniqueWork(WORK_NAME)
        prefs(context).edit().putBoolean(KEY_ENABLED, false).apply()
    }

    /** Returns false when the slot limit is reached. */
    fun addSlot(context: Context, hour: Int, minute: Int, route: String): Boolean {
        val list = slots(context)
        if (list.size >= MAX_SLOTS) return false
        val slot = ReminderSlot(newId(), hour, minute, route)
        saveSlots(context, list + slot)
        if (isEnabled(context)) schedule(context, slot)
        return true
    }

    fun updateSlot(context: Context, slot: ReminderSlot) {
        saveSlots(context, slots(context).map { if (it.id == slot.id) slot else it })
        if (isEnabled(context)) schedule(context, slot)
    }

    fun removeSlot(context: Context, id: String) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(id))
        saveSlots(context, slots(context).filterNot { it.id == id })
    }

    private fun saveSlots(context: Context, list: List<ReminderSlot>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(
                JSONObject()
                    .put("id", it.id)
                    .put("hour", it.hour)
                    .put("minute", it.minute)
                    .put("route", it.route)
            )
        }
        prefs(context).edit().putString(KEY_SLOTS, arr.toString()).apply()
    }

    private fun schedule(context: Context, slot: ReminderSlot) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(msUntilNext(slot.hour, slot.minute), TimeUnit.MILLISECONDS)
            .setInputData(Data.Builder().putString(KEY_ROUTE, slot.route).build())
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(workName(slot.id), ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    private fun newId(): String = UUID.randomUUID().toString().take(8)

    private fun msUntilNext(hour: Int, minute: Int): Long {
        val now = ZonedDateTime.now()
        var next = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next).toMillis().coerceAtLeast(60_000)
    }
}
