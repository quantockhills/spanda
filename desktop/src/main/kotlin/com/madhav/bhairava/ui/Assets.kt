package com.madhav.bhairava.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.InputStream
import javax.imageio.ImageIO

@Composable
fun rememberAssetImage(name: String): ImageBitmap? {
    return remember(name) {
        try {
            val stream: InputStream? = Thread.currentThread().contextClassLoader?.getResourceAsStream(name)
            stream?.use { ImageIO.read(it) }?.toComposeImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}
