package com.madhav.bhairava.ui

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberAssetImage(name: String): ImageBitmap? {
    val context = LocalContext.current
    return remember(name) {
        try {
            context.assets.open(name).use { BitmapFactory.decodeStream(it) }?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}
