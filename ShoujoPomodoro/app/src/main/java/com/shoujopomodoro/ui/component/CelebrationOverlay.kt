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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.shoujopomodoro.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

// ═══════════════════════════════════════════════
//  Celebration Effects — 庆祝特效系统
//  Confetti burst, star shower, sakura storm
//  Triggered on pomodoro completion 🎉✨🌸
// ═══════════════════════════════════════════════

enum class CelebrationStyle {
    CONFETTI,       // Colorful paper confetti
    STAR_SHOWER,    // Golden star rain
    SAKURA_STORM,   // Cherry blossom blizzard
    HEART_BURST,    // Heart explosion
    MAGIC_SPARKLE   // Magical transformation sparkles
}

data class ConfettiPiece(
    val x: Float, val y: Float,
    val velocityX: Float, val velocityY: Float,
    val gravity: Float,
    val rotation: Float, val rotationSpeed: Float,
    val color: Color,
    val size: Float,
    val shape: ConfettiShape,
    val life: Float,
    val maxLife: Float,
    val wind: Float
)

enum class ConfettiShape { RECTANGLE, CIRCLE, TRIANGLE, STAR, PETAL }

@Composable
fun CelebrationOverlay(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    style: CelebrationStyle = CelebrationStyle.SAKURA_STORM,
    onFinished: () -> Unit = {}
) {
    if (!isVisible) return

    val pieces = remember { mutableStateListOf<ConfettiPiece>() }
    val burstCount = when (style) {
        CelebrationStyle.CONFETTI -> 60
        CelebrationStyle.STAR_SHOWER -> 40
        CelebrationStyle.SAKURA_STORM -> 50
        CelebrationStyle.HEART_BURST -> 30
        CelebrationStyle.MAGIC_SPARKLE -> 80
    }

    LaunchedEffect(isVisible) {
        // Initial burst
        for (i in 0 until burstCount) {
            val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
            val speed = Random.nextFloat() * 8f + 3f
            pieces.add(createPiece(
                cx = 600f, cy = 800f,  // center-ish spawn
                angle = angle, speed = speed, style = style
            ))
        }

        // Animation loop
        var frame = 0
        while (frame < 180) { // ~6 seconds at 30fps
            val toRemove = mutableListOf<ConfettiPiece>()

            // Continue spawning for SAKURA_STORM style
            if (style == CelebrationStyle.SAKURA_STORM && frame < 120 && frame % 8 == 0) {
                val angle = Random.nextFloat() * Math.PI.toFloat() + Math.PI.toFloat() / 2f
                pieces.add(createPiece(
                    cx = Random.nextFloat() * 1200f, cy = -30f,
                    angle = angle, speed = Random.nextFloat() * 3f + 1f, style = style
                ))
            }

            for (i in pieces.indices) {
                val p = pieces[i]
                val newLife = p.life + 1f
                if (newLife >= p.maxLife) {
                    toRemove.add(p)
                } else {
                    val lifeRatio = newLife / p.maxLife
                    val fadeAlpha = if (lifeRatio > 0.6f) (1f - lifeRatio) / 0.4f else 1f
                    pieces[i] = p.copy(
                        x = p.x + p.velocityX + p.wind,
                        y = p.y + p.velocityY + p.gravity,
                        velocityX = p.velocityX * 0.995f,
                        velocityY = p.velocityY * 0.995f,
                        rotation = p.rotation + p.rotationSpeed,
                        life = newLife,
                        color = p.color.copy(alpha = fadeAlpha.coerceIn(0f, 1f))
                    )
                }
            }
            pieces.removeAll(toRemove)

            frame++
            delay(33)
        }

        pieces.clear()
        onFinished()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scaleX = size.width / 1200f
            val scaleY = size.height / 2000f

            pieces.forEach { p ->
                val cx = p.x * scaleX
                val cy = p.y * scaleY
                val sz = p.size * minOf(scaleX, scaleY)
                if (cx < -50 || cx > size.width + 50 || cy > size.height + 50) return@forEach

                drawConfettiPiece(cx, cy, sz, p.color, p.shape, p.rotation)
            }
        }
    }
}

