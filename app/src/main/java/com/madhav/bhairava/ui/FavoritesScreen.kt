package com.madhav.bhairava.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madhav.bhairava.data.Repository
import com.madhav.bhairava.data.Stanza
import com.madhav.bhairava.data.Bhairava
import com.madhav.bhairava.notify.AppSettings
import com.madhav.bhairava.ui.theme.DevanagariFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(onBack: () -> Unit, onOpenRoute: (String) -> Unit) {
    val context = LocalContext.current
    val lib = remember { Repository.library(context) }
    val favorites = remember { AppSettings.getFavorites(context).toList() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Favorites")
                        Text("${favorites.size} saved", style = MaterialTheme.typography.labelSmall)
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
        if (favorites.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(pad),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No favorites yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("Tap the ♥ button on any verse to save it here.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(pad),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(favorites) { route ->
                    FavoriteItem(route = route, lib = lib, onClick = { onOpenRoute(route) })
                }
            }
        }
    }
}

@Composable
private fun FavoriteItem(route: String, lib: com.madhav.bhairava.data.Library, onClick: () -> Unit) {
    val context = LocalContext.current
    val (title, subtitle) = when {
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, fontFamily = DevanagariFont, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
