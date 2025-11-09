package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.User
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val userDao = db.userDao()
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    // Background gradient
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF001F3F),
            Color(0xFF003366)
        )
    )

    fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        return pattern.matcher(email).matches()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Create Account", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it.trim()
                        if (showErrors) errorMessage = ""
                    },
                    label = { Text("Email") },
                    singleLine = true,
                    isError = showErrors && (email.isBlank() || !isValidEmail(email)),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (showErrors) errorMessage = ""
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = showErrors && password.isBlank()
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        if (showErrors) errorMessage = ""
                    },
                    label = { Text("Confirm Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = showErrors && (confirmPassword.isBlank() || confirmPassword != password)
                )

                if (showErrors && errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = {
                        showErrors = true
                        scope.launch {
                            when {
                                email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                                    errorMessage = "All fields are required"
                                }
                                !isValidEmail(email) -> {
                                    errorMessage = "Please enter a valid email address"
                                }
                                password != confirmPassword -> {
                                    errorMessage = "Passwords do not match"
                                }
                                else -> {
                                    val existingUser = userDao.getUserByEmail(email)
                                    if (existingUser == null) {
                                        userDao.insertUser(User(email = email, password = password))
                                        onNavigateToLogin()
                                    } else {
                                        errorMessage = "User already exists"
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register")
                }

                TextButton(onClick = onNavigateToLogin) {
                    Text("Already have an account? Login")
                }
            }
        }
    }
}
