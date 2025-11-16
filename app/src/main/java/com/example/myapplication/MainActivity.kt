package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.models.FlightData
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.FlightsViewModel
import com.example.myapplication.viewmodel.FlightsViewModelFactory
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            MyApplicationTheme {

                val navController = rememberNavController()

                // ⭐ Check logged state
                val isLoggedIn = auth.currentUser != null

                // ⭐ Listen for auto logout (token expiry)
                LaunchedEffect(Unit) {
                    auth.addAuthStateListener { firebaseAuth ->
                        if (firebaseAuth.currentUser == null) {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }

                // ⭐ Create ViewModel using FACTORY (correct way)
                val flightsViewModel: FlightsViewModel = viewModel(
                    factory = FlightsViewModelFactory(BuildConfig.RAPIDAPI_KEY)
                )

                // ⭐ Decide first screen
                val startScreen = if (isLoggedIn) "home" else "splash"

                NavHost(
                    navController = navController,
                    startDestination = startScreen
                ) {

                    // Splash
                    composable("splash") {
                        LaunchedEffect(Unit) {
                            delay(2500)
                            navController.navigate("login") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                        SplashScreen(onTimeout = {})
                    }

                    // Login
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    // Register
                    composable("register") {
                        RegisterScreen(
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        )
                    }

                    // Home
                    composable("home") {
                        HomeScreen(
                            onLogout = {
                                auth.signOut()
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onGoToFlights = {
                                navController.navigate("flightList")
                            },
                            onLocationPermissionGranted = {},
                            onLocationPermissionDenied = {}
                        )
                    }

                    // Flight list
                    composable("flightList") {
                        FlightsScreen(
                            viewModel = flightsViewModel,
                            onFlightClick = { flight ->
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedFlight", flight)

                                navController.navigate("flightDetails")
                            }
                        )
                    }

                    // Flight details
                    composable("flightDetails") {
                        val selectedFlight =
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<FlightData>("selectedFlight")

                        if (selectedFlight != null) {
                            FlightDetailsScreen(
                                flight = selectedFlight,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
