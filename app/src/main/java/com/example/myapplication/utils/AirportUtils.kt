package com.example.myapplication.utils

import kotlin.math.*

data class Airport(val iata: String, val lat: Double, val lon: Double)

// Add major Indian airports (you can add more later)
val airports = listOf(
    Airport("BLR", 13.1986, 77.7066),
    Airport("DEL", 28.5562, 77.1000),
    Airport("BOM", 19.0896, 72.8656),
    Airport("HYD", 17.24, 78.428),
    Airport("MAA", 12.9941, 80.1709),
    Airport("CCU", 22.6547, 88.4467)
)

fun nearestAirport(lat: Double, lon: Double): String {
    var minDist = Double.MAX_VALUE
    var nearest = "DEL"

    for (a in airports) {
        val dist = haversine(lat, lon, a.lat, a.lon)
        if (dist < minDist) {
            minDist = dist
            nearest = a.iata
        }
    }
    return nearest
}

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)

    return 2 * R * asin(sqrt(a))
}
