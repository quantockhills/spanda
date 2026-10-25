package com.madhav.bhairava.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madhav.bhairava.data.VowEntry
import com.madhav.bhairava.data.VowsRepository
import com.madhav.bhairava.notify.AppSettings
import com.madhav.bhairava.ui.theme.DevanagariFont

/** Route prefix shared by the reader, notes and favourites: "vows/<index>". */
const val VOWS_ROUTE_PREFIX = "vows/"

/** Index list, grouped the same way as the other texts. Tapping a row opens the reader. */
@Composable
fun VowsScreen(onBack: () -> Unit, onOpen: (Int) -> Unit, onlyGroup: String? = null) {
    val context = LocalContext.current
    val book = remember { VowsRepository.load(context) }
    val shown = onlyGroup?.let { name -> book.groups.firstOrNull { it.name == name }?.name }

    Column(Modifier.fillMaxSize()) {
        ReaderTopBar(
            title = shown ?: book.title,
            subtitle = if (shown != null) book.title else book.subtitle,
            onBack = onBack,
            actions = {}
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            book.groups.forEachIndexed { gi, group ->
                if (shown != null && group.name != shown) return@forEachIndexed
                val base = book.groups.take(gi).sumOf { it.entries.size }
                item {
                    Column(Modifier.padding(top = 12.dp)) {
                        if (group.imageAsset.isNotEmpty()) {
                            val img = rememberAssetImage(group.imageAsset)
                            if (img != null) {
                                Image(
                                    bitmap = img,
                                    contentDescription = group.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxWidth().height(320.dp).padding(bottom = 8.dp)
                                )
                            }
                        }
                        Text(
                            group.name,
                            fontFamily = DevanagariFont,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (group.nameRoman.isNotEmpty()) {
                            Text(
                                group.nameRoman,
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (group.subtitleEn.isNotEmpty()) {
                            Text(group.subtitleEn, style = MaterialTheme.typography.bodySmall)
                        }
                        if (group.teachingsEn.isNotEmpty()) {
                            var showTeachings by remember(group.name) { mutableStateOf(false) }
                            Spacer(Modifier.height(10.dp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { showTeachings = !showTeachings }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (showTeachings) "HIDE TEACHINGS" else "TEACHINGS · S. TIMALSINA",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    if (showTeachings) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            AnimatedVisibility(visible = showTeachings) {
                                Column {
                                    paragraphs(group.teachingsEn).forEach { p ->
                                        Text(
                                            p,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(bottom = 10.dp)
                                        )
                                    }
                                }
                            }
                        }
                        group.viniyoga?.let { v ->
                            Spacer(Modifier.height(16.dp))
                            Text(
                                v.title.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                v.iast,
                                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            paragraphs(v.translationEn).forEach { p ->
                                Text(
                                    p,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            Text(
                                v.recitationEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(v.sourceEn, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                itemsIndexed(group.entries) { i, entry ->
                    VowRow(entry = entry, onClick = { onOpen(base + i) })
                }
            }
            item {
                Text(
                    book.source,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
                )
            }
        }
    }
}

@Composable
private fun VowRow(entry: VowEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(entry.ref, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(entry.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                entry.devanagari,
                fontFamily = DevanagariFont,
                fontSize = 17.sp,
                lineHeight = 28.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/** Reader: same pattern as StanzaReaderScreen (pager, favourites, notes per verse). */
@Composable
fun VowsReaderScreen(index: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val entries = remember { VowsRepository.load(context).entries }
    val pager = rememberPagerState(
        initialPage = index.coerceIn(0, (entries.size - 1).coerceAtLeast(0))
    ) { entries.size }

    Column(Modifier.fillMaxSize()) {
        ReaderTopBar(
            title = "Vows & Contemplations",
            subtitle = "${pager.currentPage + 1} of ${entries.size}",
            onBack = onBack,
            actions = {}
        )
        HorizontalPager(state = pager, modifier = Modifier.fillMaxSize()) { page ->
            VowPage(entry = entries[page], route = "$VOWS_ROUTE_PREFIX$page")
        }
    }
}

/** Splits a stored text into paragraphs (stored with blank lines between them). */
private fun paragraphs(s: String): List<String> =
    s.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }

@Composable
private fun VowPage(entry: VowEntry, route: String) {
    val context = LocalContext.current
    var isFavorite by remember(route) { mutableStateOf(AppSettings.isFavorite(context, route)) }
    var showGloss by remember(route) { mutableStateOf(false) }
    var showSanskrit by remember(route) { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            entry.ref,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Text(
            entry.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
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
        DividerGold()
        Spacer(Modifier.height(20.dp))

        Text(
            entry.devanagari,
            fontFamily = DevanagariFont,
            fontSize = 27.sp,
            lineHeight = 44.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        if (entry.transliteration.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Text(
                entry.transliteration,
                fontStyle = FontStyle.Italic,
                fontSize = 15.sp,
                lineHeight = 25.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(20.dp))

        if (entry.wordByWord.isNotEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { showGloss = !showGloss }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (showGloss) "HIDE WORD-BY-WORD" else "WORD-BY-WORD",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    if (showGloss) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            AnimatedVisibility(visible = showGloss) {
                Text(
                    entry.wordByWord,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        Text(
            "TRANSLATION · J. H. WOODS, 1914",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(6.dp))
        paragraphs(entry.translation).forEach { p ->
            Text(
                p,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        if (entry.commentaryEn.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                entry.commentaryLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))
            if (entry.introEn.isNotEmpty()) {
                Text(
                    entry.introEn,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            paragraphs(entry.commentaryEn).forEach { p ->
                Text(
                    p,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        if (entry.commentarySa.isNotEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { showSanskrit = !showSanskrit }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (showSanskrit) "HIDE SANSKRIT BHĀṢYA (IAST)" else "SANSKRIT BHĀṢYA (IAST)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    if (showSanskrit) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            AnimatedVisibility(visible = showSanskrit) {
                Column {
                    paragraphs(entry.commentarySa).forEach { p ->
                        Text(
                            p,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }
            }
        }

        VerseNotepad(route)
        Spacer(Modifier.height(28.dp))
    }
}
