package com.shoujopomodoro.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ParticleBackground(
    modifier: Modifier = Modifier
) {
    val particles = remember { mutableStateOf(emptyList<BgParticle>()) }

    LaunchedEffect(Unit) {
        while (true) {
            // Add new particles
            if (Math.random() < 0.2) {
                val x = (Math.random() * 1000).toFloat()
                val y = (Math.random() * 600).toFloat()
                val sz = (Math.random() * 4 + 1).toFloat()
                val clr = when ((Math.random() * 3).toInt()) {
                    0 -> Color(0xFFFFB6C1) // Pink
                    1 -> Color(0xFFD8BFD8) // Lavender
                    else -> Color(0xFFFFD700) // Gold
                }.copy(alpha = 0.6f)

                particles.value = particles.value + BgParticle(x, y, sz, clr, 100f)
            }

            // Update particles
            particles.value = particles.value.map { particle: BgParticle ->
                BgParticle(
                    particle.x - 0.5f, // Drift left
                    particle.y + 0.3f, // Float up
                    particle.size,
                    particle.color,
                    particle.life - 0.5f
                )
            }.filter { it.life > 0 }

            delay(50)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.value.forEach { particle: BgParticle ->
                drawCircle(
                    color = particle.color,
                    radius = particle.size,
                    center = Offset(particle.x, particle.y)
                )
            }
        }
    }
}

data class BgParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val life: Float
)