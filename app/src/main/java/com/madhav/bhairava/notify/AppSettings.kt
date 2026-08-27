package com.madhav.bhairava.notify

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

object AppSettings {
    private const val PREFS = "app_settings"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_FAVORITES = "favorites"
    private const val KEY_NOTES = "notes"

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
}
