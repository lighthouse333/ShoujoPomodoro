package com.shoujopomodoro.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.shoujopomodoro.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ═══════════════════════════════════════════════
//  Sakura Particle System — 樱吹雪粒子系统
//  Cherry blossom petals, sparkles, light motes
//  that drift across the screen ✨🌸
// ═══════════════════════════════════════════════

private const val MAX_PARTICLES = 40
private const val SPAWN_INTERVAL_MS = 120L
private const val UPDATE_INTERVAL_MS = 33L // ~30fps

enum class ParticleType {
    SAKURA_PETAL,  // 5-petal cherry blossom
    SPARKLE,        // 4-point star sparkle
    LIGHT_MOTE,     // Soft glowing dot
    HEART           // Tiny heart
}

data class DreamParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
    val alpha: Float,
    val life: Float,
    val maxLife: Float,
    val type: ParticleType,
    val rotation: Float,
    val rotationSpeed: Float,
    val velocityX: Float,
    val velocityY: Float,
    val waveAmplitude: Float,
    val waveFrequency: Float
)

@Composable
fun SakuraParticleBackground(
    modifier: Modifier = Modifier,
    intensity: Float = 1f,  // 0..1 controls spawn rate
    tintColor: Color? = null, // optional tint override
    preferredType: ParticleType? = null // bias toward a type
) {
    val particles = remember { mutableStateListOf<DreamParticle>() }

    LaunchedEffect(intensity) {
        var time = 0f
        while (true) {
            // Spawn new particles
            val spawnChance = 0.15f * intensity
            if (Random.nextFloat() < spawnChance && particles.size < MAX_PARTICLES) {
                val w = 1200f // virtual canvas width
                val h = 2000f
                val x = Random.nextFloat() * w
                val y = -20f // spawn above screen

                val t = preferredType ?: when (Random.nextInt(10)) {
                    in 0..4 -> ParticleType.SAKURA_PETAL
                    in 5..6 -> ParticleType.SPARKLE
                    in 7..8 -> ParticleType.LIGHT_MOTE
                    else -> ParticleType.HEART
                }

                val clr = tintColor ?: when (t) {
                    ParticleType.SAKURA_PETAL -> listOf(
                        SakuraPink, RoseBlush, SakuraLight, CoralRose, HairSakura
                    ).random()
                    ParticleType.SPARKLE -> listOf(
                        KonpeitoGold, Color.White, WarmAmber
                    ).random()
                    ParticleType.LIGHT_MOTE -> listOf(
                        WisteriaLavender.copy(alpha = 0.6f),
                        MermaidBlue.copy(alpha = 0.5f),
                        SakuraGlow.copy(alpha = 0.7f),
                        IceBlue.copy(alpha = 0.5f)
                    ).random()
                    ParticleType.HEART -> listOf(
                        SakuraDeep, CoralRose, RoseBlush
                    ).random()
                }

                particles.add(DreamParticle(
                    x = x, y = y,
                    size = when (t) {
                        ParticleType.SAKURA_PETAL -> Random.nextFloat() * 8f + 6f
                        ParticleType.SPARKLE -> Random.nextFloat() * 6f + 3f
                        ParticleType.LIGHT_MOTE -> Random.nextFloat() * 4f + 2f
                        ParticleType.HEART -> Random.nextFloat() * 5f + 4f
                    },
                    color = clr,
                    alpha = Random.nextFloat() * 0.5f + 0.5f,
                    life = 0f,
                    maxLife = Random.nextFloat() * 120f + 80f,
                    type = t,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = Random.nextFloat() * 3f - 1.5f,
                    velocityX = Random.nextFloat() * 0.8f - 0.4f,
                    velocityY = Random.nextFloat() * 0.6f + 0.3f,
                    waveAmplitude = Random.nextFloat() * 15f + 5f,
                    waveFrequency = Random.nextFloat() * 0.02f + 0.01f
                ))
            }

            // Update particles
            time += 0.033f
            val toRemove = mutableListOf<DreamParticle>()
            for (i in particles.indices) {
                val p = particles[i]
                val newLife = p.life + 1f
                if (newLife >= p.maxLife) {
                    toRemove.add(p)
                } else {
                    val lifeRatio = newLife / p.maxLife
                    val fadeAlpha = when {
                        lifeRatio < 0.1f -> lifeRatio / 0.1f  // fade in
                        lifeRatio > 0.7f -> (1f - lifeRatio) / 0.3f  // fade out
                        else -> 1f
                    }
                    particles[i] = p.copy(
                        x = p.x + p.velocityX + sin(time * p.waveFrequency) * p.waveAmplitude * 0.05f,
                        y = p.y + p.velocityY,
                        life = newLife,
                        alpha = p.alpha * fadeAlpha,
                        rotation = p.rotation + p.rotationSpeed
                    )
                }
            }
            particles.removeAll(toRemove)

            delay(UPDATE_INTERVAL_MS)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = this.size.width
            val canvasH = this.size.height
            val scaleX = canvasW / 1200f
            val scaleY = canvasH / 2000f

            particles.forEach { p ->
                val cx = p.x * scaleX
                val cy = p.y * scaleY
                val sz = p.size * minOf(scaleX, scaleY) * 1.5f
                val clr = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f))

                when (p.type) {
                    ParticleType.SAKURA_PETAL -> drawSakuraPetal(cx, cy, sz, clr, p.rotation)
                    ParticleType.SPARKLE -> drawSparkleShape(cx, cy, sz, clr, p.rotation)
                    ParticleType.LIGHT_MOTE -> drawLightMote(cx, cy, sz, clr)
                    ParticleType.HEART -> drawHeartShape(cx, cy, sz * 0.7f, clr, p.rotation)
                }
            }
        }
    }
}

