package com.madhav.bhairava.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madhav.bhairava.data.Bhairava
import com.madhav.bhairava.data.Repository
import com.madhav.bhairava.ui.theme.Crimson
import com.madhav.bhairava.ui.theme.DevanagariFont
import com.madhav.bhairava.ui.theme.Gold
import com.madhav.bhairava.ui.theme.GoldLight
import com.madhav.bhairava.ui.theme.Ink
import com.madhav.bhairava.ui.theme.MutedInk

@Composable
fun BhairavaReaderScreen(index: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val lib = remember { Repository.library(context) }
    val entries = lib.bhairavas
    val pager = rememberPagerState(initialPage = index.coerceIn(0, (entries.size - 1).coerceAtLeast(0))) { entries.size }

    Scaffold(
        topBar = {
            ReaderTopBar(
                title = "Amṛtādistavaḥ",
                subtitle = "${pager.currentPage + 1} of ${entries.size}",
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
            val b = entries[page]
            val img = rememberAssetImage("mandala_${b.n.toString().padStart(2, '0')}.jpg")
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp)
            ) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(0.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            b.phoneme,
                            fontFamily = DevanagariFont,
                            color = androidx.compose.ui.graphics.Color.White,
                            fontSize = 23.sp
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(b.name, fontFamily = DevanagariFont, fontSize = 27.sp, color = MaterialTheme.colorScheme.primary)
                        Text(
                            "${b.nameRoman} · phoneme ${b.phonemeRoman}",
                            fontStyle = FontStyle.Italic,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                if (img != null) {
                    Image(
                        bitmap = img,
                        contentDescription = b.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(0.dp))
                            .border(1.5.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(0.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    b.devanagari,
                    fontFamily = DevanagariFont,
                    fontSize = 24.sp,
                    lineHeight = 40.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    b.transliteration,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    lineHeight = 23.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(18.dp))
                Text(b.translation, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(24.dp))
                Text(
                    sectionLabel(b, page),
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

private fun sectionLabel(b: Bhairava, page: Int): String =
    if (b.section == "vowels") {
        "vowel ${page + 1} of 16 · from the Vivṛtti-Vivaraṇa"
    } else {
        "consonant ${page - 15} of 34 · from the Tantrāloka"
    }
