package android.content.res

import java.io.InputStream

/** Desktop stand-in for android.content.res.AssetManager — reads classpath resources. */
class AssetManager {
    fun open(name: String): InputStream =
        checkNotNull(Thread.currentThread().contextClassLoader?.getResourceAsStream(name)) {
            "asset not found: $name"
        }
}
