package com.example.myapplication.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

class LocationHelper(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fun isLocationEnabled(): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    suspend fun getUserLocation(): Pair<Double, Double>? {
        return try {

            // 1️⃣ Try real-time GPS fix
            val fresh = fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()

            if (fresh != null) {
                Log.d("LOCATION_HELPER", "Fresh GPS: ${fresh.latitude}, ${fresh.longitude}")
                return Pair(fresh.latitude, fresh.longitude)
            }

            // 2️⃣ Fallback to last known location
            val last = fusedClient.lastLocation.await()
            if (last != null) {
                Log.d("LOCATION_HELPER", "Last known: ${last.latitude}, ${last.longitude}")
                return Pair(last.latitude, last.longitude)
            }

            Log.e("LOCATION_HELPER", "Both GPS & lastLocation failed ❌")
            null

        } catch (e: Exception) {
            Log.e("LOCATION_HELPER", "Error: ${e.message}")
            null
        }
    }

}
