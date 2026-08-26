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

data class Library(
    val title: String,
    val subtitle: String,
    val author: String,
    val intro: List<String>,
    val mvuTitle: String,
    val mvuText: String,
    val prologue: List<OpeningVerse>,
    val stanzas: List<Stanza>,
    val bhairavas: List<Bhairava>
)
