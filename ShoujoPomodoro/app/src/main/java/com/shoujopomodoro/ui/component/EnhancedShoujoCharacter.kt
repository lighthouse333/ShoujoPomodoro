package com.shoujopomodoro.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shoujopomodoro.domain.model.CharacterState
import com.shoujopomodoro.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

// ═══════════════════════════════════════════════
//  Enhanced Shoujo Character — 完整少女角色
//  Multi-style outfits, hair animations,
//  glow effects, rich expressions ✨🎀
// ═══════════════════════════════════════════════

enum class CharacterOutfit {
    SAILOR, PASTEL, MAGICAL
}

@Composable
fun EnhancedShoujoCharacter(
    characterState: CharacterState,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    outfit: CharacterOutfit = CharacterOutfit.SAILOR,
    hairStyle: Int = 0, // 0=twin tails, 1=long straight, 2=short bob
    showGlow: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "char_enhanced")

    val breatheOffset by infiniteTransition.animateFloat(
        0f, 6f, infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "breathe"
    )
    val hairSway by infiniteTransition.animateFloat(
        -3f, 3f, infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "hair"
    )
    val sparkleAlpha by infiniteTransition.animateFloat(
        0.3f, 0.95f, infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "sparkle"
    )
    val glowPulse by infiniteTransition.animateFloat(
        0.6f, 1f, infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "glow"
    )

    var blinkProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(3000L + (Math.random() * 2000).toLong())
            val anim = Animatable(0f)
            anim.animateTo(1f, tween(80))
            anim.animateTo(0f, tween(80))
            if (Math.random() > 0.5) {
                kotlinx.coroutines.delay(180L)
                anim.animateTo(1f, tween(60))
                anim.animateTo(0f, tween(60))
            }
        }
    }

    val stateTransition = remember { Animatable(1f) }
    LaunchedEffect(characterState) {
        stateTransition.snapTo(0.85f)
        stateTransition.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }

    var microOffsetX by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            microOffsetX = (Math.random().toFloat() - 0.5f) * 2f
            kotlinx.coroutines.delay(2000L)
        }
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f
            val breatheY = if (characterState == CharacterState.IDLE) breatheOffset else 0f
            val sc = stateTransition.value

            withTransform({
                translate(left = microOffsetX * sc, top = breatheY)
                scale(sc, sc, pivot = Offset(cx, cy))
            }) {
                if (showGlow) drawAuraGlow(characterState, glowPulse, w, h, cx, cy)
                drawCharEffects(characterState, sparkleAlpha, w, h, cx, cy)
                drawHairBack(w, h, cx, cy, hairStyle, hairSway)
                when (outfit) {
                    CharacterOutfit.SAILOR -> drawSailorUniform(w, h, cx, cy)
                    CharacterOutfit.PASTEL -> drawPastelDress(w, h, cx, cy)
                    CharacterOutfit.MAGICAL -> drawMagicalOutfit(w, h, cx, cy)
                }
                drawFace(w, h, cx, cy)
                drawBlush(w, h, cx, cy, characterState)
                drawEyes(w, h, cx, cy, characterState, blinkProgress)
                drawEyebrows(w, h, cx, cy, characterState)
                drawMouth(w, h, cx, cy, characterState)
                drawHairFront(w, h, cx, cy, hairStyle, hairSway)
                drawAccessories(w, h, cx, cy, characterState, outfit)
            }
        }
    }
}

// ── Glow ──
private fun DrawScope.drawAuraGlow(state: CharacterState, pulse: Float, w: Float, h: Float, cx: Float, cy: Float) {
    val c = when (state) {
        CharacterState.FOCUSING -> SakuraDeep.copy(alpha = 0.08f * pulse)
        CharacterState.RESTING -> MatchaMint.copy(alpha = 0.06f * pulse)
        CharacterState.ALERTING -> CoralRose.copy(alpha = 0.1f * pulse)
        CharacterState.IDLE -> SakuraPink.copy(alpha = 0.04f * pulse)
    }
    drawCircle(c, w * 0.38f * pulse, Offset(cx, cy))
    drawCircle(c.copy(alpha = c.alpha * 0.4f), w * 0.49f * pulse, Offset(cx, cy))
}

