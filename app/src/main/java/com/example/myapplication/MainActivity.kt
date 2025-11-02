package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        setContent {
            MyApplicationTheme {
                var showSplash by remember { mutableStateOf(true) }
                var currentScreen by remember { mutableStateOf("login") }

                val savedEmail = sharedPrefs.getString("email", null)
                val savedPassword = sharedPrefs.getString("password", null)

                when {
                    showSplash -> SplashScreen(onTimeout = { showSplash = false })
                    currentScreen == "register" -> RegisterScreen(
                        onRegisterSuccess = { email, password ->
                            with(sharedPrefs.edit()) {
                                putString("email", email)
                                putString("password", password)
                                apply()
                            }
                            currentScreen = "login"
                        },
                        onNavigateToLogin = { currentScreen = "login" }
                    )
                    currentScreen == "login" -> LoginScreen(
                        onLoginSuccess = { currentScreen = "home" },
                        onNavigateToRegister = { currentScreen = "register" },
                        validateUser = { email, password ->
                            email == savedEmail && password == savedPassword
                        }
                    )
                    else -> HomeScreen(onLogout = {
                        currentScreen = "login"
                    })
                }
            }
        }
    }
}
