package com.madhav.bhairava.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * Android file pickers for the backup feature (SAF).
 * Desktop gets an equivalent implementation in desktop/src/main/kotlin.
 */

/** Returns a trigger that opens "create document" and writes the given text. */
@Composable
fun rememberExportBackup(): (String) -> Unit {
    val context = LocalContext.current
    var pending by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val text = pending
        pending = null
        if (uri != null && text != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { it.write(text.toByteArray()) }
            } catch (_: Exception) {
            }
        }
    }
    return remember {
        { text ->
            pending = text
            launcher.launch("spanda-backup.json")
        }
    }
}

/** Returns a trigger that opens a document and passes its text to [onImported]. */
@Composable
fun rememberImportBackup(onImported: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val text = context.contentResolver.openInputStream(uri)
                    ?.use { it.readBytes() }?.toString(Charsets.UTF_8)
                if (text != null) onImported(text)
            } catch (_: Exception) {
            }
        }
    }
    return remember {
        { launcher.launch(arrayOf("application/json", "text/plain")) }
    }
}
