package com.madhav.bhairava.data

import android.content.Context
import org.json.JSONObject

object Repository {

    @Volatile
    private var lib: Library? = null

    fun library(context: Context): Library {
        lib?.let { return it }
        synchronized(this) {
            lib?.let { return it }
            val sivabodha = context.assets.open("sivabodha.json").bufferedReader().use { it.readText() }
            val amrta = context.assets.open("amrta.json").bufferedReader().use { it.readText() }
            val gita = context.assets.open("gita.json").bufferedReader().use { it.readText() }
            val samvarta = context.assets.open("samvarta.json").bufferedReader().use { it.readText() }
            lib = parse(sivabodha, amrta, gita, samvarta)
            return lib!!
        }
    }

    private fun parse(siv: String, amr: String, git: String, svt: String): Library {
        val sj = JSONObject(siv)
        val aj = JSONObject(amr)

        val stanzas = sj.getJSONArray("stanzas").let { arr ->
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Stanza(
                    n = o.getInt("n"),
                    ordinal = o.getString("ordinal"),
                    ordinalRoman = o.getString("ordinal_roman"),
                    name = o.getString("name"),
                    nameRoman = o.getString("name_roman"),
                    subtitleEn = o.optString("subtitle_en", ""),
                    devanagari = o.getString("devanagari"),
                    transliteration = o.getString("transliteration"),
                    wordByWord = o.getString("word_by_word"),
                    translation = o.getString("translation")
                )
            }
        }

        val prologue = sj.optJSONArray("prologue")?.let { arr ->
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                OpeningVerse(
                    n = o.getInt("n"),
                    devanagari = o.getString("devanagari"),
                    transliteration = o.getString("transliteration"),
                    wordByWord = o.getString("word_by_word"),
                    translation = o.getString("translation")
                )
            }
        } ?: emptyList()

        val bhairavas = aj.getJSONArray("entries").let { arr ->
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Bhairava(
                    n = o.getInt("n"),
                    section = o.getString("section"),
                    phoneme = o.getString("phoneme"),
                    phonemeRoman = o.getString("phoneme_roman"),
                    name = o.getString("name"),
                    nameRoman = o.getString("name_roman"),
                    devanagari = o.getString("devanagari"),
                    transliteration = o.getString("transliteration"),
                    translation = o.getString("translation")
                )
            }
        }

        val intro = aj.getJSONArray("intro").let { arr ->
            (0 until arr.length()).map { arr.getString(it) }
        }
        val mvu = aj.getJSONObject("mvu")

        val gitaChapters = JSONObject(git).getJSONArray("chapters").let { arr ->
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                val verses = o.getJSONArray("verses").let { va ->
                    (0 until va.length()).map { j ->
                        val vo = va.getJSONObject(j)
                        GitaVerse(
                            label = vo.getString("label"),
                            sanskrit = vo.optString("sanskrit", ""),
                            transliteration = vo.optString("transliteration", ""),
                            translation = vo.getString("translation"),
                            commentary = vo.optString("commentary", "")
                        )
                    }
                }
                GitaChapter(
                    n = o.getInt("n"),
                    name = o.optString("name", ""),
                    nameRoman = o.optString("nameRoman", ""),
                    meaning = o.optString("meaning", ""),
                    intro = o.optString("intro", ""),
                    verses = verses
                )
            }
        }

        // Saṃvarta Stavaḥ
        val sj2 = JSONObject(svt)
        fun parseVerse(o: JSONObject): SamvartaVerse = SamvartaVerse(
            n = o.getInt("n"),
            devanagari = o.getString("devanagari"),
            transliteration = o.optString("transliteration", ""),
            translation = o.getString("translation"),
            image = if (o.has("image")) o.getString("image") else null
        )
        val samvartaSections = sj2.getJSONArray("sections").let { arr ->
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                val verses = o.getJSONArray("verses").let { va ->
                    (0 until va.length()).map { j -> parseVerse(va.getJSONObject(j)) }
                }
                SamvartaSection(
                    name = o.getString("name"),
                    nameRoman = o.getString("name_roman"),
                    subtitleEn = o.optString("subtitle_en", ""),
                    image = if (o.has("image")) o.getString("image") else null,
                    verses = verses,
                    closing = if (o.has("closing") && !o.isNull("closing")) parseVerse(o.getJSONObject("closing")) else null
                )
            }
        }
        val samvartaOpening = if (sj2.has("opening") && !sj2.isNull("opening")) {
            parseVerse(sj2.getJSONObject("opening"))
        } else null
        val samvartaColophon = if (sj2.has("colophon") && !sj2.isNull("colophon")) {
            val o = sj2.getJSONObject("colophon")
            SamvartaColophon(
                note = o.optString("note", ""),
                noteRoman = o.optString("note_roman", ""),
                noteEn = o.optString("note_en", ""),
                devanagari = o.optString("devanagari", ""),
                transliteration = o.optString("transliteration", ""),
                translation = o.optString("translation", "")
            )
        } else null
        val samvartaIntro = sj2.optJSONArray("intro")?.let { arr ->
            (0 until arr.length()).map { arr.getString(it) }
        } ?: emptyList()

        return Library(
            title = aj.getString("title"),
            subtitle = aj.getString("subtitle"),
            author = aj.getString("author"),
            intro = intro,
            mvuTitle = mvu.getString("title"),
            mvuText = mvu.getString("text"),
            prologue = prologue,
            stanzas = stanzas,
            bhairavas = bhairavas,
            gita = gitaChapters,
            samvartaTitle = sj2.getString("title"),
            samvartaTitleRoman = sj2.getString("title_roman"),
            samvartaSubtitle = sj2.getString("subtitle"),
            samvartaAuthor = sj2.getString("author"),
            samvartaCredits = sj2.optString("credits", ""),
            samvartaIntro = samvartaIntro,
            samvartaOpening = samvartaOpening,
            samvartaSections = samvartaSections,
            samvartaColophon = samvartaColophon
        )
    }
}
