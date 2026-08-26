package com.madhav.bhairava

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import java.io.File
import com.madhav.bhairava.ui.AmrtaListScreen
import com.madhav.bhairava.ui.BhairavaReaderScreen
import com.madhav.bhairava.ui.HomeScreen
import com.madhav.bhairava.ui.SivabodhaListScreen
import com.madhav.bhairava.ui.StanzaReaderScreen
import com.madhav.bhairava.ui.theme.BhairavaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w411dp-h891dp-420dpi")
class ScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val out = System.getProperty("user.dir")?.let {
        if (File(it).name == "app") File(it).parentFile?.absolutePath else it
    }?.let { "$it/screens" } ?: "screens"

    @Test
    fun home() = capture(dark = false, "home.png") {
        HomeScreen(onOpenSivabodha = {}, onOpenAmrta = {}, onOpenRoute = {})
    }

    @Test
    fun homeDark() = capture(dark = true, "home_dark.png") {
        HomeScreen(onOpenSivabodha = {}, onOpenAmrta = {}, onOpenRoute = {})
    }

    @Test
    fun sivabodhaList() = capture(dark = false, "sivabodha_list.png") {
        SivabodhaListScreen(onBack = {}, onOpen = {})
    }

    @Test
    fun sivabodhaListDark() = capture(dark = true, "sivabodha_list_dark.png") {
        SivabodhaListScreen(onBack = {}, onOpen = {})
    }

    @Test
    fun stanzaReader() = capture(dark = false, "stanza_reader.png") {
        StanzaReaderScreen(index = 0, onBack = {})
    }

    @Test
    fun stanzaReaderDark() = capture(dark = true, "stanza_reader_dark.png") {
        StanzaReaderScreen(index = 0, onBack = {})
    }

    @Test
    fun amrtaList() = capture(dark = false, "amrta_list.png") {
        AmrtaListScreen(onBack = {}, onOpen = {})
    }

    @Test
    fun amrtaListDark() = capture(dark = true, "amrta_list_dark.png") {
        AmrtaListScreen(onBack = {}, onOpen = {})
    }

    @Test
    fun bhairavaReader() = capture(dark = false, "bhairava_reader.png") {
        BhairavaReaderScreen(index = 0, onBack = {})
    }

    @Test
    fun bhairavaReaderDark() = capture(dark = true, "bhairava_reader_dark.png") {
        BhairavaReaderScreen(index = 0, onBack = {})
    }

    private fun capture(dark: Boolean, name: String, content: @Composable () -> Unit) {
        composeRule.setContent {
            BhairavaTheme(darkTheme = dark) { content() }
        }
        composeRule.onRoot().captureRoboImage("$out/$name")
    }
}