// ── Character Effects ──
private fun DrawScope.drawCharEffects(state: CharacterState, alpha: Float, w: Float, h: Float, cx: Float, cy: Float) {
    when (state) {
        CharacterState.FOCUSING -> listOf(
            Offset(cx - w * 0.32f, cy - h * 0.28f), Offset(cx + w * 0.33f, cy - h * 0.32f),
            Offset(cx + w * 0.28f, cy - h * 0.05f), Offset(cx - w * 0.28f, cy + h * 0.02f)
        ).forEach { drawSparkle(it, 7f, KonpeitoGold.copy(alpha = alpha)) }
        CharacterState.RESTING -> {
            drawCircle(WisteriaLavender.copy(alpha = 0.7f), 5f, Offset(cx + w * 0.3f, cy - h * 0.2f))
            drawCircle(WisteriaLavender.copy(alpha = 0.5f), 4f, Offset(cx + w * 0.37f, cy - h * 0.28f))
            drawCircle(WisteriaLavender.copy(alpha = 0.3f), 3f, Offset(cx + w * 0.43f, cy - h * 0.34f))
        }
        CharacterState.ALERTING -> {
            drawCircle(AlertRed.copy(alpha = 0.7f), 4f, Offset(cx - w * 0.28f, cy - h * 0.22f))
            drawCircle(AlertRed.copy(alpha = 0.7f), 3.5f, Offset(cx + w * 0.28f, cy - h * 0.25f))
        }
        else -> {}
    }
}

private fun DrawScope.drawSparkle(center: Offset, size: Float, color: Color) {
    val p = Path().apply {
        moveTo(center.x, center.y - size); lineTo(center.x + size * 0.25f, center.y - size * 0.25f)
        lineTo(center.x + size, center.y); lineTo(center.x + size * 0.25f, center.y + size * 0.25f)
        lineTo(center.x, center.y + size); lineTo(center.x - size * 0.25f, center.y + size * 0.25f)
        lineTo(center.x - size, center.y); lineTo(center.x - size * 0.25f, center.y - size * 0.25f); close()
    }
    drawPath(p, color); drawCircle(color.copy(alpha = color.alpha * 0.3f), size * 0.5f, center)
}

