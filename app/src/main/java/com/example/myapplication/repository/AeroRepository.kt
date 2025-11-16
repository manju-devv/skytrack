package com.example.myapplication.repository

import android.util.Log
import com.example.myapplication.api.AeroDataBoxService
import com.example.myapplication.mappers.toFlightData
import com.example.myapplication.models.FlightData
import com.google.gson.GsonBuilder

class AeroRepository(private val service: AeroDataBoxService) {

    suspend fun getNearestAirport(lat: Double, lon: Double) =
        service.getNearestAirport(lat, lon)

    suspend fun getFlightsForAirport(icao: String, direction: String?): List<FlightData> {

        val response = service.getAirportFlights(icao)

        // 🔥 PRETTY JSON LOGGER
        val gson = GsonBuilder().setPrettyPrinting().create()
//        Log.d("AERO_JSON", "FULL API RESPONSE:\n${gson.toJson(response)}")

        // 🔥 LOG EACH ARRIVAL ITEM
        response.arrivals.forEach { item ->
//            Log.d("AERO_ITEM", "ARRIVAL ITEM: $item")
        }


        // 🔥 LOG EACH DEPARTURE ITEM
        response.departures.forEach { item ->
            Log.d("AERO_ITEM", "DEPARTURE ITEM: $item")
        }

        val items = when (direction) {
            "Arrival" -> response.arrivals
            "Departure" -> response.departures
            else -> (response.arrivals ?: emptyList()) +
                    (response.departures ?: emptyList())
        }
        items.forEach {
            Log.d("AERO_RAW_ITEM", it.toString())
        }

        return items.map { it.toFlightData() }
    }

    suspend fun getLiveFlightByNumber(airline: String, flightNumber: String) =
        service.getLiveFlightByNumber(airline, flightNumber)
}
