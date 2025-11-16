package com.example.myapplication.mappers

import com.example.myapplication.models.AeroFlightItem
import com.example.myapplication.models.FlightData

fun AeroFlightItem.toFlightData(): FlightData {

    return FlightData(
        flightNumber = this.number ?: "Unknown",

        // Prefer full airport name → fallback to IATA → fallback to ICAO → "--"
        fromAirport = this.departure?.airport?.name
            ?: this.departure?.airport?.iata
            ?: this.departure?.airport?.icao
            ?: "--",

        toAirport = this.arrival?.airport?.name
            ?: this.arrival?.airport?.iata
            ?: this.arrival?.airport?.icao
            ?: "--",

        // Scheduled times
        departureLocal = this.departure?.scheduledTime?.local ?: "--",
        arrivalLocal = this.arrival?.scheduledTime?.local ?: "--",

        // Airline
        airlineName = this.airline?.name ?: "--",

        // Terminal (arrival first, fallback to departure)
        terminal = this.arrival?.terminal
            ?: this.departure?.terminal
            ?: "N/A"
    )
}
