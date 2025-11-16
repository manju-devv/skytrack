package com.example.myapplication.models

data class AirportNearbyResponse(
    val searchBy: SearchBy?,
    val count: Int?,
    val items: List<NearbyAirportItem>?
)

data class SearchBy(
    val lat: Double?,
    val lon: Double?
)

data class NearbyAirportItem(
    val icao: String?,
    val iata: String?,
    val name: String?,
    val shortName: String?,
    val municipalityName: String?,
    val location: AirportLocation?
)

data class AirportLocation(
    val lat: Double?,
    val lon: Double?
)