// ── Hair Back ──
private fun DrawScope.drawHairBack(w: Float, h: Float, cx: Float, cy: Float, style: Int, sway: Float) {
    val faceCY = cy - h * 0.06f; val top = faceCY - h * 0.28f; val bottom = faceCY + h * 0.38f
    val color = HairSakura
    when (style) {
        0 -> { // Twin tails
            val l = cx - w * 0.3f; val r = cx + w * 0.3f; val bot2 = faceCY + h * 0.22f
            val hp = Path().apply {
                moveTo(l, top + h * 0.08f); cubicTo(l, top - h * 0.04f, r, top - h * 0.04f, r, top + h * 0.08f)
                cubicTo(r + w * 0.04f, faceCY, r + w * 0.03f, bot2, r - w * 0.02f, bot2)
                cubicTo(r - w * 0.02f, bot2 + h * 0.02f, l + w * 0.02f, bot2 + h * 0.02f, l + w * 0.02f, bot2)
                cubicTo(l - w * 0.03f, bot2, l - w * 0.04f, faceCY, l, top + h * 0.08f); close()
            }
            drawPath(hp, color)
            val tw = w * 0.08f; val th = h * 0.22f
            drawOval(color, Offset(cx - w * 0.22f + sway, faceCY + h * 0.05f), Size(tw, th))
            drawOval(color, Offset(cx + w * 0.14f + sway, faceCY + h * 0.05f), Size(tw, th))
            drawCircle(BowRed, 4f, Offset(cx - w * 0.18f + sway, faceCY + h * 0.07f))
            drawCircle(BowRed, 4f, Offset(cx + w * 0.18f + sway, faceCY + h * 0.07f))
        }
        1 -> { // Long straight
            val sw = w * 0.32f
            val hp = Path().apply {
                moveTo(cx - sw, top + h * 0.08f); cubicTo(cx - sw, top - h * 0.04f, cx + sw, top - h * 0.04f, cx + sw, top + h * 0.08f)
                cubicTo(cx + sw + w * 0.06f, faceCY + h * 0.1f, cx + sw + w * 0.04f + sway, bottom + h * 0.1f, cx + sw + sway * 0.5f, bottom + h * 0.12f)
                cubicTo(cx + sw * 0.5f + sway * 0.3f, bottom + h * 0.08f, cx - sw * 0.5f + sway * 0.3f, bottom + h * 0.08f, cx - sw + sway * 0.5f, bottom + h * 0.12f)
                cubicTo(cx - sw - w * 0.04f + sway, bottom + h * 0.1f, cx - sw - w * 0.06f, faceCY + h * 0.1f, cx - sw, top + h * 0.08f); close()
            }
            drawPath(hp, color)
        }
        2 -> { // Short bob
            val sw = w * 0.28f; val bot2 = faceCY + h * 0.18f
            val hp = Path().apply {
                moveTo(cx - sw, top + h * 0.08f); cubicTo(cx - sw, top - h * 0.03f, cx + sw, top - h * 0.03f, cx + sw, top + h * 0.08f)
                cubicTo(cx + sw + w * 0.04f, faceCY, cx + sw + w * 0.02f + sway, bot2, cx + sw * 0.8f + sway, bot2 + h * 0.02f)
                cubicTo(cx, bot2 + h * 0.03f, cx, bot2 + h * 0.03f, cx - sw * 0.8f + sway, bot2 + h * 0.02f)
                cubicTo(cx - sw - w * 0.02f + sway, bot2, cx - sw - w * 0.04f, faceCY, cx - sw, top + h * 0.08f); close()
            }
            drawPath(hp, color)
        }
    }
}

// ── Outfits ──
private fun DrawScope.drawSailorUniform(w: Float, h: Float, cx: Float, cy: Float) {
    val bt = cy + h * 0.05f; val bh = h * 0.24f; val bw = w * 0.33f; val navy = UniformNavy
    drawPath(Path().apply { moveTo(cx - bw, bt); lineTo(cx, bt + bh * 0.35f); lineTo(cx + bw, bt); close() }, navy)
    repeat(2) { i ->
        val t = (i + 1) / 3f
        drawPath(Path().apply { moveTo(cx - bw * (1f - t * 0.6f), bt + bh * 0.1f * t); lineTo(cx - bw * 0.15f, bt + bh * 0.28f)
            moveTo(cx + bw * 0.15f, bt + bh * 0.28f); lineTo(cx + bw * (1f - t * 0.6f), bt + bh * 0.1f * t) }, Color.White, style = Stroke(2.5f))
    }
    drawPath(Path().apply { moveTo(cx - bw * 0.8f, bt); lineTo(cx + bw * 0.8f, bt); lineTo(cx + bw * 0.65f, bt + bh); lineTo(cx - bw * 0.65f, bt + bh); close() }, navy)
    drawPath(Path().apply { moveTo(cx - bw * 0.7f, bt + bh); lineTo(cx - bw * 0.85f, bt + bh * 1.35f); lineTo(cx + bw * 0.85f, bt + bh * 1.35f); lineTo(cx + bw * 0.7f, bt + bh); close() }, navy)
    drawBow(cx, bt + bh * 0.1f, 11f, BowRed)
}

