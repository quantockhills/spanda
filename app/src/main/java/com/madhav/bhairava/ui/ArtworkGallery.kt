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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/** One piece of booklet artwork shown in the gallery. */
data class Artwork(
    val asset: String,
    val title: String,
    val caption: String
)

val ArtworkGalleryItems = listOf(
    Artwork(
        "art_frontispiece.jpg",
        "Prabhāmaṇḍala",
        "Frontispiece aureole of the title page — golden rays on black, the divine effulgence behind the deities."
    ),
    Artwork(
        "art_sadashiva.jpg",
        "Bronze Sadāśiva",
        "Four-armed deity seated on a lotus, crescent moon and kapālas in hand — title-page bronze."
    ),
    Artwork(
        "art_emblem.jpg",
        "Vimarsha Foundation",
        "The foundation emblem — a lotus maṇḍala of bījas around the third eye. Maṇḍala art by Cibeleh Da Mata."
    ),
    Artwork(
        "art_intro_deity.jpg",
        "Deity of the Introduction",
        "Green-bodied four-armed deity with triśūla, in royal ease beneath a kīrtimukha arch — introduction page."
    ),
    Artwork(
        "art_five_deities.jpg",
        "Register of Five Deities",
        "Pañcāyatana-style registers of enthroned deities with red halos — a painted manuscript plate."
    ),
    Artwork(
        "art_lineage.jpg",
        "Lineage Painting",
        "The lord with two devotees in red robes and an offering-bearer — a guru-lineage / donor portrait."
    ),
    Artwork(
        "art_heruka.jpg",
        "Heruka in Yab-Yum",
        "Brass sculpture of the deity in union with his consort upon the vehicle — colophon plate."
    ),
    Artwork(
        "art_stele.jpg",
        "Bodhisattva Stele",
        "Seated bodhisattva in royal ease within a flaming aureole, attendant figures below."
    ),
    Artwork(
        "art_chakrasamvara.jpg",
        "Cakrasaṃvara Relief",
        "Twelve-armed deity in embrace with Vajravārāhī, ringed by small shrine figures — stone relief."
    ),
    Artwork(
        "art_bodhisattva.jpg",
        "Crowned Bodhisattva",
        "Six-armed crowned bodhisattva in lotus posture, dharmacakra mudrā — stone sculpture."
    ),
    Artwork(
        "art_paubha.jpg",
        "Maṇḍala Painting",
        "Newar-style paubha: central deity ringed by a lotus of smaller deities — the booklet's closing maṇḍala."
    )
)

@Composable
fun ArtworkGallery() {
    var selected by remember { mutableStateOf<Artwork?>(null) }

    Column {
        Text(
            "The Artwork",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "deities and ornaments from the booklet — maṇḍala art by Cibeleh Da Mata",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 4.dp)
        ) {
            items(ArtworkGalleryItems) { art ->
                ArtworkCard(art, onClick = { selected = art })
            }
        }
    }

    selected?.let { art ->
        Dialog(onDismissRequest = { selected = null }) {
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(14.dp)) {
                    val img = rememberAssetImage(art.asset)
                    if (img != null) {
                        Image(
                            bitmap = img,
                            contentDescription = art.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(0.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(art.title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                    Text(art.caption, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            "Close",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { selected = null }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtworkCard(art: Artwork, onClick: () -> Unit) {
    val img = rememberAssetImage(art.asset)
    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (img != null) {
                    Image(
                        bitmap = img,
                        contentDescription = art.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(art.title.take(1), fontFamily = com.madhav.bhairava.ui.theme.DevanagariFont, fontSize = 30.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
            Column(Modifier.padding(10.dp)) {
                Text(
                    art.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    art.caption,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    maxLines = 3,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}
