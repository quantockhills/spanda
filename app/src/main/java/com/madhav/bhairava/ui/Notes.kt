package com.madhav.bhairava.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madhav.bhairava.data.Library
import com.madhav.bhairava.notify.AppSettings
import com.madhav.bhairava.ui.theme.DevanagariFont

/**
 * Inline notepad below a verse/stanza. Persists via AppSettings keyed by route.
 * Empty -> tappable "add note" hint. Has note -> preview + tap to edit.
 */
@Composable
fun VerseNotepad(route: String) {
    val context = LocalContext.current
    var note by remember(route) { mutableStateOf(AppSettings.getNote(context, route)) }
    var editing by remember(route) { mutableStateOf(false) }
    var draft by remember(route) { mutableStateOf("") }

    Column(Modifier.fillMaxWidth().padding(top = 24.dp)) {
        DividerGold()
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "MY NOTE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(6.dp))

        if (editing) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Write your own contemplation…", fontSize = 14.sp) },
                minLines = 3,
                maxLines = 10
            )
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (note.isNotBlank()) {
                    TextButton(onClick = {
                        note = ""
                        draft = ""
                        editing = false
                        AppSettings.deleteNote(context, route)
                    }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(Modifier.width(8.dp))
                }
                TextButton(
                    onClick = {
                        note = draft.trim()
                        editing = false
                        AppSettings.saveNote(context, route, note)
                    },
                    enabled = draft.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        } else if (note.isBlank()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        draft = ""
                        editing = true
                    }
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    "Tap to add your own contemplation…",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        } else {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        draft = note
                        editing = true
                    }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Resolve a route ("gita/0/12", "sivabodha/3", "amrta/5") to a display title. */
fun routeTitle(lib: Library, route: String): Pair<String, String> {
    return when {
        route.startsWith("sivabodha/") -> {
            val idx = route.removePrefix("sivabodha/").toIntOrNull() ?: 0
            val s = lib.stanzas.getOrNull(idx)
            if (s != null) Pair(s.name, "Śivabodhaviṃśikā · ${s.ordinal}") else Pair("Unknown", "")
        }
        route.startsWith("amrta/") -> {
            val idx = route.removePrefix("amrta/").toIntOrNull() ?: 0
            val b = lib.bhairavas.getOrNull(idx)
            if (b != null) Pair(b.name, "Amṛtādistavaḥ · ${b.phoneme}") else Pair("Unknown", "")
        }
        route.startsWith("gita/") -> {
            val parts = route.removePrefix("gita/").split("/")
            val ch = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val v = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val gc = lib.gita.getOrNull(ch)
            val gv = gc?.verses?.getOrNull(v)
            if (gv != null) Pair("BG ${gc.n}.${gv.label}", gc.nameRoman.ifBlank { "Chapter ${gc.n}" }) else Pair("Unknown", "")
        }
        else -> Pair("Unknown", "")
    }
}

/** Dedicated screen listing all saved notes; tap to open the verse, trash to delete. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(onBack: () -> Unit, onOpenRoute: (String) -> Unit) {
    val context = LocalContext.current
    val lib = remember { com.madhav.bhairava.data.Repository.library(context) }
    var notes by remember { mutableStateOf(AppSettings.getNotes(context)) }

    fun refresh() {
        notes = AppSettings.getNotes(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Notes")
                        Text("${notes.size} saved", style = MaterialTheme.typography.labelSmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        if (notes.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(pad),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No notes yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tap the notepad below any verse to write your own contemplation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notes.entries.sortedBy { it.key }) { (route, text) ->
                    val (title, subtitle) = routeTitle(lib, route)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenRoute(route) },
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(title, fontFamily = DevanagariFont, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(onClick = {
                                AppSettings.deleteNote(context, route)
                                refresh()
                            }) {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = "Delete note",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
