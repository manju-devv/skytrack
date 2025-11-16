package com.example.myapplication.utils

fun parseFlightIdent(ident: String?): Pair<String, String> {
    if (ident.isNullOrEmpty()) return "" to ""
    // common ident formats: "AI2757", "AI 2757", "AAL1234"
    val cleaned = ident.replace(" ", "")
    val idx = cleaned.indexOfFirst { it.isDigit() }
    return if (idx <= 0) {
        "" to cleaned
    } else {
        val airline = cleaned.substring(0, idx)
        val number = cleaned.substring(idx)
        airline to number
    }
}
