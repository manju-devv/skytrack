package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.FlightLand
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.models.FlightData
import com.example.myapplication.utils.TimeUtils

@Composable
fun FlightCard(
    flight: FlightData,
    onClick: () -> Unit

) {
    Log.d("AERO_ITEMHi", "DEPARTURE ITEM: $flight")
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xAA0A1A40)
        ),
        elevation = CardDefaults.cardElevation(6.dp)

    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            // Flight Number

            Text(
                text = flight.flightNumber ?: "Unknown Flight",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ROUTE
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(Icons.Default.FlightTakeoff, null, tint = Color(0xFF00D4FF))
                Spacer(Modifier.width(6.dp))

                Text(
                    text = flight.fromAirport ?: "--",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(6.dp))
                Text("→", color = Color.White)
                Spacer(Modifier.width(6.dp))

                Icon(Icons.Default.FlightLand, null, tint = Color(0xFF00D4FF))
                Spacer(Modifier.width(6.dp))

                Text(
                    text = flight.toAirport ?: "--",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Airline + Departure time
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text("Airline", color = Color(0xFF00D4FF))
                    Text(
                        text = flight.airlineName ?: "--",
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column {
                    Text("Depart", color = Color(0xFF00D4FF))
                    Text(
                        text = TimeUtils.formatTime(flight.departureLocal),
                        color = Color.White
                    )
                }
            }
        }
    }
}