private fun DrawScope.drawPastelDress(w: Float, h: Float, cx: Float, cy: Float) {
    val bt = cy + h * 0.05f; val bh = h * 0.24f; val bw = w * 0.33f; val dc = WisteriaLight
    drawPath(Path().apply {
        moveTo(cx - bw * 0.7f, bt); cubicTo(cx - bw * 0.7f, bt - bh * 0.05f, cx + bw * 0.7f, bt - bh * 0.05f, cx + bw * 0.7f, bt)
        cubicTo(cx + bw * 0.7f, bt + bh, cx + bw * 0.9f, bt + bh * 1.4f, cx + bw * 0.5f, bt + bh * 1.4f)
        cubicTo(cx + bw * 0.3f, bt + bh * 1.4f, cx + bw * 0.1f, bt + bh, cx, bt + bh * 0.9f)
        cubicTo(cx - bw * 0.1f, bt + bh, cx - bw * 0.3f, bt + bh * 1.4f, cx - bw * 0.5f, bt + bh * 1.4f)
        cubicTo(cx - bw * 0.9f, bt + bh * 1.4f, cx - bw * 0.7f, bt + bh, cx - bw * 0.7f, bt); close()
    }, dc)
    drawLine(RoseBlush, Offset(cx - bw * 0.5f, bt + bh * 1.35f), Offset(cx + bw * 0.5f, bt + bh * 1.35f), 2f)
    drawBow(cx, bt + bh * 0.08f, 8f, SakuraPink)
}

private fun DrawScope.drawMagicalOutfit(w: Float, h: Float, cx: Float, cy: Float) {
    val bt = cy + h * 0.05f; val bh = h * 0.24f; val bw = w * 0.33f
    drawPath(Path().apply {
        moveTo(cx - bw * 0.65f, bt); cubicTo(cx - bw * 0.65f, bt - bh * 0.05f, cx + bw * 0.65f, bt - bh * 0.05f, cx + bw * 0.65f, bt)
        lineTo(cx + bw * 0.5f, bt + bh); lineTo(cx + bw * 0.9f, bt + bh * 1.5f); lineTo(cx - bw * 0.9f, bt + bh * 1.5f); lineTo(cx - bw * 0.5f, bt + bh); close()
    }, SakuraPink)
    drawLine(KonpeitoGold, Offset(cx - bw * 0.65f, bt + bh), Offset(cx + bw * 0.65f, bt + bh), 3f)
    drawBow(cx, bt + bh * 0.15f, 14f, KonpeitoGold)
}

private fun DrawScope.drawBow(x: Float, y: Float, size: Float, color: Color) {
    drawCircle(color.copy(red = color.red * 0.8f, green = color.green * 0.8f, blue = color.blue * 0.8f), size * 0.35f, Offset(x, y))
    drawPath(Path().apply { moveTo(x, y); cubicTo(x - size * 1.6f, y - size, x - size * 1.6f, y + size, x, y + size * 0.25f); close() }, color)
    drawPath(Path().apply { moveTo(x, y); cubicTo(x + size * 1.6f, y - size, x + size * 1.6f, y + size, x, y + size * 0.25f); close() }, color)
}

// ── Face ──
private fun DrawScope.drawFace(w: Float, h: Float, cx: Float, cy: Float) {
    val fcy = cy - h * 0.06f; val fw = w * 0.23f; val fh = h * 0.20f
    drawOval(SkinTone, Offset(cx - fw, fcy - fh), Size(fw * 2, fh * 2))
    drawOval(Color.White.copy(alpha = 0.08f), Offset(cx - fw * 0.5f, fcy - fh * 0.9f), Size(fw, fh * 0.6f))
}

private fun DrawScope.drawBlush(w: Float, h: Float, cx: Float, cy: Float, state: CharacterState) {
    val fcy = cy - h * 0.06f
    val a = when (state) { CharacterState.RESTING -> 0.5f; CharacterState.ALERTING -> 0.45f; else -> 0.25f }
    drawOval(BlushPink.copy(alpha = a), Offset(cx - w * 0.17f, fcy - h * 0.01f), Size(w * 0.055f, h * 0.035f))
    drawOval(BlushPink.copy(alpha = a), Offset(cx + w * 0.115f, fcy - h * 0.01f), Size(w * 0.055f, h * 0.035f))
}

