package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.location.LocationHelper
import com.example.myapplication.models.FlightData
import com.example.myapplication.viewmodel.FlightsViewModel
import android.util.Log

@Composable
fun FlightsScreen(
    viewModel: FlightsViewModel,
    onFlightClick: (FlightData) -> Unit
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    var currentAirport by remember { mutableStateOf<String?>(null) }

    val flights by viewModel.flights.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        val loc = locationHelper.getUserLocation()

        if (loc != null) {
            try {
                val nearby = viewModel.repoPublic.getNearestAirport(loc.first, loc.second)
                val airportIcao = nearby.items?.firstOrNull()?.icao

                Log.d("AERODATABOX", "Nearest airport = $airportIcao")

                currentAirport = airportIcao
                viewModel.loadArrivals("EGLL")



                if (airportIcao != null) {
                    viewModel.loadArrivals(airportIcao)
                } else {
                    Log.e("AERODATABOX", "No ICAO returned from API")
                }
            } catch (e: Exception) {
                Log.e("AERODATABOX", "❌ API FAILED: ${e.message}")
            }
        }
    }

    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF020024),
            Color(0xFF090979),
            Color(0xFF00D4FF)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
    ) {

        if (loading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column {

                Text(
                    "Available Flights",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )

                Row(Modifier.padding(16.dp)) {

                    Button(
                        onClick = {
                            currentAirport?.let { viewModel.loadArrivals(it) }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Arrivals")
                    }

                    Spacer(Modifier.width(12.dp))

                    Button(
                        onClick = {
                            currentAirport?.let { viewModel.loadDepartures(it) }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Departures")
                    }
                }

                LazyColumn {
                    items(flights) { flight ->
                        FlightCard(flight) { onFlightClick(flight) }
                    }
                }
            }
        }
    }
}
