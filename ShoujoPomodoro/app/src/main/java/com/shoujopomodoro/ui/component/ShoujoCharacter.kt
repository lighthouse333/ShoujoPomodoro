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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.shoujopomodoro.domain.model.CharacterState
import com.shoujopomodoro.ui.theme.BlushPink
import com.shoujopomodoro.ui.theme.BowRed
import com.shoujopomodoro.ui.theme.EyeBlue
import com.shoujopomodoro.ui.theme.HairPink
import com.shoujopomodoro.ui.theme.SkinTone
import com.shoujopomodoro.ui.theme.UniformNavy
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ShoujoCharacter(
    characterState: CharacterState,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 220.dp
) {
    // Infinite animations
    val infiniteTransition = rememberInfiniteTransition(label = "character_idle")

    val breatheOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
    )

    // Blink animation
    var blinkProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(3000L)
            // Blink
            val anim = Animatable(0f)
            anim.animateTo(1f, tween(100))
            anim.animateTo(0f, tween(100))
            kotlinx.coroutines.delay(200L)
            // Double blink sometimes
            if (Math.random() > 0.6) {
                anim.animateTo(1f, tween(80))
                anim.animateTo(0f, tween(80))
            }
        }
    }

    // State transition animation
    val stateTransition = remember { Animatable(1f) }
    LaunchedEffect(characterState) {
        stateTransition.snapTo(0.85f)
        stateTransition.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f

            // Apply breathing and state transition
            val breatheY = if (characterState == CharacterState.IDLE) breatheOffset else 0f
            val scale = stateTransition.value

            withTransform({
                translate(top = breatheY)
                scale(scale, scale, pivot = Offset(cx, cy))
            }) {
                // Draw character layers
                drawBackgroundEffects(characterState, sparkleAlpha, w, h, cx, cy)
                drawHairBack(w, h, cx, cy)
                drawBody(w, h, cx, cy)
                drawFace(w, h, cx, cy)
                drawBlush(w, h, cx, cy, characterState)
                drawEyes(w, h, cx, cy, characterState, blinkProgress)
                drawEyebrows(w, h, cx, cy, characterState)
                drawMouth(w, h, cx, cy, characterState)
                drawHairFront(w, h, cx, cy)
                drawAccessories(w, h, cx, cy, characterState)
            }
        }
    }
}

// ============================================================
// Background effects
// ============================================================

private fun DrawScope.drawBackgroundEffects(
    state: CharacterState,
    sparkleAlpha: Float,
    w: Float, h: Float, cx: Float, cy: Float
) {
    when (state) {
        CharacterState.FOCUSING -> drawSparkles(w, h, cx, cy, sparkleAlpha)
        CharacterState.RESTING -> drawZzzBubbles(w, h, cx, cy)
        CharacterState.ALERTING -> drawExclamationMarks(w, h, cx, cy)
        CharacterState.IDLE -> { /* no effects */ }
    }
}

private fun DrawScope.drawSparkles(w: Float, h: Float, cx: Float, cy: Float, alpha: Float) {
    val sparkleColor = Color(0xFFFFD700).copy(alpha = alpha)
    val positions = listOf(
        Offset(cx - w * 0.35f, cy - h * 0.3f),
        Offset(cx + w * 0.35f, cy - h * 0.35f),
        Offset(cx + w * 0.3f, cy - h * 0.1f),
        Offset(cx - w * 0.3f, cy - h * 0.05f)
    )
    positions.forEach { pos ->
        drawSparkle(pos, 8f, sparkleColor)
    }
}

