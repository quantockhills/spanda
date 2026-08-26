package com.madhav.bhairava.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.madhav.bhairava.data.GitaChapter
import com.madhav.bhairava.data.Repository
import com.madhav.bhairava.notify.AppSettings
import com.madhav.bhairava.ui.theme.DevanagariFont

@Composable
fun GitaVerseReaderScreen(chapterIndex: Int, initialVerse: Int = 0, onBack: () -> Unit) {
    val context = LocalContext.current
    val lib = remember { Repository.library(context) }
    val chapter: GitaChapter = lib.gita.getOrNull(chapterIndex) ?: lib.gita.first()
    val verses = chapter.verses
    val pager = rememberPagerState(
        initialPage = initialVerse.coerceIn(0, (verses.size - 1).coerceAtLeast(0))
    ) { verses.size }

    Scaffold(
        topBar = {
            ReaderTopBar(
                title = "Chapter ${chapter.n}",
                subtitle = "${chapter.nameRoman.ifBlank { chapter.name }} · ${pager.currentPage + 1} of ${verses.size}",
                onBack = onBack
            )
        }
    ) { pad ->
        HorizontalPager(
            state = pager,
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) { page ->
            val v = verses[page]
            val route = "gita/$chapterIndex/$page"
            var isFavorite by remember(page) { mutableStateOf(AppSettings.isFavorite(context, route)) }

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "अध्याय ${devanagariNum(chapter.n)} · श्लोक ${devanagariNum(page + 1)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                if (v.sanskrit.isNotBlank()) {
                    Text(
                        v.sanskrit,
                        fontFamily = DevanagariFont,
                        fontSize = 24.sp,
                        lineHeight = 40.sp,
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
                    Spacer(Modifier.height(8.dp))
                }
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
                Spacer(Modifier.height(4.dp))
                DividerGold()
                Spacer(Modifier.height(20.dp))
                Text(
                    v.translation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (v.commentary.isNotBlank()) {
                    Spacer(Modifier.height(22.dp))
                    Text(
                        "ABHINAVAGUPTA'S COMMENTARY",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        v.commentary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(28.dp))
                Text(
                    "— ${chapter.n}.${v.label} —",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(36.dp))
            }
        }
    }
}

private fun devanagariNum(n: Int): String {
    val digits = mapOf('0' to "०", '1' to "१", '2' to "२", '3' to "३", '4' to "४",
        '5' to "५", '6' to "६", '7' to "७", '8' to "८", '9' to "९")
    return n.toString().map { digits[it] ?: it }.joinToString("")
}
