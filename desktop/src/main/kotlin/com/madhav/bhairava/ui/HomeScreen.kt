package com.madhav.bhairava.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madhav.bhairava.data.Repository
import com.madhav.bhairava.notify.Meditation
import com.madhav.bhairava.notify.buildPool
import com.madhav.bhairava.ui.theme.DevanagariFont
import com.madhav.bhairava.ui.theme.SerifFont
import kotlin.random.Random

@Composable
fun HomeScreen(
    onOpenSivabodha: () -> Unit,
    onOpenAmrta: () -> Unit,
    onOpenGita: () -> Unit,
    onOpenSamvarta: () -> Unit,
    onOpenRoute: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenNotes: () -> Unit
) {
    val context = LocalContext.current
    val lib = remember { Repository.library(context) }
    val pool = remember(lib) { buildPool(lib) }
    var todayIdx by remember { mutableIntStateOf(if (pool.isEmpty()) 0 else Random.nextInt(pool.size)) }
    val cover = rememberAssetImage("cover.jpg")

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ---- header ----
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(0.dp))
                    .background(Color(0xFF241C12))
            ) {
                if (cover != null) {
                    Image(
                        bitmap = cover,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(235.dp)
                            .clip(RoundedCornerShape(0.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x221E150D), Color(0xE6241C12))
                            )
                        )
                )
                Box(Modifier.fillMaxWidth().padding(20.dp)) {
                    Column {
                        Text(
                            "Spanda",
                            fontFamily = SerifFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = Color(0xFFFAF8F2)
                        )
                        Text(
                            "शिवबोधविंशिका · अमृतादिस्तवः · संवर्तस्तवः",
                            fontFamily = DevanagariFont,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            "Śivabodhaviṃśikā · Amṛtādistavaḥ · Saṃvarta Stavaḥ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFCBBFA8)
                        )
                    }
                    Row(Modifier.align(Alignment.TopEnd)) {
                        IconButton(onClick = onOpenNotes) {
                            Icon(Icons.Outlined.Edit, contentDescription = "My Notes", tint = Color(0xFFCBBFA8))
                        }
                        IconButton(onClick = onOpenFavorites) {
                            Icon(Icons.Outlined.Favorite, contentDescription = "Favorites", tint = Color(0xFFCBBFA8))
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = Color(0xFFCBBFA8))
                        }
                    }
                }
            }
        }

        // ---- today's meditation ----
        if (pool.isNotEmpty()) {
            item {
                val m: Meditation = pool[todayIdx % pool.size]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("TODAY'S MEDITATION", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { todayIdx = Random.nextInt(pool.size) }) {
                                Icon(Icons.Outlined.Refresh, contentDescription = "Another", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(m.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            m.sanskrit,
                            fontFamily = DevanagariFont,
                            fontSize = 17.sp,
                            lineHeight = 28.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            m.body,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { onOpenRoute(m.route) }) {
                            Text("Open", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ---- the four texts ----
        item {
            BookCard(
                devTitle = "शिवबोधविंशिका",
                title = "Śivabodhaviṃśikā",
                subtitle = "Twenty contemplations on the realization of Śiva — each stanza a bhāvanā, in order, with word-by-word glosses.",
                thumb = null,
                onClick = onOpenSivabodha
            )
        }
        item {
            BookCard(
                devTitle = "अमृतादिस्तवः",
                title = "Amṛtādistavaḥ",
                subtitle = "Abhinavagupta's hidden hymn — the fifty Bhairavas of the alphabet, each with its maṇḍala and translation.",
                thumb = rememberAssetImage("mandala_01.jpg"),
                onClick = onOpenAmrta
            )
        }
        item {
            BookCard(
                devTitle = "संवर्तस्तवः",
                title = "Saṃvarta Stavaḥ",
                subtitle = "Timalsina's hymn to Saṃvarta Bhairava — the four soma-pressings of the day, with the Saṃvartamaṇḍala.",
                thumb = rememberAssetImage("samvarta_p13_0.jpg"),
                onClick = onOpenSamvarta
            )
        }
        item {
            BookCard(
                devTitle = "भगवद्गीता",
                title = "Bhagavad Gītā",
                subtitle = "The Gītārtha-saṅgraha — Abhinavagupta's commentary on the Gita, verse by verse, in the Kashmiri recension.",
                thumb = null,
                onClick = onOpenGita
            )
        }

        // ---- attribution ----
        item {
            Text(
                "Text by Śaivācārya Abhinavagupta · Translated by Śaivācārya Sthaneshwar Timalsina\nSaṃvarta Stavaḥ: text and commentary by Sthaneshwar Timalsina · Vimarsha Foundation, San Diego, 2021 — for personal study",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 16.dp)
            )
        }
    }
}