private fun createPiece(
    cx: Float, cy: Float, angle: Float, speed: Float, style: CelebrationStyle
): ConfettiPiece {
    val colors = when (style) {
        CelebrationStyle.CONFETTI -> listOf(
            SakuraPink, WisteriaLavender, SkyBlue, MatchaMint,
            KonpeitoGold, CoralRose, MermaidBlue, RoseBlush
        )
        CelebrationStyle.STAR_SHOWER -> listOf(
            KonpeitoGold, WarmAmber, Color.White, Color(0xFFFFE082)
        )
        CelebrationStyle.SAKURA_STORM -> listOf(
            SakuraPink, RoseBlush, HairSakura, SakuraLight, CoralRose
        )
        CelebrationStyle.HEART_BURST -> listOf(
            SakuraDeep, CoralRose, RoseBlush, Color(0xFFFF4081)
        )
        CelebrationStyle.MAGIC_SPARKLE -> listOf(
            WisteriaLavender, MermaidBlue, SakuraGlow, IceBlue,
            Color.White, KonpeitoGold
        )
    }

    val shapes = when (style) {
        CelebrationStyle.CONFETTI -> listOf(ConfettiShape.RECTANGLE, ConfettiShape.CIRCLE, ConfettiShape.TRIANGLE)
        CelebrationStyle.STAR_SHOWER -> listOf(ConfettiShape.STAR)
        CelebrationStyle.SAKURA_STORM -> listOf(ConfettiShape.PETAL)
        CelebrationStyle.HEART_BURST -> listOf(ConfettiShape.STAR, ConfettiShape.CIRCLE)
        CelebrationStyle.MAGIC_SPARKLE -> listOf(ConfettiShape.CIRCLE, ConfettiShape.STAR)
    }

    return ConfettiPiece(
        x = cx, y = cy,
        velocityX = cos(angle) * speed * (if (style == CelebrationStyle.SAKURA_STORM) 0.6f else 1f),
        velocityY = sin(angle) * speed,
        gravity = when (style) {
            CelebrationStyle.SAKURA_STORM -> 0.02f
            else -> 0.08f
        },
        rotation = Random.nextFloat() * 360f,
        rotationSpeed = Random.nextFloat() * 15f - 7.5f,
        color = colors.random().copy(alpha = 1f),
        size = Random.nextFloat() * 8f + 4f,
        shape = shapes.random(),
        life = 0f,
        maxLife = Random.nextFloat() * 120f + 60f,
        wind = when (style) {
            CelebrationStyle.SAKURA_STORM -> Random.nextFloat() * 0.3f - 0.15f
            else -> 0f
        }
    )
}

private fun DrawScope.drawConfettiPiece(
    cx: Float, cy: Float, size: Float, color: Color, shape: ConfettiShape, rotation: Float
) {
    rotate(rotation, pivot = Offset(cx, cy)) {
        when (shape) {
            ConfettiShape.RECTANGLE -> {
                drawRect(
                    color = color,
                    topLeft = Offset(cx - size * 0.3f, cy - size * 0.6f),
                    size = Size(size * 0.6f, size * 1.2f)
                )
            }
            ConfettiShape.CIRCLE -> {
                drawCircle(color, size * 0.5f, Offset(cx, cy))
            }
            ConfettiShape.TRIANGLE -> {
                val path = Path().apply {
                    moveTo(cx, cy - size * 0.6f)
                    lineTo(cx - size * 0.5f, cy + size * 0.4f)
                    lineTo(cx + size * 0.5f, cy + size * 0.4f)
                    close()
                }
                drawPath(path, color)
            }
            ConfettiShape.STAR -> {
                val path = Path().apply {
                    for (i in 0 until 5) {
                        val a = Math.toRadians(i * 72.0 - 90.0)
                        val outerR = size * 0.6f
                        val innerR = size * 0.25f
                        val ox = cx + outerR * cos(a).toFloat()
                        val oy = cy + outerR * sin(a).toFloat()
                        val ix = cx + innerR * cos(a + Math.toRadians(36.0)).toFloat()
                        val iy = cy + innerR * sin(a + Math.toRadians(36.0)).toFloat()
                        if (i == 0) moveTo(ox, oy) else lineTo(ox, oy)
                        lineTo(ix, iy)
                    }
                    close()
                }
                drawPath(path, color)
            }
            ConfettiShape.PETAL -> {
                // Small ellipse for cherry petal
                drawOval(
                    color = color,
                    topLeft = Offset(cx - size * 0.25f, cy - size * 0.5f),
                    size = Size(size * 0.5f, size)
                )
            }
        }
    }
}
