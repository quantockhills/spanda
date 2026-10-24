package com.madhav.bhairava.data

import android.content.Context
import org.json.JSONObject

/**
 * One verse-level entry, same shape as Stanza/Bhairava so the reader, notes and
 * favourites work the same way. Routes use the 0-based position in [VowsBook.entries].
 */
data class VowEntry(
    val n: Int,
    val ref: String,
    val title: String,
    val devanagari: String,
    val transliteration: String,
    val wordByWord: String,
    val translation: String,
    val introEn: String,
    val commentaryEn: String,
    val commentarySa: String
)

data class VowGroup(
    val name: String,
    val nameRoman: String,
    val subtitleEn: String,
    val entries: List<VowEntry>
)

data class VowsBook(
    val title: String,
    val subtitle: String,
    val source: String,
    val groups: List<VowGroup>
) {
    val entries: List<VowEntry> get() = groups.flatMap { it.entries }
}

object VowsRepository {
    @Volatile
    private var cached: VowsBook? = null

    fun load(context: Context): VowsBook {
        cached?.let { return it }
        synchronized(this) {
            cached?.let { return it }
            val o = JSONObject(context.assets.open("vows.json").bufferedReader().use { it.readText() })
            val ga = o.getJSONArray("groups")
            val groups = (0 until ga.length()).map { gi ->
                val g = ga.getJSONObject(gi)
                val ea = g.getJSONArray("entries")
                VowGroup(
                    name = g.str("name"),
                    nameRoman = g.str("name_roman"),
                    subtitleEn = g.str("subtitle_en"),
                    entries = (0 until ea.length()).map { ei ->
                        val e = ea.getJSONObject(ei)
                        VowEntry(
                            n = e.optInt("n", ei + 1),
                            ref = e.str("ref"),
                            title = e.str("title"),
                            devanagari = e.str("devanagari"),
                            transliteration = e.str("transliteration"),
                            wordByWord = e.str("word_by_word"),
                            translation = e.str("translation"),
                            introEn = e.str("intro_en"),
                            commentaryEn = e.str("commentary_en"),
                            commentarySa = e.str("commentary_sa")
                        )
                    }
                )
            }
            val book = VowsBook(
                title = o.str("title"),
                subtitle = o.str("subtitle"),
                source = o.str("source"),
                groups = groups
            )
            cached = book
            return book
        }
    }

    fun entry(context: Context, index: Int): VowEntry? = load(context).entries.getOrNull(index)
}

private fun JSONObject.str(key: String): String =
    optString(key, "").takeUnless { it == "null" } ?: ""
