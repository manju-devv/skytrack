package com.example.myapplication

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = ""
    )

    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1500),
        label = ""
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF001F3F),
                        Color(0xFF003366)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.flight),
                contentDescription = "Flight Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sky Track",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alphaAnim.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Aviation • Compass • Live Flights",
                color = Color(0xFFB0C4DE),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alphaAnim.value)
            )
        }
    }
}
