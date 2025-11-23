@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

data class FlightResponse(val departures: List<FlightItem>?)
data class FlightItem(val number: String?, val airline: AirlineInfo?, val status: String?, val departure: DepartureInfo?, val arrival: ArrivalInfo?)
data class AirlineInfo(val name: String?)
data class DepartureInfo(val scheduledTimeLocal: String? = null, val airport: AirportShortInfo? = null)
data class ArrivalInfo(val scheduledTimeLocal: String? = null, val airport: AirportShortInfo? = null)
data class AirportShortInfo(val iata: String? = null, val name: String? = null)
data class FlightMovement(val scheduledTimeLocal: String? = null, val airport: AirportShortInfo? = null)
data class FlightStatusResponse(val number: String?, val airline: AirlineInfo?, val status: String?, val departure: FlightMovement?, val arrival: FlightMovement?)

interface AeroAPI {
    @Headers(
        "X-RapidAPI-Key: 1513b4f774msh56378ee7de3d78ep194288jsn6e66abc527db",
        "X-RapidAPI-Host: aerodatabox.p.rapidapi.com"
    )
    @GET("flights/airports/iata/{origin}")
    suspend fun getFlightsFromAirport(
        @Path("origin") origin: String,
        @Query("direction") direction: String = "Departure",
        @Query("withLocation") withLocation: Boolean = true,
        @Query("withAircraftImage") withAircraftImage: Boolean = false
    ): FlightResponse
}

