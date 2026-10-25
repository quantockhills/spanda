package com.madhav.bhairava.notify

import android.content.Context
import com.madhav.bhairava.data.Library
import com.madhav.bhairava.data.VowsRepository
import com.madhav.bhairava.ui.flattenSamvarta

data class Meditation(
    val title: String,
    val sanskrit: String,
    val body: String,
    val route: String
)

/** Every verse a reminder can show: Śivabodha, Amṛtādistava, Saṃvarta, and the Vows book. */
fun buildPool(context: Context, lib: Library): List<Meditation> {
    val pool = mutableListOf<Meditation>()
    lib.stanzas.forEach { s ->
        pool += Meditation(
            title = "Śivabodhaviṃśikā — ${s.ordinalRoman} · ${s.nameRoman}",
            sanskrit = s.devanagari,
            body = s.translation,
            route = "sivabodha/${s.n}"
        )
    }
    lib.bhairavas.forEach { b ->
        pool += Meditation(
            title = "Amṛtādistavaḥ — ${b.nameRoman} · ${b.phonemeRoman}",
            sanskrit = b.devanagari,
            body = b.translation,
            route = "amrta/${b.n}"
        )
    }
    flattenSamvarta(lib).forEachIndexed { i, entry ->
        pool += Meditation(
            title = "Saṃvarta Stavaḥ — ${entry.label}",
            sanskrit = entry.verse.devanagari,
            body = entry.verse.translation,
            route = "samvarta/$i"
        )
    }
    VowsRepository.load(context).entries.forEachIndexed { i, e ->
        if (e.translation.isBlank()) return@forEachIndexed
        pool += Meditation(
            title = if (e.title.isBlank()) "Vows — ${e.ref}" else "${e.ref} · ${e.title}",
            sanskrit = e.devanagari.ifBlank { e.transliteration },
            body = e.translation,
            route = "vows/$i"
        )
    }
    return pool
}

fun Meditation.notificationBody(): String =
    if (body.length > 300) body.substring(0, 297) + "…" else body
