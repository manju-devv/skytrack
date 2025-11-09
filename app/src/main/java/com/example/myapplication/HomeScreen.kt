package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit = {}) {
    var showWelcome by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1565C0), Color(0xFF42A5F5))
    )

    val recentFlights = listOf(
        Flight("HYD → DEL", "On Time", "A320", "12:45 PM"),
        Flight("DEL → MAA", "Delayed", "B737", "3:10 PM"),
        Flight("MAA → BLR", "On Time", "ATR 72", "7:30 PM"),
    )

    var selectedFlight by remember { mutableStateOf<Flight?>(null) }

    LaunchedEffect(Unit) {
        delay(800)
        showWelcome = false
        snackbarHostState.showSnackbar("🛫 Welcome aboard Sky Track!")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sky Track", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1565C0)),
                actions = {
                    val context = LocalContext.current
                    val userPreferences = remember { UserPreferences(context) }

                    TextButton(onClick = {
                        coroutineScope.launch {
                            userPreferences.setLoggedIn(false)
                            onLogout()
                        }
                    }) {
                        Text("Logout", color = Color.White)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(innerPadding)
        ) {
            if (showWelcome) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Loading Sky Track...", color = Color.White, fontSize = 18.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    item {
                        Text(
                            "Welcome to Sky Track,",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Track your flights and performance in real time.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        StatsSection()
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "Recent Flights",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(recentFlights) { flight ->
                        FlightCard(flight) { selectedFlight = flight }
                    }
                }
            }

            // Overlay when a flight is clicked
            selectedFlight?.let { flight ->
                FlightDetailsDialog(flight) {
                    selectedFlight = null
                }
            }
        }
    }
}

@Composable
fun StatsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatCard(title = "Flights Today", value = "3", modifier = Modifier.weight(1f))
        StatCard(title = "On-Time Rate", value = "92%", modifier = Modifier.weight(1f))
        StatCard(title = "Avg Delay", value = "6 min", modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FlightCard(flight: Flight, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    flight.route,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF333333)
                )
                Text(flight.aircraft, color = Color.Gray, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    flight.time,
                    color = Color(0xFF1565C0),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    flight.status,
                    color = if (flight.status == "On Time") Color(0xFF27AE60) else Color(0xFFE74C3C),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun FlightDetailsDialog(flight: Flight, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF1565C0))
            }
        },
        title = {
            Text("Flight Details", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text("✈️ Route: ${flight.route}")
                Text("🛫 Aircraft: ${flight.aircraft}")
                Text("🕒 Departure Time: ${flight.time}")
                Text("📡 Status: ${flight.status}")
                Text("🧭 Compass Heading: 230° SW")
                Text("📍 GPS Location: 17.3850° N, 78.4867° E")
                Text("🛬 Nearest Airport: Rajiv Gandhi Intl (HYD)")
            }
        }
    )
}

data class Flight(
    val route: String,
    val status: String,
    val aircraft: String,
    val time: String
)