fun createApi(): AeroAPI = Retrofit.Builder()
    .baseUrl("https://aerodatabox.p.rapidapi.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(AeroAPI::class.java)

fun randomTime(): String {
    val h = (0..23).random().toString().padStart(2,'0')
    val m = (0..59).random().toString().padStart(2,'0')
    return "$h:$m"
}

@Composable
fun FlightSearchScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    // NOTE: UserPreferences class definition is assumed to be available
    val prefs = remember { UserPreferences(context) }
    val api = remember { createApi() }
    val scope = rememberCoroutineScope()

    var origin by remember { mutableStateOf(TextFieldValue("")) }
    var destination by remember { mutableStateOf(TextFieldValue("")) }
    var flights by remember { mutableStateOf<List<FlightItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedFlight by remember { mutableStateOf<FlightItem?>(null) }
    var flightDetails by remember { mutableStateOf<FlightStatusResponse?>(null) }

    var isOriginManuallyClosed by remember { mutableStateOf(false) }
    var isDestManuallyClosed by remember { mutableStateOf(false) }


    val originHistory by prefs.originHistory.collectAsState(initial = emptySet())
    val destHistory by prefs.destHistory.collectAsState(initial = emptySet())

    val bg = Brush.verticalGradient(listOf(Color(0xFF001B48), Color(0xFF0056A4), Color(0xFF00A6FB)))

    Box(Modifier.fillMaxSize().background(bg).padding(20.dp)) {

        TextButton(
            onClick = { FirebaseAuth.getInstance().signOut(); onBack() },
            modifier = Modifier.align(Alignment.TopEnd)
        ) { Text("Logout", color = Color.White) }

        if (selectedFlight != null) {
            FlightDetailsScreen(selectedFlight!!, flightDetails) {
                selectedFlight = null
                flightDetails = null
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(50.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Flight, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Search Flights", color = Color.White, fontSize = 24.sp)
                }

                Spacer(Modifier.height(25.dp))

                val filteredOrigin = remember(origin.text, originHistory) {
                    originHistory.filter { it.startsWith(origin.text, true) }
                }
                // DERIVED STATE: Only expand if valid input, matches exist, AND it hasn't been manually closed
                val showOriginMenu = origin.text.length >= 2 && filteredOrigin.isNotEmpty() && !isOriginManuallyClosed


                ExposedDropdownMenuBox(
                    expanded = showOriginMenu,
                    onExpandedChange = { isOriginManuallyClosed = !it }
                ) {
                    OutlinedTextField(
                        value = origin,
                        onValueChange = { v ->
                            origin = TextFieldValue(v.text.uppercase(), v.selection)
                            isOriginManuallyClosed = false
                        },
                        label = { Text("Origin (JFK)", color = Color.White) },
                        singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showOriginMenu) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                        modifier = Modifier.fillMaxWidth(0.9f).menuAnchor(),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White, unfocusedBorderColor = Color.White,
                            focusedLabelColor = Color.White, unfocusedLabelColor = Color.White,
                            cursorColor = Color.White
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = showOriginMenu,
                        onDismissRequest = { isOriginManuallyClosed = true },
                        containerColor = Color(0xFF000000)
                    ) {
                        filteredOrigin.forEach { option ->
                            DropdownRow(text = option) {
                                origin = TextFieldValue(option, TextRange(option.length))
                                isOriginManuallyClosed = true
                            }
                        }
                    }
                }

                if (origin.text.isNotEmpty() && origin.text.length != 3)
                    Text("Must be 3 letters", color = Color.Red, fontSize = 12.sp)

                Spacer(Modifier.height(15.dp))

                val filteredDest = remember(destination.text, destHistory) {
                    destHistory.filter { it.startsWith(destination.text, true) }
                }
                val showDestMenu = destination.text.length >= 2 && filteredDest.isNotEmpty() && !isDestManuallyClosed


                ExposedDropdownMenuBox(
                    expanded = showDestMenu,
                    onExpandedChange = { isDestManuallyClosed = !it }
                ) {
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { v ->
                            destination = TextFieldValue(v.text.uppercase(), v.selection)
                            isDestManuallyClosed = false
                        },
                        label = { Text("Destination (optional)", color = Color.White) },
                        singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showDestMenu) },
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                        modifier = Modifier.fillMaxWidth(0.9f).menuAnchor(),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White, unfocusedBorderColor = Color.White,
                            focusedLabelColor = Color.White, unfocusedLabelColor = Color.White,
                            cursorColor = Color.White
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = showDestMenu,
                        onDismissRequest = { isDestManuallyClosed = true },
                        containerColor = Color(0xFF000000)
                    ) {
                        filteredDest.forEach { option ->
                            DropdownRow(text = option) {
                                destination = TextFieldValue(option, TextRange(option.length))
                                isDestManuallyClosed = true
                            }
                        }
                    }
                }

                Spacer(Modifier.height(25.dp))

                Button(
                    onClick = {
                        if (origin.text.length != 3) return@Button
                        isLoading = true
                        flights = emptyList()

                        scope.launch(Dispatchers.IO) {
                            if (origin.text.isNotEmpty()) prefs.saveOrigin(origin.text)
                            if (destination.text.isNotEmpty()) prefs.saveDestination(destination.text)

                            try {
                                val response = api.getFlightsFromAirport(origin.text)
                                val allFlights = response.departures ?: emptyList()
                                flights =
                                    if (destination.text.length == 3)
                                        allFlights.filter { it.arrival?.airport?.iata.equals(destination.text, true) }
                                    else allFlights
                            } catch (e: Exception) {
                                Log.e("API_ERROR", e.toString())
                            }
                            isLoading = false
                        }
                    },
                    enabled = origin.text.length == 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(0.9f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) { Text("Search", fontSize = 18.sp) }

                Spacer(Modifier.height(25.dp))

                if (isLoading) CircularProgressIndicator(color = Color.White)
                else if (flights.isEmpty()) Text("No flights found!", color = Color.White, fontSize = 16.sp)
                else LazyColumn(Modifier.fillMaxWidth().weight(1f)) {
                    items(flights) { flight ->
                        FlightCard(flight) {
                            selectedFlight = flight
                            flightDetails = FlightStatusResponse(
                                number = flight.number,
                                airline = flight.airline,
                                status = flight.status ?: "Scheduled",
                                departure = FlightMovement(randomTime(), flight.departure?.airport),
                                arrival = FlightMovement(randomTime(), flight.arrival?.airport)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownRow(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x66000000))
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(10.dp)
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
fun FlightCard(flight: FlightItem, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier.padding(8.dp).fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(12.dp)) {
            Text("${flight.airline?.name ?: "Unknown"} ${flight.number ?: ""}", fontSize = 18.sp)
            Text("Status: ${flight.status ?: "N/A"}", color = Color.Gray, fontSize = 14.sp)
            Text("View Details", color = Color(0xFF0066CC), fontSize = 14.sp,
                modifier = Modifier.clickable(onClick = onClick),
                style = LocalTextStyle.current.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline))
        }
    }
}

@Composable
fun FlightDetailsScreen(flight: FlightItem, details: FlightStatusResponse?, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(top = 60.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) { Text("← Back", color = Color.White) }
        Spacer(Modifier.height(10.dp))
        Card(colors = CardDefaults.cardColors(Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${flight.airline?.name ?: "Unknown"} ${flight.number ?: ""}", fontSize = 20.sp)
                Text("Status: ${details?.status ?: "N/A"}", fontSize = 16.sp)
                val dep = details?.departure?.airport?.name ?: details?.departure?.airport?.iata
                val arr = details?.arrival?.airport?.name ?: details?.arrival?.airport?.iata
                Text("Departure: ${dep ?: "-"} at ${details?.departure?.scheduledTimeLocal ?: "-"}")
                Text("Arrival: ${arr ?: "-"} at ${details?.arrival?.scheduledTimeLocal ?: "-"}")
            }
        }
    }
}