// ── Eyes ──
private fun DrawScope.drawEyes(w: Float, h: Float, cx: Float, cy: Float, state: CharacterState, blink: Float) {
    val fcy = cy - h * 0.06f; val ey = fcy - h * 0.03f; val es = w * 0.085f; val ew = w * 0.065f
    val eh = when (state) { CharacterState.ALERTING -> h * 0.065f; CharacterState.FOCUSING -> h * 0.038f; else -> h * 0.048f }
    val bs = (if (blink > 0.5f) 1f - (blink - 0.5f) * 2f else blink * 2f).coerceIn(0.03f, 1f)

    when (state) {
        CharacterState.IDLE -> { drawSparkleEye(cx - es, ey, ew, eh, bs); drawSparkleEye(cx + es, ey, ew, eh, bs) }
        CharacterState.FOCUSING -> { drawFocusEye(cx - es, ey, ew, eh); drawFocusEye(cx + es, ey, ew, eh) }
        CharacterState.RESTING -> { drawRestEye(cx - es, ey, ew); drawRestEye(cx + es, ey, ew) }
        CharacterState.ALERTING -> { drawAlertEye(cx - es, ey, ew, eh); drawAlertEye(cx + es, ey, ew, eh) }
    }
}

private fun DrawScope.drawSparkleEye(x: Float, y: Float, w: Float, h: Float, by: Float) {
    val sh = h * by
    drawOval(Color.White, Offset(x - w, y - sh), Size(w * 2, sh * 2))
    drawOval(EyeBlue, Offset(x - w * 0.68f, y - sh * 0.68f), Size(w * 1.36f, sh * 1.36f))
    drawOval(Color(0xFF1A1A2E), Offset(x - w * 0.38f, y - sh * 0.48f), Size(w * 0.76f, sh * 0.96f))
    if (sh > 0.15f) {
        drawCircle(Color.White, w * 0.22f, Offset(x - w * 0.22f, y - sh * 0.38f))
        drawCircle(Color.White.copy(alpha = 0.5f), w * 0.08f, Offset(x + w * 0.1f, y + sh * 0.05f))
    }
}

private fun DrawScope.drawFocusEye(x: Float, y: Float, w: Float, h: Float) {
    drawOval(Color.White, Offset(x - w, y - h), Size(w * 2, h * 2))
    drawOval(EyeBlue, Offset(x - w * 0.58f, y - h * 0.38f), Size(w * 1.16f, h * 1.25f))
    drawOval(Color(0xFF1A1A2E), Offset(x - w * 0.33f, y - h * 0.18f), Size(w * 0.66f, h * 0.85f))
    drawCircle(Color.White, w * 0.18f, Offset(x - w * 0.18f, y - h * 0.18f))
}

