package com.madhav.bhairava.notify

import android.content.Context
import android.content.SharedPreferences

object AppSettings {
    private const val PREFS = "app_settings"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_FAVORITES = "favorites"

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
}
