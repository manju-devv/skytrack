package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.models.FlightData
import com.example.myapplication.utils.TimeUtils

@Composable
fun FlightDetailsScreen(flight: FlightData, onBack: () -> Unit) {

    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF020024),
            Color(0xFF090979),
            Color(0xFF00D4FF)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()     // ⭐ FIX TOP OVERLAP
            .padding(16.dp)
    ) {

        // ⭐ TOP BAR
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(10.dp))
            Text("Flight Details", color = Color.White, fontSize = 26.sp)
        }

        Spacer(Modifier.height(20.dp))

        // ⭐ MAIN CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(Color(0xFF0A1A40)),
            shape = RoundedCornerShape(20.dp)
        ) {

            Column(Modifier.padding(20.dp)) {

                Text(
                    text = flight.flightNumber,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(20.dp))

                // ⭐ ROUTE ROW
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(Icons.Filled.FlightTakeoff, null, tint = Color(0xFF00D4FF))
                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = flight.fromAirport ?: "--",
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.width(10.dp))
                    Text("→", color = Color.White)
                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = flight.toAirport ?: "--",
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.width(10.dp))
                    Icon(Icons.Filled.FlightLand, null, tint = Color(0xFF00D4FF))
                }

                Spacer(Modifier.height(20.dp))

                // ⭐ DETAILS
                DetailRow("Airline", flight.airlineName)
                DetailRow("Terminal", flight.terminal)
                DetailRow("Departure Time", TimeUtils.formatTime(flight.departureLocal))
                DetailRow("Arrival Time", TimeUtils.formatTime(flight.arrivalLocal))
            }
        }

        Spacer(Modifier.height(25.dp))

        Text("Live Data", color = Color.White, fontSize = 24.sp)

//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 12.dp),
//            colors = CardDefaults.cardColors(Color(0xFF0A1A40)),
//            shape = RoundedCornerShape(20.dp)
//        ) {
//
//            Column(Modifier.padding(20.dp)) {
//                DetailRowIcon("Latitude", flight.latitude?.toString(), Icons.Default.LocationOn)
//                DetailRowIcon("Longitude", flight.longitude?.toString(), Icons.Default.LocationOn)
//                DetailRowIcon("Altitude (ft)", flight.altitudeFeet?.toString(), Icons.Default.Speed)
//                DetailRowIcon("Speed (km/h)", flight.speedKmh?.toString(), Icons.Default.Speed)
//            }
//        }
    }
}

// ⭐⭐ REUSABLE ROW COMPONENT — MUST BE OUTSIDE ABOVE FUNCTION ⭐⭐
@Composable
fun DetailRow(title: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = Color(0xFF00D4FF))
        Text(value ?: "--", color = Color.White)
    }
}

@Composable
fun DetailRowIcon(title: String, value: String?, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF00D4FF))
        Spacer(Modifier.width(10.dp))
        Text(title, color = Color.White)
        Spacer(Modifier.weight(1f))
        Text(value ?: "--", color = Color.White)
    }
}
