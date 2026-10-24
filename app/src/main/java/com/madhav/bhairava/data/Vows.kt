package com.madhav.bhairava.data

import android.content.Context
import org.json.JSONObject

data class VowEntry(
    val ref: String,
    val title: String,
    val devanagari: String,
    val sutraIast: String,
    val introEn: String,
    val sutraEn: String,
    val commentaryEn: String,
    val introSa: String,
    val commentarySa: String
)

data class VowGroup(
    val name: String,
    val nameRoman: String,
    val subtitle: String,
    val entries: List<VowEntry>
)

data class VowsBook(
    val title: String,
    val subtitle: String,
    val source: String,
    val groups: List<VowGroup>
)

object VowsRepository {
    @Volatile
    private var cached: VowsBook? = null

    fun load(context: Context): VowsBook {
        cached?.let { return it }
        synchronized(this) {
            cached?.let { return it }
            val text = context.assets.open("vows.json").bufferedReader().use { it.readText() }
            val o = JSONObject(text)
            val groups = o.getJSONArray("groups").let { ga ->
                (0 until ga.length()).map { gi ->
                    val g = ga.getJSONObject(gi)
                    val entries = g.getJSONArray("entries").let { ea ->
                        (0 until ea.length()).map { ei ->
                            val e = ea.getJSONObject(ei)
                            VowEntry(
                                ref = e.getString("ref"),
                                title = e.getString("title"),
                                devanagari = e.getString("devanagari"),
                                sutraIast = e.optString("sutra_iast", ""),
                                introEn = e.optString("intro_en", "").let { if (it == "null") "" else it },
                                sutraEn = e.getString("sutra_en"),
                                commentaryEn = e.getString("commentary_en"),
                                introSa = e.optString("intro_sa", ""),
                                commentarySa = e.optString("commentary_sa", "")
                            )
                        }
                    }
                    VowGroup(
                        name = g.getString("name"),
                        nameRoman = g.optString("name_roman", ""),
                        subtitle = g.optString("subtitle", ""),
                        entries = entries
                    )
                }
            }
            val book = VowsBook(
                title = o.getString("title"),
                subtitle = o.optString("subtitle", ""),
                source = o.optString("source", ""),
                groups = groups
            )
            cached = book
            return book
        }
    }
}
