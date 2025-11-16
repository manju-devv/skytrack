package com.example.myapplication.models

data class LiveFlightResponse(
    val items: List<AeroFlightItem> = emptyList()
)
