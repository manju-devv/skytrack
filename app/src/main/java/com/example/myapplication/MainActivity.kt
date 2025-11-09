package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf("splash") }
                LaunchedEffect(Unit) {
                    delay(2500)
                    currentScreen = "login"
                }

                when (currentScreen) {
                    "splash" -> SplashScreen(onTimeout = { currentScreen = "login" })

                    "login" -> LoginScreen(
                        onLoginSuccess = { currentScreen = "home" },
                        onNavigateToRegister = { currentScreen = "register" }
                    )

                    "register" -> RegisterScreen(
                        onNavigateToLogin = { currentScreen = "login" }
                    )

                    "home" -> HomeScreen(
                        onLogout = { currentScreen = "login" }
                    )
                }
            }
        }
    }
}
