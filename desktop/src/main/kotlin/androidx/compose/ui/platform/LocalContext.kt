package androidx.compose.ui.platform

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Desktop stand-in for androidx.compose.ui.platform.LocalContext.
 * The shared UI sources read `LocalContext.current` to reach Repository/AppSettings;
 * on the JVM this provides the android.content.Context shim.
 */
val LocalContext = staticCompositionLocalOf<Context> { Context() }
