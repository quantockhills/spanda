package android.content

import android.content.res.AssetManager
import java.io.File

/**
 * Desktop stand-in for Android's Context.
 *
 * The shared UI sources (read from app/src/main/java) call `LocalContext.current`
 * and pass it to Repository.library(context) / AppSettings.*(context). On the JVM
 * there is no android framework, so this shim provides exactly the surface those
 * calls need: assets (classpath resources) and SharedPreferences (JSON file).
 */
open class Context {
    val assets: AssetManager = AssetManager()

    fun getSharedPreferences(name: String, mode: Int): SharedPreferences = SharedPreferences(name)

    fun getFileDir(): File = File(System.getProperty("user.home"), ".spanda").apply { mkdirs() }

    fun getFilesDir(): File = File(System.getProperty("user.home"), ".spanda").apply { mkdirs() }

    companion object {
        const val MODE_PRIVATE = 0
    }
}
