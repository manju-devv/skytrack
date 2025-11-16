package com.example.myapplication.utils

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object TimeUtils {

    fun formatTime(raw: String?): String {
        if (raw.isNullOrBlank() || raw == "--") return "--"

        return try {
            // FIX AERODATABOX FORMAT (SPACE → T)
            val fixedRaw = raw.replace(" ", "T")

            val input = OffsetDateTime.parse(fixedRaw)
            val outputFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a")
            input.format(outputFormat)

        } catch (e: Exception) {
            "--"
        }
    }
}