private fun DrawScope.drawSparkle(center: Offset, size: Float, color: Color) {
    val path = Path().apply {
        // 4-pointed star
        moveTo(center.x, center.y - size)
        lineTo(center.x + size * 0.3f, center.y - size * 0.3f)
        lineTo(center.x + size, center.y)
        lineTo(center.x + size * 0.3f, center.y + size * 0.3f)
        lineTo(center.x, center.y + size)
        lineTo(center.x - size * 0.3f, center.y + size * 0.3f)
        lineTo(center.x - size, center.y)
        lineTo(center.x - size * 0.3f, center.y - size * 0.3f)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawZzzBubbles(w: Float, h: Float, cx: Float, cy: Float) {
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.argb(180, 147, 112, 219)
        textSize = 28f
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
    }
    drawContext.canvas.nativeCanvas.apply {
        drawText("Z", cx + w * 0.3f, cy - h * 0.2f, paint)
        drawText("z", cx + w * 0.38f, cy - h * 0.3f, paint.apply { textSize = 20f })
        drawText("z", cx + w * 0.44f, cy - h * 0.37f, paint.apply { textSize = 14f })
    }
}

private fun DrawScope.drawExclamationMarks(w: Float, h: Float, cx: Float, cy: Float) {
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.argb(220, 255, 69, 58)
        textSize = 32f
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
        isFakeBoldText = true
    }
    drawContext.canvas.nativeCanvas.apply {
        drawText("!", cx - w * 0.3f, cy - h * 0.25f, paint)
        drawText("!", cx + w * 0.3f, cy - h * 0.28f, paint)
    }
}

// ============================================================
// Body / Uniform
// ============================================================

private fun DrawScope.drawBody(w: Float, h: Float, cx: Float, cy: Float) {
    // Body starts below face center
    val bodyTop = cy + h * 0.05f
    val bodyHeight = h * 0.25f
    val bodyWidth = w * 0.35f

    // Sailor collar
    val collarPath = Path().apply {
        moveTo(cx - bodyWidth, bodyTop)
        lineTo(cx, bodyTop + bodyHeight * 0.4f)
        lineTo(cx + bodyWidth, bodyTop)
        close()
    }
    drawPath(collarPath, UniformNavy)

    // Main uniform body
    val bodyPath = Path().apply {
        moveTo(cx - bodyWidth * 0.85f, bodyTop)
        lineTo(cx + bodyWidth * 0.85f, bodyTop)
        lineTo(cx + bodyWidth * 0.7f, bodyTop + bodyHeight)
        lineTo(cx - bodyWidth * 0.7f, bodyTop + bodyHeight)
        close()
    }
    drawPath(bodyPath, UniformNavy)

    // Bow at neck
    drawBow(cx, bodyTop + bodyHeight * 0.15f, 12f)

    // White stripes on collar
    val stripePath = Path().apply {
        moveTo(cx - bodyWidth * 0.6f, bodyTop + bodyHeight * 0.12f)
        lineTo(cx - bodyWidth * 0.15f, bodyTop + bodyHeight * 0.3f)
        moveTo(cx + bodyWidth * 0.15f, bodyTop + bodyHeight * 0.3f)
        lineTo(cx + bodyWidth * 0.6f, bodyTop + bodyHeight * 0.12f)
    }
    drawPath(stripePath, Color.White, style = Stroke(width = 3f))
}

private fun DrawScope.drawBow(x: Float, y: Float, size: Float) {
    // Center knot
    drawCircle(Color(0xFFCC1144), size * 0.4f, Offset(x, y))
    // Left wing
    val leftWing = Path().apply {
        moveTo(x, y)
        cubicTo(x - size * 1.5f, y - size, x - size * 1.5f, y + size, x, y + size * 0.3f)
        close()
    }
    drawPath(leftWing, BowRed)
    // Right wing
    val rightWing = Path().apply {
        moveTo(x, y)
        cubicTo(x + size * 1.5f, y - size, x + size * 1.5f, y + size, x, y + size * 0.3f)
        close()
    }
    drawPath(rightWing, BowRed)
}

// ============================================================
// Hair (back layer)
// ============================================================

