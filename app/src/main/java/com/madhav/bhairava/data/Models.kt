package com.madhav.bhairava.data

data class Stanza(
    val n: Int,
    val ordinal: String,
    val ordinalRoman: String,
    val name: String,
    val nameRoman: String,
    val subtitleEn: String,
    val devanagari: String,
    val transliteration: String,
    val wordByWord: String,
    val translation: String
)

data class Bhairava(
    val n: Int,
    val section: String,
    val phoneme: String,
    val phonemeRoman: String,
    val name: String,
    val nameRoman: String,
    val devanagari: String,
    val transliteration: String,
    val translation: String
)

data class OpeningVerse(
    val n: Int,
    val devanagari: String,
    val transliteration: String,
    val wordByWord: String,
    val translation: String
)

data class GitaVerse(
    val label: String,
    val sanskrit: String,
    val transliteration: String,
    val translation: String,
    val commentary: String
)

data class GitaChapter(
    val n: Int,
    val name: String,
    val nameRoman: String,
    val meaning: String,
    val intro: String,
    val verses: List<GitaVerse>
)

data class Library(
    val title: String,
    val subtitle: String,
    val author: String,
    val intro: List<String>,
    val mvuTitle: String,
    val mvuText: String,
    val prologue: List<OpeningVerse>,
    val stanzas: List<Stanza>,
    val bhairavas: List<Bhairava>,
    val gita: List<GitaChapter>,
    val samvartaTitle: String = "",
    val samvartaTitleRoman: String = "",
    val samvartaSubtitle: String = "",
    val samvartaAuthor: String = "",
    val samvartaCredits: String = "",
    val samvartaIntro: List<String> = emptyList(),
    val samvartaOpening: SamvartaVerse? = null,
    val samvartaSections: List<SamvartaSection> = emptyList(),
    val samvartaColophon: SamvartaColophon? = null
)

data class SamvartaVerse(
    val n: Int,
    val devanagari: String,
    val transliteration: String,
    val translation: String,
    val image: String? = null
)

data class SamvartaSection(
    val name: String,
    val nameRoman: String,
    val subtitleEn: String,
    val image: String? = null,
    val verses: List<SamvartaVerse>,
    val closing: SamvartaVerse? = null
)

data class SamvartaColophon(
    val note: String,
    val noteRoman: String,
    val noteEn: String,
    val devanagari: String,
    val transliteration: String,
    val translation: String
)
