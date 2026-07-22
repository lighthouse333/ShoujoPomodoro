package com.shoujopomodoro.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shoujopomodoro.domain.model.TimerPhase
import com.shoujopomodoro.ui.theme.ShoujoPink
import com.shoujopomodoro.ui.theme.ShoujoLavender
import com.shoujopomodoro.ui.theme.ShoujoGold
import com.shoujopomodoro.ui.theme.ShoujoSkyBlue
import com.shoujopomodoro.ui.theme.ShoujoMint
import com.shoujopomodoro.ui.theme.ShoujoRose
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Size

@Composable
fun EnhancedCircularTimerIndicator(
    progress: Float,
    phase: TimerPhase,
    containerSize: Dp = 300.dp,
    strokeWidth: Dp = 10.dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val particles = remember { mutableStateOf(emptyList<IndicatorParticle>()) }
    val pxSize = with(density) { containerSize.toPx() }

    // Particle animation
    LaunchedEffect(progress, phase) {
        while (true) {
            // Add new particles occasionally
            if (Math.random() < 0.3) {
                val angle = (Math.random() * 2 * Math.PI).toFloat()
                val distance = (pxSize / 2 * 0.7f)
                val x = (pxSize / 2 + distance * Math.cos(angle.toDouble())).toFloat()
                val y = (pxSize / 2 + distance * Math.sin(angle.toDouble())).toFloat()
                val sz = (Math.random() * 4 + 2).toFloat()
                val clr = when (phase) {
                    TimerPhase.FOCUS -> Color(0xFFFF69B4)
                    TimerPhase.SHORT_BREAK -> Color(0xFF7CFC00)
                    TimerPhase.LONG_BREAK -> Color(0xFF00BFFF)
                }.copy(alpha = 0.7f)

                particles.value = particles.value + IndicatorParticle(x, y, sz, clr, 100f)
            }

            // Update existing particles
            particles.value = particles.value.map { it: IndicatorParticle ->
                IndicatorParticle(
                    it.x,
                    it.y,
                    it.size,
                    it.color,
                    it.life - 1f
                )
            }.filter { it.life > 0 }

            delay(50)
        }
    }

    Box(modifier = modifier.size(containerSize)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val centerX = containerSize.toPx() / 2
            val centerY = containerSize.toPx() / 2
            val radius = (containerSize.toPx() / 2 - strokeWidth.toPx() / 2).toFloat()
            
            // Background track
            drawCircle(
                color = Color(0x33FFFFFF),
                radius = radius,
                center = Offset(centerX, centerY)
            )
            
            // Progress arc with glow effect
            val startAngle = -90f
            val sweepAngle = 360f * progress
            
            // Glow layer
            val glowRadius = radius + 4f
            drawArc(
                color = when (phase) {
                    TimerPhase.FOCUS -> Color(0x80FF69B4)
                    TimerPhase.SHORT_BREAK -> Color(0x807CFC00)
                    TimerPhase.LONG_BREAK -> Color(0x8000BFFF)
                },
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - glowRadius, centerY - glowRadius),
                size = androidx.compose.ui.geometry.Size(glowRadius * 2, glowRadius * 2),
                style = Stroke(width = strokeWidth.toPx() * 2)
            )
            
            // Main progress arc
            drawArc(
                color = when (phase) {
                    TimerPhase.FOCUS -> Color(0xFFFF69B4)
                    TimerPhase.SHORT_BREAK -> Color(0xFF7CFC00)
                    TimerPhase.LONG_BREAK -> Color(0xFF00BFFF)
                },
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth.toPx())
            )
            
            // Center decorative element
            val centerColor = when (phase) {
                TimerPhase.FOCUS -> Color(0xFFFF69B4)
                TimerPhase.SHORT_BREAK -> Color(0xFF7CFC00)
                TimerPhase.LONG_BREAK -> Color(0xFF00BFFF)
            }
            
            drawCircle(
                color = centerColor.copy(alpha = 0.2f),
                radius = radius * 0.3f,
                center = Offset(centerX, centerY)
            )
            
            // Particles
            particles.value.forEach { particle ->
                drawCircle(
                    color = particle.color,
                    radius = particle.size,
                    center = Offset(particle.x, particle.y)
                )
            }
        }
    }
}

data class IndicatorParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val life: Float
)