private fun DrawScope.drawHairBack(w: Float, h: Float, cx: Float, cy: Float) {
    val hairColor = HairPink
    val faceCenterY = cy - h * 0.08f

    // Main back hair — large rounded shape behind face
    val hairPath = Path().apply {
        val top = faceCenterY - h * 0.3f
        val left = cx - w * 0.32f
        val right = cx + w * 0.32f
        val bottom = faceCenterY + h * 0.35f

        moveTo(left, top + h * 0.1f)
        // Top curve
        cubicTo(left, top - h * 0.05f, right, top - h * 0.05f, right, top + h * 0.1f)
        // Right side flowing down
        cubicTo(right + w * 0.05f, faceCenterY, right + w * 0.08f, bottom - h * 0.05f, right + w * 0.03f, bottom)
        // Bottom curve
        cubicTo(right + w * 0.02f, bottom + h * 0.02f, left - w * 0.02f, bottom + h * 0.02f, left - w * 0.03f, bottom)
        // Left side flowing down
        cubicTo(left - w * 0.08f, bottom - h * 0.05f, left - w * 0.05f, faceCenterY, left, top + h * 0.1f)
        close()
    }
    drawPath(hairPath, hairColor)
}

// ============================================================
// Face
// ============================================================

private fun DrawScope.drawFace(w: Float, h: Float, cx: Float, cy: Float) {
    val faceCenterY = cy - h * 0.08f
    val faceWidth = w * 0.24f
    val faceHeight = h * 0.22f

    // Face oval
    drawOval(
        color = SkinTone,
        topLeft = Offset(cx - faceWidth, faceCenterY - faceHeight),
        size = Size(faceWidth * 2, faceHeight * 2)
    )
}

// ============================================================
// Blush
// ============================================================

private fun DrawScope.drawBlush(w: Float, h: Float, cx: Float, cy: Float, state: CharacterState) {
    val faceCenterY = cy - h * 0.08f
    val blushAlpha = if (state == CharacterState.RESTING) 0.5f else 0.3f

    // Left blush
    drawOval(
        color = BlushPink.copy(alpha = blushAlpha),
        topLeft = Offset(cx - w * 0.18f, faceCenterY - h * 0.02f),
        size = Size(w * 0.06f, h * 0.04f)
    )
    // Right blush
    drawOval(
        color = BlushPink.copy(alpha = blushAlpha),
        topLeft = Offset(cx + w * 0.12f, faceCenterY - h * 0.02f),
        size = Size(w * 0.06f, h * 0.04f)
    )
}

// ============================================================
// Eyes
// ============================================================

private fun DrawScope.drawEyes(
    w: Float, h: Float, cx: Float, cy: Float,
    state: CharacterState, blink: Float
) {
    val faceCenterY = cy - h * 0.08f
    val eyeY = faceCenterY - h * 0.04f
    val eyeSpacing = w * 0.09f
    val eyeWidth = w * 0.07f
    val eyeHeight = when (state) {
        CharacterState.ALERTING -> h * 0.07f
        CharacterState.FOCUSING -> h * 0.04f
        else -> h * 0.05f
    }

    // Blink scale: 1.0 = open, 0.0 = closed
    val blinkScaleY = if (blink > 0.5f) {
        1f - (blink - 0.5f) * 2f  // closing -> 0
    } else {
        blink * 2f  // opening -> 1
    }.coerceIn(0.05f, 1f)

    when (state) {
        CharacterState.IDLE -> {
            drawOpenEye(cx - eyeSpacing, eyeY, eyeWidth, eyeHeight, blinkScaleY, lookRight = false)
            drawOpenEye(cx + eyeSpacing, eyeY, eyeWidth, eyeHeight, blinkScaleY, lookRight = false)
        }
        CharacterState.FOCUSING -> {
            drawFocusEye(cx - eyeSpacing, eyeY, eyeWidth, eyeHeight)
            drawFocusEye(cx + eyeSpacing, eyeY, eyeWidth, eyeHeight)
        }
        CharacterState.RESTING -> {
            drawRestingEye(cx - eyeSpacing, eyeY, eyeWidth)
            drawRestingEye(cx + eyeSpacing, eyeY, eyeWidth)
        }
        CharacterState.ALERTING -> {
            drawAlertEye(cx - eyeSpacing, eyeY, eyeWidth, eyeHeight)
            drawAlertEye(cx + eyeSpacing, eyeY, eyeWidth, eyeHeight)
        }
    }
}

