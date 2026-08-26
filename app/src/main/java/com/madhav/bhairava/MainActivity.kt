package com.madhav.bhairava

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import com.madhav.bhairava.notify.AppSettings
import com.madhav.bhairava.ui.App
import com.madhav.bhairava.ui.theme.BhairavaTheme

class MainActivity : ComponentActivity() {

    private var deepLink by mutableStateOf<String?>(null)
    private var themeKey by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deepLink = intent?.getStringExtra("route")
        setContent {
            val mode = remember(themeKey) { AppSettings.getThemeMode(this) }
            val dark = when (mode) {
                "light" -> false
                "dark"  -> true
                else    -> null // follow system
            }
            BhairavaTheme(darkTheme = dark) {
                App(
                    deepLink = deepLink,
                    onDeepLinkHandled = { deepLink = null },
                    onThemeChanged = { themeKey++ }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLink = intent.getStringExtra("route")
    }
}
