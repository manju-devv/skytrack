package com.example.myapplication.api

import com.example.myapplication.models.AirportNearbyResponse
import com.example.myapplication.models.AeroFlightsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AeroDataBoxService {

    // Working nearest airport alternative (search by location)
    @GET("airports/search/location/{lat}/{lon}/km/{radius}/16")
    suspend fun getNearestAirport(
        @Path("lat") lat: Double,
        @Path("lon") lon: Double,
        @Path("radius") radius: Int = 50
    ): AirportNearbyResponse

    // Arrivals
    @GET("flights/airports/icao/{icao}")
    suspend fun getAirportFlights(
        @Path("icao") icao: String,
        @Query("offsetMinutes") offsetMinutes: Int = -120,
        @Query("durationMinutes") durationMinutes: Int = 720,
        @Query("withLeg") withLeg: Boolean = true,
        @Query("direction") direction: String = "Both",
        @Query("withCancelled") withCancelled: Boolean = true,
        @Query("withCodeshared") withCodeshared: Boolean = true,
        @Query("withCargo") withCargo: Boolean = true,
        @Query("withPrivate") withPrivate: Boolean = true,
        @Query("withLocation") withLocation: Boolean = false
    ): AeroFlightsResponse

    // Live flight
    @GET("flights/number/{airline}/{flightNumber}")
    suspend fun getLiveFlightByNumber(
        @Path("airline") airline: String,
        @Path("flightNumber") flightNumber: String
    ): AeroFlightsResponse
}