private fun DrawScope.drawOpenEye(
    x: Float, y: Float, width: Float, height: Float,
    blinkScaleY: Float, lookRight: Boolean
) {
    val scaledHeight = height * blinkScaleY

    // White of the eye
    drawOval(
        color = Color.White,
        topLeft = Offset(x - width, y - scaledHeight),
        size = Size(width * 2, scaledHeight * 2)
    )
    // Iris outline
    drawOval(
        color = EyeBlue,
        topLeft = Offset(x - width * 0.7f, y - scaledHeight * 0.7f),
        size = Size(width * 1.4f, scaledHeight * 1.4f)
    )
    // Pupil
    drawOval(
        color = Color(0xFF1A1A2E),
        topLeft = Offset(x - width * 0.4f, y - scaledHeight * 0.5f),
        size = Size(width * 0.8f, scaledHeight * 1.0f)
    )
    // Highlight
    if (scaledHeight > 0.2f) {
        drawCircle(
            color = Color.White,
            radius = width * 0.25f,
            center = Offset(x - width * 0.25f, y - scaledHeight * 0.4f)
        )
    }
}

private fun DrawScope.drawFocusEye(
    x: Float, y: Float, width: Float, height: Float
) {
    // Narrower, more determined eye shape
    drawOval(
        color = Color.White,
        topLeft = Offset(x - width, y - height),
        size = Size(width * 2, height * 2)
    )
    // Iris — slightly smaller, looking slightly down
    drawOval(
        color = EyeBlue,
        topLeft = Offset(x - width * 0.6f, y - height * 0.4f),
        size = Size(width * 1.2f, height * 1.3f)
    )
    // Pupil
    drawOval(
        color = Color(0xFF1A1A2E),
        topLeft = Offset(x - width * 0.35f, y - height * 0.2f),
        size = Size(width * 0.7f, height * 0.9f)
    )
    // Highlight
    drawCircle(
        color = Color.White,
        radius = width * 0.2f,
        center = Offset(x - width * 0.2f, y - height * 0.2f)
    )
}