private fun DrawScope.drawRestEye(x: Float, y: Float, w: Float) {
    drawPath(Path().apply { moveTo(x - w, y); quadraticTo(x, y - w * 0.85f, x + w, y) }, Color(0xFF5D4037), style = Stroke(2.5f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
}

private fun DrawScope.drawAlertEye(x: Float, y: Float, w: Float, h: Float) {
    val aw = w * 1.3f; val ah = h * 1.2f
    drawOval(Color.White, Offset(x - aw, y - ah), Size(aw * 2, ah * 2))
    drawOval(EyeBlue, Offset(x - aw * 0.74f, y - ah * 0.74f), Size(aw * 1.48f, ah * 1.48f))
    drawOval(Color(0xFF1A1A2E), Offset(x - aw * 0.34f, y - ah * 0.38f), Size(aw * 0.68f, ah * 0.78f))
    drawCircle(Color.White, aw * 0.22f, Offset(x - aw * 0.2f, y - ah * 0.3f))
    drawCircle(Color.White, aw * 0.1f, Offset(x + aw * 0.08f, y + ah * 0.1f))
}

// ── Eyebrows ──
private fun DrawScope.drawEyebrows(w: Float, h: Float, cx: Float, cy: Float, state: CharacterState) {
    val fcy = cy - h * 0.06f; val by = fcy - h * 0.09f; val sp = w * 0.085f; val len = w * 0.065f; val c = Color(0xFF5D4037)
    when (state) {
        CharacterState.FOCUSING -> {
            drawLine(c, Offset(cx - sp - len, by + h * 0.008f), Offset(cx - sp + len, by - h * 0.008f), 2.5f)
            drawLine(c, Offset(cx + sp + len, by + h * 0.008f), Offset(cx + sp - len, by - h * 0.008f), 2.5f)
        }
        CharacterState.ALERTING -> {
            drawLine(c, Offset(cx - sp - len * 0.7f, by - h * 0.018f), Offset(cx - sp + len * 0.7f, by - h * 0.018f), 2.5f)
            drawLine(c, Offset(cx + sp + len * 0.7f, by - h * 0.018f), Offset(cx + sp - len * 0.7f, by - h * 0.018f), 2.5f)
        }
        else -> {
            drawLine(c, Offset(cx - sp - len * 0.8f, by), Offset(cx - sp + len * 0.8f, by - h * 0.004f), 2.5f)
            drawLine(c, Offset(cx + sp + len * 0.8f, by), Offset(cx + sp - len * 0.8f, by - h * 0.004f), 2.5f)
        }
    }
}

// ── Mouth ──
private fun DrawScope.drawMouth(w: Float, h: Float, cx: Float, cy: Float, state: CharacterState) {
    val fcy = cy - h * 0.06f; val my = fcy + h * 0.07f; val mw = w * 0.038f
    when (state) {
        CharacterState.IDLE -> drawPath(Path().apply { moveTo(cx - mw, my); quadraticTo(cx, my + mw * 0.5f, cx + mw, my) }, Color(0xFFE57373), style = Stroke(2f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
        CharacterState.FOCUSING -> drawOval(Color(0xFFD32F2F), Offset(cx - mw * 0.55f, my), Size(mw * 1.1f, mw * 1.1f))
        CharacterState.RESTING -> drawPath(Path().apply { moveTo(cx - mw * 1.4f, my - mw * 0.2f); quadraticTo(cx, my + mw * 1.3f, cx + mw * 1.4f, my - mw * 0.2f) }, Color(0xFFE57373), style = Stroke(2.5f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
        CharacterState.ALERTING -> {
            drawOval(Color(0xFFD32F2F), Offset(cx - mw * 0.75f, my - mw * 0.1f), Size(mw * 1.5f, mw * 1.7f))
            drawOval(Color(0xFF5D1A1A), Offset(cx - mw * 0.45f, my + mw * 0.1f), Size(mw * 0.9f, mw * 1.1f))
        }
    }
}

// ── Hair Front ──
private fun DrawScope.drawHairFront(w: Float, h: Float, cx: Float, cy: Float, style: Int, sway: Float) {
    val fcy = cy - h * 0.06f; val fw = w * 0.23f; val fh = h * 0.20f; val ft = fcy - fh; val color = HairSakura
    when (style) {
        0 -> {
            drawPath(Path().apply { moveTo(cx, ft + fh * 0.1f); cubicTo(cx + w * 0.03f, ft - fh * 0.7f, cx + w * 0.02f, ft - fh * 0.5f, cx + w * 0.05f, ft - fh * 0.3f); cubicTo(cx + w * 0.04f, ft - fh * 0.45f, cx + w * 0.02f, ft - fh * 0.55f, cx, ft + fh * 0.1f); close() }, color)
            drawPath(Path().apply { moveTo(cx, ft + fh * 0.1f); quadraticTo(cx - fw * 0.3f, ft + fh * 0.45f, cx - fw * 0.7f, ft + fh * 0.25f); lineTo(cx - fw * 0.78f, ft - fh * 0.45f); quadraticTo(cx, ft - fh * 0.15f, cx + fw * 0.78f, ft - fh * 0.45f); lineTo(cx + fw * 0.7f, ft + fh * 0.25f); quadraticTo(cx + fw * 0.3f, ft + fh * 0.45f, cx, ft + fh * 0.1f); close() }, color)
        }
        1 -> drawPath(Path().apply { moveTo(cx, ft + fh * 0.02f); cubicTo(cx - fw * 0.2f, ft + fh * 0.2f, cx - fw * 0.5f, ft + fh * 0.15f, cx - fw * 0.8f, ft - fh * 0.3f); lineTo(cx - fw * 0.3f, ft - fh * 0.4f); lineTo(cx, ft - fh * 0.1f); lineTo(cx + fw * 0.3f, ft - fh * 0.4f); lineTo(cx + fw * 0.8f, ft - fh * 0.3f); cubicTo(cx + fw * 0.5f, ft + fh * 0.15f, cx + fw * 0.2f, ft + fh * 0.2f, cx, ft + fh * 0.02f); close() }, color)
        2 -> drawPath(Path().apply { moveTo(cx - fw * 0.78f, ft + fh * 0.1f); cubicTo(cx - fw * 0.8f, ft - fh * 0.2f, cx + fw * 0.8f, ft - fh * 0.2f, cx + fw * 0.78f, ft + fh * 0.1f); cubicTo(cx + fw * 0.6f, ft - fh * 0.35f, cx - fw * 0.6f, ft - fh * 0.35f, cx - fw * 0.78f, ft + fh * 0.1f); close() }, color)
    }
    // Side strands (all styles)
    val ls = Path().apply { moveTo(cx - fw * 0.82f, ft + fh * 0.25f); cubicTo(cx - fw * 1.05f + sway * 0.5f, ft + fh * 0.75f, cx - fw * 0.95f + sway, ft + fh * 1.4f, cx - fw * 0.65f + sway, ft + fh * 1.5f); lineTo(cx - fw * 0.45f, ft + fh * 1.15f); cubicTo(cx - fw * 0.65f, ft + fh * 0.75f, cx - fw * 0.65f, ft + fh * 0.45f, cx - fw * 0.55f, ft + fh * 0.25f); close() }
    drawPath(ls, color)
    val rs = Path().apply { moveTo(cx + fw * 0.82f, ft + fh * 0.25f); cubicTo(cx + fw * 1.05f + sway * 0.5f, ft + fh * 0.75f, cx + fw * 0.95f + sway, ft + fh * 1.4f, cx + fw * 0.65f + sway, ft + fh * 1.5f); lineTo(cx + fw * 0.45f, ft + fh * 1.15f); cubicTo(cx + fw * 0.65f, ft + fh * 0.75f, cx + fw * 0.65f, ft + fh * 0.45f, cx + fw * 0.55f, ft + fh * 0.25f); close() }
    drawPath(rs, color)
}

// ── Accessories ──
private fun DrawScope.drawAccessories(w: Float, h: Float, cx: Float, cy: Float, state: CharacterState, outfit: CharacterOutfit) {
    val fcy = cy - h * 0.06f
    when (state) {
        CharacterState.FOCUSING -> {
            drawCircle(MermaidBlue.copy(alpha = 0.5f), 3f, Offset(cx + w * 0.16f, fcy - h * 0.18f))
            drawCircle(MermaidBlue.copy(alpha = 0.3f), 2f, Offset(cx + w * 0.18f, fcy - h * 0.16f))
        }
        CharacterState.ALERTING -> {
            val my = fcy + h * 0.07f
            drawLine(Color(0xFF666666), Offset(cx + w * 0.13f, my - 5f), Offset(cx + w * 0.17f, my - 9f), 1.5f)
            drawLine(Color(0xFF666666), Offset(cx + w * 0.14f, my), Offset(cx + w * 0.18f, my - 3f), 1.5f)
        }
        else -> {}
    }
    when (outfit) {
        CharacterOutfit.MAGICAL -> {
            drawCircle(KonpeitoGold.copy(alpha = 0.6f), 2.5f, Offset(cx - w * 0.35f, cy + h * 0.18f))
            drawCircle(KonpeitoGold.copy(alpha = 0.4f), 2f, Offset(cx + w * 0.35f, cy + h * 0.18f))
        }
        else -> {}
    }
}
