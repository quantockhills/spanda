package com.madhav.bhairava

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.madhav.bhairava.notify.AppSettings
import com.madhav.bhairava.ui.App
import com.madhav.bhairava.ui.theme.BhairavaTheme

fun main() = application {
    var themeKey by remember { mutableIntStateOf(0) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Spanda",
        state = rememberWindowState(width = 1080.dp, height = 1600.dp)
    ) {
        val context = LocalContext.current
        val mode = remember(themeKey, context) { AppSettings.getThemeMode(context) }
        val dark = when (mode) {
            "light" -> false
            "dark" -> true
            else -> null // follow system
        }
        BhairavaTheme(darkTheme = dark) {
            App(
                deepLink = null,
                onDeepLinkHandled = {},
                onThemeChanged = { themeKey++ }
            )
        }
    }
}
