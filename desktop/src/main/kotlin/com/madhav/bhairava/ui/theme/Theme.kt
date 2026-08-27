package com.madhav.bhairava.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ---- accents: medium dark red ----
val Crimson = Color(0xFFA02030)
val CrimsonLight = Color(0xFFD64856) // reads well on dark backgrounds
val DeepCrimson = Color(0xFF701522)
val Gold = Crimson // kept for API compat — accent is red, not gold
val GoldLight = Color(0xFFDCA5AB)

// ---- light (neutral near-white, no yellow cast) ----
val Parchment = Color(0xFFF7F7F4)
val CardBg = Color(0xFFFFFFFF)
val Ink = Color(0xFF241C12)
val MutedInk = Color(0xFF7A6A58)
val Line = Color(0xFFE5E5E2)

private val LightColors = lightColorScheme(
    primary = Crimson,
    onPrimary = Color.White,
    secondary = Crimson,
    onSecondary = Color.White,
    tertiary = GoldLight,
    background = Parchment,
    onBackground = Ink,
    surface = CardBg,
    onSurface = Ink,
    surfaceVariant = Color(0xFFF0EADF),
    onSurfaceVariant = MutedInk,
    outline = Line,
    outlineVariant = Line
)

private val DarkColors = darkColorScheme(
    primary = CrimsonLight,
    onPrimary = Color.White,
    secondary = CrimsonLight,
    onSecondary = Color.White,
    tertiary = Color(0xFFE8A0A8),
    background = Color(0xFF151515),
    onBackground = Color(0xFFF0F0F0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF0F0F0),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFA8A8A8),
    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF3A3A3A)
)

/**
 * Desktop font loading: Compose Multiplatform desktop's `FontFamily(resourcePath)`
 * reads a TTF straight from classpath resources (bundled under desktop/src/main/resources/fonts).
 */
@OptIn(ExperimentalTextApi::class)
val DevanagariFont: FontFamily = FontFamily("fonts/tiro_devanagari_regular.ttf")

@OptIn(ExperimentalTextApi::class)
val SerifFont: FontFamily = FontFamily("fonts/gentiumplus_regular.ttf")

private fun typography(text: Color, muted: Color): Typography = Typography(
    headlineLarge = TextStyle(fontFamily = SerifFont, fontWeight = FontWeight.Bold, fontSize = 30.sp, color = text),
    titleLarge = TextStyle(fontFamily = SerifFont, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = text),
    titleMedium = TextStyle(fontFamily = SerifFont, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = text),
    titleSmall = TextStyle(fontFamily = SerifFont, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = text),
    bodyLarge = TextStyle(fontFamily = SerifFont, fontSize = 17.sp, lineHeight = 26.sp, color = text),
    bodyMedium = TextStyle(fontFamily = SerifFont, fontSize = 15.sp, lineHeight = 23.sp, color = text),
    bodySmall = TextStyle(fontFamily = SerifFont, fontSize = 13.sp, lineHeight = 19.sp, color = muted),
    labelLarge = TextStyle(fontFamily = SerifFont, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = text),
    labelMedium = TextStyle(fontFamily = SerifFont, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.4.sp, color = muted),
    labelSmall = TextStyle(fontFamily = SerifFont, fontSize = 11.sp, letterSpacing = 1.2.sp, color = muted)
)

@Composable
fun BhairavaTheme(
    darkTheme: Boolean? = null,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = darkTheme ?: systemDark
    val scheme = if (useDark) DarkColors else LightColors
    MaterialTheme(
        colorScheme = scheme,
        typography = typography(scheme.onSurface, scheme.onSurfaceVariant),
        content = content
    )
}
