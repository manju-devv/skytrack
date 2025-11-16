package com.example.myapplication.models

data class AeroFlightsResponse(
    val arrivals: List<AeroFlightItem> = emptyList(),
    val departures: List<AeroFlightItem> = emptyList()
)
