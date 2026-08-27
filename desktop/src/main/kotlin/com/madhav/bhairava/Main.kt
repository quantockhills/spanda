package com.madhav.bhairava

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.madhav.bhairava.notify.AppSettings
import com.madhav.bhairava.ui.App
import com.madhav.bhairava.ui.theme.BhairavaTheme
import org.jetbrains.skia.Image
import java.io.InputStream

private fun windowIcon(): BitmapPainter? = try {
    val stream: InputStream? = Thread.currentThread().contextClassLoader?.getResourceAsStream("spanda_icon.png")
    stream?.use { Image.makeFromEncoded(it.readBytes()) }?.toComposeImageBitmap()?.let(::BitmapPainter)
} catch (e: Exception) {
    null
}

fun main() = application {
    var themeKey by remember { mutableIntStateOf(0) }
    val icon = remember { windowIcon() }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Spanda",
        state = rememberWindowState(width = 1080.dp, height = 1600.dp),
        icon = icon
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
