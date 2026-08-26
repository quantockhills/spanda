package com.madhav.bhairava.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madhav.bhairava.data.Bhairava
import com.madhav.bhairava.data.Library
import com.madhav.bhairava.data.Repository
import com.madhav.bhairava.ui.theme.CardBg
import com.madhav.bhairava.ui.theme.Crimson
import com.madhav.bhairava.ui.theme.DevanagariFont
import com.madhav.bhairava.ui.theme.Gold
import com.madhav.bhairava.ui.theme.Ink
import com.madhav.bhairava.ui.theme.Line
import com.madhav.bhairava.ui.theme.MutedInk

@Composable
fun AmrtaListScreen(onBack: () -> Unit, onOpen: (Int) -> Unit) {
    val context = LocalContext.current
    val lib = remember { Repository.library(context) }
    val entries = lib.bhairavas
    val vowels = entries.filter { it.section == "vowels" }
    val rudras = entries.filter { it.section == "rudras" }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ReaderTopBar(title = "Amṛtādistavaḥ", subtitle = "The Fifty Bhairavas of the Alphabet", onBack = onBack)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) { AmrtaIntroCard(lib) }
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader(
                    "The Sixteen Amṛta Bhairavas",
                    "the vowels — from the Īśvara-Pratyabhijñā-Vivṛtti-Vivaraṇa"
                )
            }
            items(vowels.size) { i -> BhairavaCard(vowels[i], onClick = { onOpen(i) }) }
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader(
                    "The Thirty-Four Rudras",
                    "the consonants — from the Tantrāloka"
                )
            }
            items(rudras.size) { i -> BhairavaCard(rudras[i], onClick = { onOpen(vowels.size + i) }) }
        }
    }
}

@Composable
private fun BhairavaCard(b: Bhairava, onClick: () -> Unit) {
    val img = rememberAssetImage("mandala_${b.n.toString().padStart(2, '0')}.jpg")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            Box {
                if (img != null) {
                    Image(
                        bitmap = img,
                        contentDescription = b.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
                Box(
                    Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(1.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        b.phoneme,
                        fontFamily = DevanagariFont,
                        color = Color.White,
                        fontSize = 17.sp
                    )
                }
            }
            Column(Modifier.padding(10.dp)) {
                Text(
                    b.name,
                    fontFamily = DevanagariFont,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    b.nameRoman,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AmrtaIntroCard(lib: Library) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(lib.title, fontFamily = DevanagariFont, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
            Text(
                "Abhinavagupta's hymn, hidden in the maṅgala verses",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(8.dp))
            Text(lib.intro.first(), style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = { expanded = !expanded }) {
                Text(
                    if (expanded) "Less" else "Read the introduction",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    lib.intro.drop(1).forEach { p ->
                        Text(p, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (expanded) "HIDE" else "SHOW",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(lib.mvuTitle, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        lib.mvuText,
                        fontFamily = DevanagariFont,
                        fontSize = 14.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
