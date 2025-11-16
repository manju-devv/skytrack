package com.example.myapplication.models

data class AeroFlightItem(
    val number: String? = null,
    val callSign: String? = null,
    val status: String? = null,
    val airline: AirlineInfo? = null,
    val departure: DepartureInfo? = null,
    val arrival: ArrivalInfo? = null,
    val location: LiveLocation? = null        // <-- YOU MUST HAVE THIS
)

data class AirlineInfo(
    val name: String? = null,
    val iata: String? = null,
    val icao: String? = null
)

data class DepartureInfo(
    val airport: AirportInfo? = null,
    val scheduledTime: TimeInfo? = null,
    val revisedTime: TimeInfo? = null,
    val terminal: String? = null,
    val gate: String? = null
)

data class ArrivalInfo(
    val airport: AirportInfo? = null,
    val scheduledTime: TimeInfo? = null,
    val revisedTime: TimeInfo? = null,
    val terminal: String? = null
)

data class AirportInfo(
    val icao: String? = null,
    val iata: String? = null,
    val name: String? = null,
    val timeZone: String? = null
)

data class TimeInfo(
    val utc: String? = null,
    val local: String? = null
)

data class LiveLocation(
    val lat: Double? = null,
    val lon: Double? = null,
    val altitude: Altitude? = null
)

data class Altitude(
    val meter: Double? = null
)
