package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.ApiClient
import com.example.myapplication.models.FlightData
import com.example.myapplication.repository.AeroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FlightsViewModel(apiKey: String) : ViewModel() {

    private val service = ApiClient.create(apiKey)
    private val repo = AeroRepository(service)
    val repoPublic get() = repo

    private val _flights = MutableStateFlow<List<FlightData>>(emptyList())
    val flights: StateFlow<List<FlightData>> = _flights

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadArrivals(icao: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val flightsList = repo.getFlightsForAirport(icao, "Arrival")
                _flights.value = flightsList
            } catch (e: Exception) {
                _error.value = e.message
            }
            _loading.value = false
        }
    }

    fun loadDepartures(icao: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val flightsList = repo.getFlightsForAirport(icao, "Departure")
                _flights.value = flightsList
            } catch (e: Exception) {
                _error.value = e.message
            }
            _loading.value = false
        }
    }

    fun loadFlightsByLocation(lat: Double, lon: Double, direction: String?) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val nearby = repo.getNearestAirport(lat, lon)
                val icao = nearby.items?.firstOrNull()?.icao

                if (icao == null) {
                    _error.value = "No nearby airport found"
                    _flights.value = emptyList()
                } else {
                    val flightsList = repo.getFlightsForAirport(icao, direction)
                    _flights.value = flightsList
                }
            } catch (e: Exception) {
                _error.value = e.message
                _flights.value = emptyList()
            }
            _loading.value = false
        }
    }
}
