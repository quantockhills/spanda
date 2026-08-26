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
            lib = parse(sivabodha, amrta)
            return lib!!
        }
    }

    private fun parse(siv: String, amr: String): Library {
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

        return Library(
            title = aj.getString("title"),
            subtitle = aj.getString("subtitle"),
            author = aj.getString("author"),
            intro = intro,
            mvuTitle = mvu.getString("title"),
            mvuText = mvu.getString("text"),
            prologue = prologue,
            stanzas = stanzas,
            bhairavas = bhairavas
        )
    }
}
