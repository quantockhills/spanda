package com.madhav.bhairava.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.awt.FileDialog
import java.io.File

/**
 * Desktop file pickers for the backup feature (AWT FileDialog).
 * Android gets its own SAF implementation in app/src/main/java.
 */

/** Returns a trigger that opens a save dialog and writes the given text. */
@Composable
fun rememberExportBackup(): (String) -> Unit = remember {
    { text ->
        val dialog = FileDialog(null as java.awt.Frame?, "Export Spanda backup", FileDialog.SAVE)
        dialog.file = "spanda-backup.json"
        dialog.isVisible = true
        val dir = dialog.directory
        val file = dialog.file
        if (dir != null && file != null) {
            try {
                File(dir, file).writeText(text)
            } catch (_: Exception) {
            }
        }
    }
}

/** Returns a trigger that opens a load dialog and passes the file text to [onImported]. */
@Composable
fun rememberImportBackup(onImported: (String) -> Unit): () -> Unit = remember {
    {
        val dialog = FileDialog(null as java.awt.Frame?, "Import Spanda backup", FileDialog.LOAD)
        dialog.isVisible = true
        val dir = dialog.directory
        val file = dialog.file
        if (dir != null && file != null) {
            val f = File(dir, file)
            try {
                if (f.exists()) onImported(f.readText())
            } catch (_: Exception) {
            }
        }
    }
}