private fun DrawScope.drawRestingEye(x: Float, y: Float, width: Float) {
    // Closed happy eye — inverted U arc
    val eyePath = Path().apply {
        moveTo(x - width, y)
        quadraticTo(x, y - width * 0.8f, x + width, y)
    }
    drawPath(
        eyePath,
        color = Color(0xFF444444),
        style = Stroke(width = 2.5f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    )
}

private fun DrawScope.drawAlertEye(
    x: Float, y: Float, width: Float, height: Float
) {
    // Extra-large eyes for alert state
    val alertWidth = width * 1.3f
    val alertHeight = height * 1.2f

    drawOval(
        color = Color.White,
        topLeft = Offset(x - alertWidth, y - alertHeight),
        size = Size(alertWidth * 2, alertHeight * 2)
    )
    // Iris
    drawOval(
        color = EyeBlue,
        topLeft = Offset(x - alertWidth * 0.75f, y - alertHeight * 0.75f),
        size = Size(alertWidth * 1.5f, alertHeight * 1.5f)
    )
    // Small pupil (shocked)
    drawOval(
        color = Color(0xFF1A1A2E),
        topLeft = Offset(x - alertWidth * 0.35f, y - alertHeight * 0.4f),
        size = Size(alertWidth * 0.7f, alertHeight * 0.8f)
    )
    // Double highlight
    drawCircle(Color.White, alertWidth * 0.22f, Offset(x - alertWidth * 0.2f, y - alertHeight * 0.3f))
    drawCircle(Color.White, alertWidth * 0.1f, Offset(x + alertWidth * 0.1f, y + alertHeight * 0.1f))
}

// ============================================================
// Eyebrows
// ============================================================

private fun DrawScope.drawEyebrows(w: Float, h: Float, cx: Float, cy: Float, state: CharacterState) {
    val faceCenterY = cy - h * 0.08f
    val browY = faceCenterY - h * 0.1f
    val browSpacing = w * 0.09f
    val browLength = w * 0.07f
    val browColor = Color(0xFF5D4037)

    when (state) {
        CharacterState.FOCUSING -> {
            // Angled inward (determined)
            drawLine(browColor, Offset(cx - browSpacing - browLength, browY + h * 0.01f), Offset(cx - browSpacing + browLength, browY - h * 0.01f), 2.5f)
            drawLine(browColor, Offset(cx + browSpacing + browLength, browY + h * 0.01f), Offset(cx + browSpacing - browLength, browY - h * 0.01f), 2.5f)
        }
        CharacterState.ALERTING -> {
            // Raised high
            drawLine(browColor, Offset(cx - browSpacing - browLength * 0.7f, browY - h * 0.02f), Offset(cx - browSpacing + browLength * 0.7f, browY - h * 0.02f), 2.5f)
            drawLine(browColor, Offset(cx + browSpacing + browLength * 0.7f, browY - h * 0.02f), Offset(cx + browSpacing - browLength * 0.7f, browY - h * 0.02f), 2.5f)
        }
        else -> {
            // Neutral
            drawLine(browColor, Offset(cx - browSpacing - browLength * 0.8f, browY), Offset(cx - browSpacing + browLength * 0.8f, browY - h * 0.005f), 2.5f)
            drawLine(browColor, Offset(cx + browSpacing + browLength * 0.8f, browY), Offset(cx + browSpacing - browLength * 0.8f, browY - h * 0.005f), 2.5f)
        }
    }
}

// ============================================================
// Mouth
// ============================================================

private fun DrawScope.drawMouth(w: Float, h: Float, cx: Float, cy: Float, state: CharacterState) {
    val faceCenterY = cy - h * 0.08f
    val mouthY = faceCenterY + h * 0.08f
    val mouthWidth = w * 0.04f

    when (state) {
        CharacterState.IDLE -> {
            // Small gentle smile
            val mouthPath = Path().apply {
                moveTo(cx - mouthWidth, mouthY)
                quadraticTo(cx, mouthY + mouthWidth * 0.5f, cx + mouthWidth, mouthY)
            }
            drawPath(mouthPath, Color(0xFFE57373), style = Stroke(width = 2f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
        }
        CharacterState.FOCUSING -> {
            // Small determined "o" or tight mouth
            drawOval(
                color = Color(0xFFD32F2F),
                topLeft = Offset(cx - mouthWidth * 0.6f, mouthY),
                size = Size(mouthWidth * 1.2f, mouthWidth * 1.2f)
            )
        }
        CharacterState.RESTING -> {
            // Wide relaxed smile
            val mouthPath = Path().apply {
                moveTo(cx - mouthWidth * 1.5f, mouthY - mouthWidth * 0.2f)
                quadraticTo(cx, mouthY + mouthWidth * 1.2f, cx + mouthWidth * 1.5f, mouthY - mouthWidth * 0.2f)
            }
            drawPath(mouthPath, Color(0xFFE57373), style = Stroke(width = 2.5f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
        }
        CharacterState.ALERTING -> {
            // Open mouth (shocked/speaking)
            drawOval(
                color = Color(0xFFD32F2F),
                topLeft = Offset(cx - mouthWidth * 0.8f, mouthY - mouthWidth * 0.1f),
                size = Size(mouthWidth * 1.6f, mouthWidth * 1.8f)
            )
            // Inner dark
            drawOval(
                color = Color(0xFF5D1A1A),
                topLeft = Offset(cx - mouthWidth * 0.5f, mouthY + mouthWidth * 0.1f),
                size = Size(mouthWidth * 1.0f, mouthWidth * 1.2f)
            )
        }
    }
}

// ============================================================
// Hair (front layer)
// ============================================================

private fun DrawScope.drawHairFront(w: Float, h: Float, cx: Float, cy: Float) {
    val hairColor = HairPink
    val faceCenterY = cy - h * 0.08f
    val faceWidth = w * 0.24f
    val faceHeight = h * 0.22f
    val faceTop = faceCenterY - faceHeight

    // Bangs — triangular/curved shapes covering upper forehead
    val bangsPath = Path().apply {
        // Center bang
        moveTo(cx, faceTop + faceHeight * 0.1f)
        quadraticTo(cx - faceWidth * 0.3f, faceTop + faceHeight * 0.5f, cx - faceWidth * 0.7f, faceTop + faceHeight * 0.3f)
        lineTo(cx - faceWidth * 0.8f, faceTop - faceHeight * 0.5f)
        quadraticTo(cx, faceTop - faceHeight * 0.2f, cx + faceWidth * 0.8f, faceTop - faceHeight * 0.5f)
        lineTo(cx + faceWidth * 0.7f, faceTop + faceHeight * 0.3f)
        quadraticTo(cx + faceWidth * 0.3f, faceTop + faceHeight * 0.5f, cx, faceTop + faceHeight * 0.1f)
        close()
    }
    drawPath(bangsPath, hairColor)

    // Side strands
    val leftStrand = Path().apply {
        moveTo(cx - faceWidth * 0.85f, faceTop + faceHeight * 0.3f)
        cubicTo(
            cx - faceWidth * 1.1f, faceTop + faceHeight * 0.8f,
            cx - faceWidth * 1.0f, faceTop + faceHeight * 1.5f,
            cx - faceWidth * 0.7f, faceTop + faceHeight * 1.6f
        )
        lineTo(cx - faceWidth * 0.5f, faceTop + faceHeight * 1.2f)
        cubicTo(
            cx - faceWidth * 0.7f, faceTop + faceHeight * 0.8f,
            cx - faceWidth * 0.7f, faceTop + faceHeight * 0.5f,
            cx - faceWidth * 0.6f, faceTop + faceHeight * 0.3f
        )
        close()
    }
    drawPath(leftStrand, hairColor)

    val rightStrand = Path().apply {
        moveTo(cx + faceWidth * 0.85f, faceTop + faceHeight * 0.3f)
        cubicTo(
            cx + faceWidth * 1.1f, faceTop + faceHeight * 0.8f,
            cx + faceWidth * 1.0f, faceTop + faceHeight * 1.5f,
            cx + faceWidth * 0.7f, faceTop + faceHeight * 1.6f
        )
        lineTo(cx + faceWidth * 0.5f, faceTop + faceHeight * 1.2f)
        cubicTo(
            cx + faceWidth * 0.7f, faceTop + faceHeight * 0.8f,
            cx + faceWidth * 0.7f, faceTop + faceHeight * 0.5f,
            cx + faceWidth * 0.6f, faceTop + faceHeight * 0.3f
        )
        close()
    }
    drawPath(rightStrand, hairColor)
}

// ============================================================
// Accessories
// ============================================================

private fun DrawScope.drawAccessories(w: Float, h: Float, cx: Float, cy: Float, state: CharacterState) {
    val faceCenterY = cy - h * 0.08f

    when (state) {
        CharacterState.FOCUSING -> {
            // Sweat drop
            val sweatX = cx + w * 0.18f
            val sweatY = faceCenterY - h * 0.2f
            val sweatPath = Path().apply {
                moveTo(sweatX, sweatY - 6f)
                cubicTo(sweatX - 4f, sweatY, sweatX - 4f, sweatY + 8f, sweatX, sweatY + 10f)
                cubicTo(sweatX + 4f, sweatY + 8f, sweatX + 4f, sweatY, sweatX, sweatY - 6f)
                close()
            }
            drawPath(sweatPath, Color(0xCC87CEEB))
        }
        CharacterState.ALERTING -> {
            // Small speech lines near mouth
            val mouthY = faceCenterY + h * 0.08f
            val lineX = cx + w * 0.12f
            drawLine(Color(0xFF444444), Offset(lineX, mouthY - 4f), Offset(lineX + 6f, mouthY - 8f), 1.5f)
            drawLine(Color(0xFF444444), Offset(lineX + 2f, mouthY + 1f), Offset(lineX + 6f, mouthY - 2f), 1.5f)
        }
        else -> {}
    }
}

private fun DrawScope.drawLine(color: Color, start: Offset, end: Offset, strokeWidth: Float) {
    drawLine(color, start, end, strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
}
