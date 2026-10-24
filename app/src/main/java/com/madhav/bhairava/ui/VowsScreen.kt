package com.madhav.bhairava.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madhav.bhairava.data.VowEntry
import com.madhav.bhairava.data.VowsRepository
import com.madhav.bhairava.ui.theme.DevanagariFont

@Composable
fun VowsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val book = remember { VowsRepository.load(context) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ReaderTopBar(title = book.title, subtitle = book.subtitle, onBack = onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            book.groups.forEach { group ->
                item {
                    Column(Modifier.padding(top = 10.dp)) {
                        Text(
                            group.name.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (group.subtitle.isNotEmpty()) {
                            Text(group.subtitle, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                group.entries.forEach { entry ->
                    item { VowCard(entry) }
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
private fun VowCard(e: VowEntry) {
    var open by remember { mutableStateOf(false) }
    var showSanskrit by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { open = !open },
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(e.ref, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(e.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                e.devanagari,
                fontFamily = DevanagariFont,
                fontSize = 18.sp,
                lineHeight = 30.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(e.sutraEn, style = MaterialTheme.typography.bodyMedium)
            if (open) {
                Spacer(Modifier.height(12.dp))
                Text("VYĀSA'S COMMENTARY", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(4.dp))
                if (e.introEn.isNotEmpty()) {
                    Text(e.introEn, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(6.dp))
                }
                Text(e.commentaryEn, style = MaterialTheme.typography.bodyMedium)
                if (e.commentarySa.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        if (showSanskrit) "Hide Sanskrit (IAST)" else "Show Sanskrit (IAST)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { showSanskrit = !showSanskrit }
                    )
                    if (showSanskrit) {
                        Spacer(Modifier.height(6.dp))
                        val head = listOf(e.introSa, e.sutraIast).filter { it.isNotEmpty() }.joinToString("\n")
                        if (head.isNotEmpty()) Text(head, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(4.dp))
                        Text(e.commentarySa, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Translation: J. H. Woods, 1914",
                    style = MaterialTheme.typography.labelSmall
                )
            } else {
                Spacer(Modifier.height(6.dp))
                Text("Tap for Vyāsa's commentary", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
