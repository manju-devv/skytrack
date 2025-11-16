package com.example.myapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
//data class FlightData(
//    val number: String?,
//    val airline: String?,
//    val depIata: String?,
//    val arrIata: String?,
//    val depAirport: String?,
//    val arrAirport: String?,
//    val departureTime: String?,
//    val arrivalTime: String?,
//    val latitude: Double?,
//    val longitude: Double?,
//    val altitude: Double?
//) : Parcelable

data class FlightData(
    val flightNumber: String,
    val fromAirport: String?,
    val toAirport: String?,
    val departureLocal: String?,
    val arrivalLocal: String?,
    val airlineName: String?,
    val terminal: String?
) :Parcelable

