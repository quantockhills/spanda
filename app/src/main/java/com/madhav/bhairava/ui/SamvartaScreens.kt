package com.madhav.bhairava.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madhav.bhairava.data.Library
import com.madhav.bhairava.data.Repository
import com.madhav.bhairava.data.SamvartaVerse
import com.madhav.bhairava.notify.AppSettings
import com.madhav.bhairava.ui.theme.DevanagariFont

/** One page in the Saṃvarta reader: a verse, a transition verse, or the colophon. */
data class SamvartaEntry(
    val label: String,
    val sublabel: String,
    val verse: SamvartaVerse
)

/** Flatten opening + sections (+ closings) + colophon into pager/list order. */
fun flattenSamvarta(lib: Library): List<SamvartaEntry> {
    val out = mutableListOf<SamvartaEntry>()
    lib.samvartaOpening?.let {
        out += SamvartaEntry("Opening", lib.samvartaTitleRoman, it)
    }
    lib.samvartaSections.forEach { sec ->
        sec.verses.forEach { v ->
            out += SamvartaEntry("Verse ${v.n}", "${sec.name} · ${sec.subtitleEn}", v)
        }
        sec.closing?.let {
            out += SamvartaEntry("Closing verse", "${sec.name} · ${sec.subtitleEn}", it)
        }
    }
    lib.samvartaColophon?.let {
        out += SamvartaEntry(
            "Colophon",
            "इति स्थानेश्वरमुखोद्गीर्णः संवर्तस्तवः",
            SamvartaVerse(
                n = 0,
                devanagari = it.devanagari,
                transliteration = it.transliteration,
                translation = it.translation,
                image = null
            )
        )
    }
    return out
}

private fun devNum(n: Int): String {
    val digits = "०१२३४५६७८९"
    return n.toString().map { digits[it - '0'] }.joinToString("")
}

@Composable
fun SamvartaListScreen(onBack: () -> Unit, onOpen: (Int) -> Unit) {
    val context = LocalContext.current
    val lib = remember { Repository.library(context) }
    val entries = remember(lib) { flattenSamvarta(lib) }
    var expanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ReaderTopBar(title = "Saṃvarta Stavaḥ", subtitle = "संवर्तस्तवः · Hymn to Saṃvarta Bhairava", onBack = onBack)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(lib.samvartaTitle, fontFamily = DevanagariFont, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                        Text(lib.samvartaSubtitle, style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(4.dp))
                        Text(lib.samvartaAuthor, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        if (lib.samvartaIntro.isNotEmpty()) {
                            Text(lib.samvartaIntro.first(), style = MaterialTheme.typography.bodySmall)
                            TextButton(onClick = { expanded = !expanded }) {
                                Text(if (expanded) "Less" else "Read the introduction", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        AnimatedVisibility(visible = expanded) {
                            Column {
                                lib.samvartaIntro.drop(1).forEach { p ->
                                    Text(p, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("HIDE", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                    Icon(
                                        Icons.Outlined.KeyboardArrowUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        if (lib.samvartaCredits.isNotBlank()) {
                            Spacer(Modifier.height(10.dp))
                            Text(lib.samvartaCredits, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            lib.samvartaSections.forEach { sec ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SectionHeader(sec.name, "${sec.nameRoman} · ${sec.subtitleEn}")
                }
                items(sec.verses.size) { i ->
                    val v = sec.verses[i]
                    val idx = entries.indexOfFirst { it.verse === v }
                    SamvartaVerseCard(v, onClick = { onOpen(idx.coerceAtLeast(0)) })
                }
                sec.closing?.let { closing ->
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SamvartaClosingCard(closing, onClick = {
                            val idx = entries.indexOfFirst { it.verse === closing }
                            onOpen(idx.coerceAtLeast(0))
                        })
                    }
                }
            }
            lib.samvartaColophon?.let { col ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    val idx = entries.indexOfFirst { it.verse.n == 0 }
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onOpen(idx.coerceAtLeast(0)) },
                        shape = RoundedCornerShape(0.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Colophon", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(4.dp))
                            if (col.note.isNotBlank()) {
                                Text(col.note, fontFamily = DevanagariFont, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(Modifier.height(4.dp))
                            }
                            Text(col.devanagari, fontFamily = DevanagariFont, fontSize = 15.sp, lineHeight = 26.sp, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.height(6.dp))
                            Text(col.translation, style = MaterialTheme.typography.bodySmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SamvartaVerseCard(v: SamvartaVerse, onClick: () -> Unit) {
    val img = v.image?.let { rememberAssetImage(it) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            Box {
                if (img != null) {
                    Image(
                        bitmap = img,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxWidth().aspectRatio(1f).background(MaterialTheme.colorScheme.surfaceVariant))
                }
                Box(
                    Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(devNum(v.n), fontFamily = DevanagariFont, color = Color.White, fontSize = 14.sp)
                }
            }
            Column(Modifier.padding(10.dp)) {
                Text("Verse ${v.n}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Text(
                    v.translation,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SamvartaClosingCard(v: SamvartaVerse, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("Closing verse", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(v.devanagari, fontFamily = DevanagariFont, fontSize = 15.sp, lineHeight = 26.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            Text(v.translation, style = MaterialTheme.typography.bodySmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun SamvartaReaderScreen(index: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val lib = remember { Repository.library(context) }
    val entries = remember(lib) { flattenSamvarta(lib) }
    val pager = rememberPagerState(initialPage = index.coerceIn(0, (entries.size - 1).coerceAtLeast(0))) { entries.size }

    Scaffold(
        topBar = {
            ReaderTopBar(
                title = "Saṃvarta Stavaḥ",
                subtitle = "${entries.getOrNull(pager.currentPage)?.label ?: ""} · ${pager.currentPage + 1} of ${entries.size}",
                onBack = onBack
            )
        }
    ) { pad ->
        HorizontalPager(
            state = pager,
            modifier = Modifier.fillMaxSize().padding(pad)
        ) { page ->
            val entry = entries[page]
            val v = entry.verse
            val route = "samvarta/$page"
            var isFavorite by remember(page) { mutableStateOf(AppSettings.isFavorite(context, route)) }
            val img = v.image?.let { rememberAssetImage(it) }

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp)
            ) {
                Spacer(Modifier.height(6.dp))
                Text(
                    entry.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    entry.sublabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    IconButton(onClick = {
                        isFavorite = AppSettings.toggleFavorite(context, route)
                    }) {
                        Icon(
                            if (isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                if (img != null) {
                    Image(
                        bitmap = img,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(0.dp))
                            .border(1.5.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(0.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(20.dp))
                }
                if (v.devanagari.isNotBlank()) {
                    Text(
                        v.devanagari,
                        fontFamily = DevanagariFont,
                        fontSize = 23.sp,
                        lineHeight = 38.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                }
                if (v.transliteration.isNotBlank()) {
                    Text(
                        v.transliteration,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        lineHeight = 23.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(18.dp))
                }
                Text(v.translation, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                VerseNotepad(route)
                Spacer(Modifier.height(36.dp))
            }
        }
    }
}
