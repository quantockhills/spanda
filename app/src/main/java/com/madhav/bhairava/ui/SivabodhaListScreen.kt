package com.madhav.bhairava.ui

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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import com.madhav.bhairava.data.OpeningVerse
import com.madhav.bhairava.data.Repository
import com.madhav.bhairava.data.Stanza
import com.madhav.bhairava.ui.theme.CardBg
import com.madhav.bhairava.ui.theme.Crimson
import com.madhav.bhairava.ui.theme.DevanagariFont
import com.madhav.bhairava.ui.theme.Gold
import com.madhav.bhairava.ui.theme.GoldLight
import com.madhav.bhairava.ui.theme.Ink
import com.madhav.bhairava.ui.theme.Line
import com.madhav.bhairava.ui.theme.MutedInk

@Composable
fun SivabodhaListScreen(onBack: () -> Unit, onOpen: (Int) -> Unit) {
    val context = LocalContext.current
    val lib = remember { Repository.library(context) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ReaderTopBar(title = "Śivabodhaviṃśikā", subtitle = "Twenty Contemplations on the Realization of Śiva", onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (lib.prologue.isNotEmpty()) {
                item { PrologueCard(lib.prologue) }
            }
            item {
                SectionHeader(
                    "The Twenty Contemplations",
                    "each stanza is a bhāvanā — a visualization — given here in order"
                )
            }
            itemsIndexed(lib.stanzas) { i, s ->
                StanzaRow(s, onClick = { onOpen(i) })
            }
        }
    }
}

@Composable
private fun StanzaRow(s: Stanza, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("${s.n}", fontFamily = com.madhav.bhairava.ui.theme.SerifFont, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White, fontSize = 16.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(s.name, fontFamily = DevanagariFont, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(s.ordinal, fontFamily = DevanagariFont, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
                Text(
                    "${s.nameRoman} — ${s.ordinalRoman}",
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    s.translation,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun PrologueCard(verses: List<OpeningVerse>) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Opening verses", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Text("the doctrinal couplet · verses 1–2 of the booklet", style = MaterialTheme.typography.labelSmall)
                }
                Icon(
                    if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    verses.forEach { v ->
                        Spacer(Modifier.height(14.dp))
                        Text(v.devanagari, fontFamily = DevanagariFont, fontSize = 19.sp, lineHeight = 32.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(6.dp))
                        Text(v.transliteration, fontStyle = FontStyle.Italic, fontSize = 13.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text(v.translation, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Then follow the twenty contemplations…", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
