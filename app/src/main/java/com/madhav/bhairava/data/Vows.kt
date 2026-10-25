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
    val commentarySa: String,
    /** Heading over this verse's commentary; set from its group. */
    val commentaryLabel: String = VOWS_COMMENTARY_LABEL
)

data class VowGroup(
    val name: String,
    val nameRoman: String,
    val subtitleEn: String,
    val entries: List<VowEntry>,
    /** Optional artwork file in assets, shown above the group in the list. */
    val imageAsset: String = "",
    /** Teacher's introduction, shown once above the group's first verse. */
    val teachingsEn: String = "",
    val commentaryLabel: String = VOWS_COMMENTARY_LABEL
)

const val VOWS_COMMENTARY_LABEL = "VYĀSA'S COMMENTARY · WOODS, 1914"

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
                val label = g.str("commentary_label").ifEmpty { VOWS_COMMENTARY_LABEL }
                VowGroup(
                    name = g.str("name"),
                    nameRoman = g.strOr("name_roman", "nameRoman"),
                    subtitleEn = g.strOr("subtitle_en", "subtitleEn"),
                    imageAsset = g.str("image"),
                    teachingsEn = g.str("teachings_en"),
                    commentaryLabel = label,
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
                            commentarySa = e.str("commentary_sa"),
                            commentaryLabel = label
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

/** First non-empty value among the keys; the file uses both snake_case and camelCase. */
private fun JSONObject.strOr(vararg keys: String): String =
    keys.map { str(it) }.firstOrNull { it.isNotEmpty() } ?: ""