// ── 5-petal sakura blossom ──
private fun DrawScope.drawSakuraPetal(
    cx: Float, cy: Float, size: Float, color: Color, rotation: Float
) {
    rotate(rotation, pivot = Offset(cx, cy)) {
        for (i in 0 until 5) {
            val angle = Math.toRadians((i * 72.0 - 90.0))
            val px = cx + size * cos(angle).toFloat()
            val py = cy + size * sin(angle).toFloat()
            val cpx1 = cx + size * 0.4f * cos(angle - 0.4).toFloat()
            val cpy1 = cy + size * 0.4f * sin(angle - 0.4).toFloat()
            val cpx2 = cx + size * 0.4f * cos(angle + 0.4).toFloat()
            val cpy2 = cy + size * 0.4f * sin(angle + 0.4).toFloat()

            val path = Path().apply {
                moveTo(cx, cy)
                cubicTo(cpx1, cpy1, cpx2, cpy2, px, py)
                close()
            }
            drawPath(path, color.copy(alpha = color.alpha * 0.9f))
        }
        // Center dot
        drawCircle(Color.White.copy(alpha = color.alpha), size * 0.15f, Offset(cx, cy))
    }
}

// ── 4-point sparkle star ──
private fun DrawScope.drawSparkleShape(
    cx: Float, cy: Float, size: Float, color: Color, rotation: Float
) {
    rotate(rotation, pivot = Offset(cx, cy)) {
        val path = Path().apply {
            moveTo(cx, cy - size)
            lineTo(cx + size * 0.25f, cy - size * 0.25f)
            lineTo(cx + size, cy)
            lineTo(cx + size * 0.25f, cy + size * 0.25f)
            lineTo(cx, cy + size)
            lineTo(cx - size * 0.25f, cy + size * 0.25f)
            lineTo(cx - size, cy)
            lineTo(cx - size * 0.25f, cy - size * 0.25f)
            close()
        }
        drawPath(path, color)
        // Glow halo
        drawCircle(color.copy(alpha = color.alpha * 0.3f), size * 0.6f, Offset(cx, cy))
    }
}

// ── Soft glowing dot ──
private fun DrawScope.drawLightMote(cx: Float, cy: Float, size: Float, color: Color) {
    drawCircle(color.copy(alpha = color.alpha * 0.25f), size * 2f, Offset(cx, cy))
    drawCircle(color.copy(alpha = color.alpha * 0.5f), size, Offset(cx, cy))
    drawCircle(color.copy(alpha = color.alpha * 0.8f), size * 0.4f, Offset(cx, cy))
}

// ── Tiny heart ──
private fun DrawScope.drawHeartShape(
    cx: Float, cy: Float, size: Float, color: Color, rotation: Float
) {
    rotate(rotation, pivot = Offset(cx, cy)) {
        val path = Path().apply {
            val s = size * 0.5f
            moveTo(cx, cy + s)
            cubicTo(cx - s * 1.5f, cy - s * 0.5f, cx - s, cy - s * 0.5f, cx, cy - s * 0.8f)
            cubicTo(cx + s, cy - s * 0.5f, cx + s * 1.5f, cy - s * 0.5f, cx, cy + s)
            close()
        }
        drawPath(path, color)
    }
}
