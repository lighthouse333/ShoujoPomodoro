package com.shoujopomodoro.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shoujopomodoro.domain.model.TimerPhase
import com.shoujopomodoro.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ═══════════════════════════════════════════════
//  Premium Circular Timer — 高级圆环计时器
//  Multi-layer glow, sparkles, star markers,
//  smooth gradient progress arc ✨💫
// ═══════════════════════════════════════════════

data class OrbitingParticle(
    val x: Float, val y: Float,
    val angle: Float, val distance: Float,
    val size: Float, val color: Color,
    val speed: Float, val life: Float,
    val maxLife: Float
)

@Composable
fun PremiumCircularTimer(
    progress: Float,
    phase: TimerPhase,
    containerSize: Dp = 320.dp,
    strokeWidth: Dp = 12.dp,
    modifier: Modifier = Modifier,
    showMarkers: Boolean = true,
    showOrbitingParticles: Boolean = true,
    showInnerGlow: Boolean = true
) {
    val density = LocalDensity.current
    val pxSize = with(density) { containerSize.toPx() }

    // Smooth progress animation
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "progress"
    )

    // Orbiting particles
    val particles = remember { mutableStateListOf<OrbitingParticle>() }
    var time by remember { mutableStateOf(0f) }

    LaunchedEffect(phase) {
        while (true) {
            time += 0.016f
            if (showOrbitingParticles && Random.nextFloat() < 0.25f && particles.size < 20) {
                val angle = Random.nextFloat() * 2f * PI.toFloat()
                val dist = pxSize / 2 * (0.85f + Random.nextFloat() * 0.1f)
                particles.add(OrbitingParticle(
                    x = (pxSize / 2 + cos(angle) * dist).toFloat(),
                    y = (pxSize / 2 + sin(angle) * dist).toFloat(),
                    angle = angle, distance = dist,
                    size = Random.nextFloat() * 3f + 1.5f,
                    color = when (phase) {
                        TimerPhase.FOCUS -> listOf(SakuraPink, SakuraDeep, KonpeitoGold, RoseBlush).random()
                        TimerPhase.SHORT_BREAK -> listOf(MatchaMint, Color(0xFF7CFC00), KonpeitoGold).random()
                        TimerPhase.LONG_BREAK -> listOf(MermaidBlue, SkyBlue, IceBlue, KonpeitoGold).random()
                    }.copy(alpha = 0.9f),
                    speed = Random.nextFloat() * 0.3f + 0.1f,
                    life = 0f,
                    maxLife = Random.nextFloat() * 80f + 40f
                ))
            }

            // Update particles
            val toRemove = mutableListOf<OrbitingParticle>()
            for (i in particles.indices) {
                val p = particles[i]
                val newLife = p.life + 1f
                if (newLife >= p.maxLife) {
                    toRemove.add(p)
                } else {
                    val lifeRatio = newLife / p.maxLife
                    val fadeAlpha = if (lifeRatio > 0.7f) (1f - lifeRatio) / 0.3f else 1f
                    particles[i] = p.copy(
                        angle = p.angle + p.speed * 0.05f,
                        life = newLife,
                        color = p.color.copy(alpha = fadeAlpha.coerceIn(0f, 1f)),
                        x = (pxSize / 2 + cos(p.angle + p.speed * 0.05f) * p.distance).toFloat(),
                        y = (pxSize / 2 + sin(p.angle + p.speed * 0.05f) * p.distance).toFloat()
                    )
                }
            }
            particles.removeAll(toRemove)
            delay(33)
        }
    }

    val phaseColor = when (phase) {
        TimerPhase.FOCUS -> SakuraDeep
        TimerPhase.SHORT_BREAK -> MatchaMint
        TimerPhase.LONG_BREAK -> SkyBlue
    }

    val trackColor = when (phase) {
        TimerPhase.FOCUS -> Color(0x20FF69B4)
        TimerPhase.SHORT_BREAK -> Color(0x2066BB6A)
        TimerPhase.LONG_BREAK -> Color(0x2042A5F5)
    }

    Box(modifier = modifier.size(containerSize)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val strokePx = strokeWidth.toPx()
            val radius = (minOf(cx, cy) - strokePx / 2f)

            // ── Outer glow ring ──
            drawCircle(
                color = phaseColor.copy(alpha = 0.08f),
                radius = radius + strokePx * 2.5f,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = phaseColor.copy(alpha = 0.04f),
                radius = radius + strokePx * 4f,
                center = Offset(cx, cy)
            )

            // ── Track arc ──
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // ── Star progress markers (every 25%) ──
            if (showMarkers) {
                for (i in 0..3) {
                    val markerAngle = Math.toRadians((i * 90.0 - 90.0))
                    val mx = cx + radius * cos(markerAngle).toFloat()
                    val my = cy + radius * sin(markerAngle).toFloat()
                    val isActive = animatedProgress >= (i / 4f)
                    val markerColor = if (isActive) phaseColor else trackColor
                    drawStarMarker(mx, my, strokePx * 2f, markerColor.copy(alpha = if (isActive) 0.9f else 0.3f))
                }
            }

            // ── Glow arc layer ──
            drawArc(
                color = phaseColor.copy(alpha = 0.25f),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(cx - radius - strokePx, cy - radius - strokePx),
                size = Size((radius + strokePx) * 2, (radius + strokePx) * 2),
                style = Stroke(width = strokePx * 2.2f, cap = StrokeCap.Round)
            )

            // ── Main progress arc with gradient ──
            val progressGradient = Brush.sweepGradient(
                colors = listOf(
                    phaseColor.copy(alpha = 0.6f),
                    phaseColor,
                    phaseColor.copy(alpha = 0.8f),
                    phaseColor
                )
            )
            drawArc(
                color = phaseColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(cx - radius, cy - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // ── Progress tip glow dot ──
            if (animatedProgress > 0.001f) {
                val tipAngle = Math.toRadians((-90.0 + 360.0 * animatedProgress))
                val tipX = cx + radius * cos(tipAngle).toFloat()
                val tipY = cy + radius * sin(tipAngle).toFloat()
                drawCircle(phaseColor.copy(alpha = 0.3f), strokePx * 1.5f, Offset(tipX, tipY))
                drawCircle(Color.White.copy(alpha = 0.8f), strokePx * 0.5f, Offset(tipX, tipY))
            }

            // ── Inner glow circle ──
            if (showInnerGlow) {
                drawCircle(
                    phaseColor.copy(alpha = 0.06f),
                    radius * 0.42f,
                    Offset(cx, cy)
                )
                drawCircle(
                    phaseColor.copy(alpha = 0.03f),
                    radius * 0.55f,
                    Offset(cx, cy)
                )
            }

            // ── Center decorative dot pattern ──
            for (i in 0..7) {
                val da = Math.toRadians((i * 45.0))
                val dx = cx + radius * 0.28f * cos(da).toFloat()
                val dy = cy + radius * 0.28f * sin(da).toFloat()
                drawCircle(
                    phaseColor.copy(alpha = 0.12f),
                    strokePx * 0.4f,
                    Offset(dx, dy)
                )
            }

            // ── Orbiting particles ──
            particles.forEach { p ->
                drawCircle(p.color, p.size, Offset(p.x, p.y))
                // Glow halo
                drawCircle(p.color.copy(alpha = p.color.alpha * 0.2f), p.size * 2.5f, Offset(p.x, p.y))
            }
        }
    }
}

private fun DrawScope.drawStarMarker(x: Float, y: Float, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(x, y - size)
        lineTo(x + size * 0.25f, y - size * 0.25f)
        lineTo(x + size, y)
        lineTo(x + size * 0.25f, y + size * 0.25f)
        lineTo(x, y + size)
        lineTo(x - size * 0.25f, y + size * 0.25f)
        lineTo(x - size, y)
        lineTo(x - size * 0.25f, y - size * 0.25f)
        close()
    }
    drawPath(path, color)
    drawCircle(color.copy(alpha = color.alpha * 0.3f), size * 0.6f, Offset(x, y))
}
