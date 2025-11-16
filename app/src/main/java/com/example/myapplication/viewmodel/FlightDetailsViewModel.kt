package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.ApiClient
import com.example.myapplication.mappers.toFlightData
import com.example.myapplication.models.FlightData
import com.example.myapplication.repository.AeroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FlightDetailsViewModel(apiKey: String) : ViewModel() {

    private val service = ApiClient.create(apiKey)
    private val repo = AeroRepository(service)

    private val _flight = MutableStateFlow<FlightData?>(null)
    val flight: StateFlow<FlightData?> = _flight

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadLiveFlight(airlineCode: String, flightNumber: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val resp = repo.getLiveFlightByNumber(airlineCode, flightNumber)

                // Combine arrivals + departures because API returns both
                val aero = (resp.arrivals + resp.departures).firstOrNull()

                if (aero == null) {
                    _error.value = "No live data for this flight"
                    _flight.value = null
                } else {
                    _flight.value = aero.toFlightData()
                }

            } catch (t: Throwable) {
                _error.value = t.message ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

}
