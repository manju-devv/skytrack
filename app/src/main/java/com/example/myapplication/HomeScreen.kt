package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay

import com.example.myapplication.location.LocationHelper
import com.google.accompanist.permissions.*
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onGoToFlights: () -> Unit,
    onLocationPermissionGranted: () -> Unit,
    onLocationPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Ask permission only once
    LaunchedEffect(Unit) {
        if (permissionState.status !is PermissionStatus.Granted) {
            permissionState.launchPermissionRequest()
        }
    }

    val status = permissionState.status

    // 🔁 WATCH GPS STATUS AND REFRESH UI EVERY SECOND
    var gpsEnabled by remember { mutableStateOf(locationHelper.isLocationEnabled()) }

    LaunchedEffect(Unit) {
        while (true) {
            gpsEnabled = locationHelper.isLocationEnabled()
            delay(1000)
        }
    }

    // BACKGROUND UI
    val backgroundBrush = Brush.verticalGradient(
        listOf(Color(0xFF020024), Color(0xFF090979), Color(0xFF00D4FF))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(20.dp)
    ) {

        // ❌ GPS OFF → SHOW THIS (AUTO-REFRESHES WHEN USER ENABLES GPS)
        if (!gpsEnabled) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Location Disabled") },
                text = { Text("Please enable GPS for nearby flights.") },
                confirmButton = {
                    Button(onClick = {
                        // Opens Android's built-in GPS enable popup (not settings page)
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }) {
                        Text("Enable GPS")
                    }
                }
            )
            return@Box
        }

        // ❌ Permission not granted
        if (status !is PermissionStatus.Granted) {
            Text(
                text = "Location permission required.",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            return@Box
        }

        // UI (same)
        TextButton(
            onClick = onLogout,
            modifier = Modifier.align(Alignment.TopEnd)
        ) { Text("Logout", color = Color.White) }

        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("web_flight.json"))
        val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

        Column(
            modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier.fillMaxHeight(0.8f)
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onGoToFlights,
                modifier = Modifier.width(220.dp)
            ) { Text("View Flights") }
        }
    }
}


//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun HomeScreen(
//    onLogout: () -> Unit,
//    onGoToFlights: () -> Unit,
//    onLocationPermissionGranted: () -> Unit,
//    onLocationPermissionDenied: () -> Unit
//) {
//    val context = LocalContext.current
//    val locationHelper = remember { LocationHelper(context) }
//
//    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
//
//    // Ask permission on first launch
//    LaunchedEffect(Unit) {
//        if (locationPermissionState.status !is PermissionStatus.Granted) {
//            locationPermissionState.launchPermissionRequest()
//        } else {
//            onLocationPermissionGranted()
//        }
//    }
//
//    // Listen to permission changes
//    val status = locationPermissionState.status
//    LaunchedEffect(status) {
//        when (status) {
//            is PermissionStatus.Granted -> onLocationPermissionGranted()
//            is PermissionStatus.Denied -> onLocationPermissionDenied()
//        }
//    }
//
//    // ⭐ Check if GPS is enabled
//    var gpsEnabled by remember { mutableStateOf(locationHelper.isLocationEnabled()) }
//
//    LaunchedEffect(Unit) {
//        gpsEnabled = locationHelper.isLocationEnabled()
//        Log.d("HomeScreen", "GPS Enabled? $gpsEnabled")
//    }
//
//    // Lottie animation setup
//    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("web_flight.json"))
//    val progress by animateLottieCompositionAsState(
//        composition = composition,
//        iterations = LottieConstants.IterateForever
//    )
//
//    val backgroundBrush = Brush.verticalGradient(
//        colors = listOf(Color(0xFF020024), Color(0xFF090979), Color(0xFF00D4FF))
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(backgroundBrush)
//            .padding(20.dp)
//    ) {
//
//        // ❌ SHOW DIALOG IF GPS IS OFF
//        if (!gpsEnabled) {
//            AlertDialog(
//                onDismissRequest = {},
//                title = { Text("Location Disabled") },
//                text = { Text("Please enable GPS to get nearby flights.") },
//                confirmButton = {
//                    Button(onClick = {
//                        context.startActivity(
//                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                        )
//                    }) {
//                        Text("Enable")
//                    }
//                }
//            )
//            return@Box
//        }
//
//        // ❌ Permission not granted
//        if (status !is PermissionStatus.Granted) {
//            Text(
//                text = "Location permission is required to show nearby flights.",
//                color = Color.White,
//                modifier = Modifier.align(Alignment.Center)
//            )
//            return@Box
//        }
//
//        // ✅ MAIN UI
//        TextButton(
//            onClick = onLogout,
//            modifier = Modifier.align(Alignment.TopEnd)
//        ) {
//            Text("Logout", color = Color.White)
//        }
//
//        Column(
//            modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//            LottieAnimation(
//                composition = composition,
//                progress = progress,
//                modifier = Modifier
//                    .fillMaxHeight(0.8f)
//                    .aspectRatio(1f)
//            )
//
//            Spacer(modifier = Modifier.height(40.dp))
//
//            Button(
//                onClick = onGoToFlights,
//                modifier = Modifier.width(220.dp)
//            ) {
//                Text("View Flights")
//            }
//        }
//    }
//}


