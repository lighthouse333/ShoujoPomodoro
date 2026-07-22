package com.shoujopomodoro.ui.component

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.math.cos

// ═══════════════════════════════════════════════
//  AGSL Gradient Backgrounds
//  GPU-accelerated animated gradient shaders
//  for a dreamy, magical-girl atmosphere ✨
// ═══════════════════════════════════════════════

/**
 * Aurora gradient — flowing northern lights effect.
 * Soft pastel waves that slowly shift across the screen.
 */
@Composable
fun AuroraGradientBackground(
    modifier: Modifier = Modifier,
    speed: Float = 0.3f
) {
    val time = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            time.floatValue += 0.016f * speed
            delay(16)
        }
    }

    val shader = remember {
        RuntimeShader(AURORA_SHADER_SRC)
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        shader.setFloatUniform("uResolution", size.width, size.height)
        shader.setFloatUniform("uTime", time.floatValue)
        drawRect(brush = ShaderBrush(shader))
    }
}

/**
 * Soft gradient background that shifts between pastel colors.
 * Works on all API levels (fallback to Compose gradient).
 */
@Composable
fun SoftGradientBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    val time = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            time.floatValue += 0.005f
            delay(33)
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val shaderSrc = if (isDark) DARK_GRADIENT_SHADER else LIGHT_GRADIENT_SHADER
        val shader = remember { RuntimeShader(shaderSrc) }

        Canvas(modifier = modifier.fillMaxSize()) {
            shader.setFloatUniform("uResolution", size.width, size.height)
            shader.setFloatUniform("uTime", time.floatValue)
            drawRect(brush = ShaderBrush(shader))
        }
    } else {
        // Fallback: use Compose gradient with animated colors via recomposition
        AnimatedComposeGradientBackground(modifier = modifier, isDark = isDark)
    }
}

/**
 * Fallback gradient for pre-Android 13 devices.
 * Slightly less performant but visually comparable.
 */
@Composable
private fun AnimatedComposeGradientBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    val time = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            time.floatValue += 0.01f
            delay(33)
        }
    }

    val t = time.floatValue
    val gradient = if (isDark) {
        listOf(
            androidx.compose.ui.graphics.Color(
                red = (0.06f + sin(t * 0.3f) * 0.03f).coerceIn(0f, 1f),
                green = (0.05f + sin(t * 0.5f + 1f) * 0.03f).coerceIn(0f, 1f),
                blue = (0.12f + sin(t * 0.4f + 2f) * 0.05f).coerceIn(0f, 1f),
                alpha = 1f
            ),
            androidx.compose.ui.graphics.Color(
                red = (0.15f + sin(t * 0.4f + 0.5f) * 0.05f).coerceIn(0f, 1f),
                green = (0.05f + sin(t * 0.3f + 1.5f) * 0.03f).coerceIn(0f, 1f),
                blue = (0.25f + sin(t * 0.5f + 1f) * 0.07f).coerceIn(0f, 1f),
                alpha = 1f
            ),
        )
    } else {
        listOf(
            androidx.compose.ui.graphics.Color(
                red = (1f + sin(t * 0.3f) * 0.03f).coerceIn(0f, 1f),
                green = (0.94f + sin(t * 0.5f + 1f) * 0.04f).coerceIn(0f, 1f),
                blue = (0.96f + sin(t * 0.4f + 2f) * 0.04f).coerceIn(0f, 1f),
                alpha = 1f
            ),
            androidx.compose.ui.graphics.Color(
                red = (0.96f + sin(t * 0.4f + 0.5f) * 0.04f).coerceIn(0f, 1f),
                green = (0.88f + sin(t * 0.3f + 1.5f) * 0.05f).coerceIn(0f, 1f),
                blue = (0.96f + sin(t * 0.5f + 1f) * 0.04f).coerceIn(0f, 1f),
                alpha = 1f
            ),
        )
    }

    val brush = androidx.compose.ui.graphics.Brush.linearGradient(
        colors = gradient,
        start = Offset(0f, 0f),
        end = Offset(1000f, 2000f)
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(brush = brush)
    }
}

// ═══════════════════════════════════════════════
//  AGSL Shader Sources
// ═══════════════════════════════════════════════

private val AURORA_SHADER_SRC = """
    uniform float2 uResolution;
    uniform float uTime;

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / uResolution;

        // Multiple wave layers at different scales
        float wave1 = sin(uv.x * 3.0 + uTime * 0.3) * cos(uv.y * 2.5 + uTime * 0.2) * 0.5 + 0.5;
        float wave2 = sin(uv.x * 5.0 - uTime * 0.5) * cos(uv.y * 4.0 + uTime * 0.4) * 0.3 + 0.5;
        float wave3 = sin(uv.y * 6.0 + uTime * 0.35) * cos(uv.x * 3.5 - uTime * 0.25) * 0.2 + 0.5;

        float noise = (wave1 * 0.5 + wave2 * 0.3 + wave3 * 0.2);

        // Pastel sunset colors
        half3 color1 = half3(1.0, 0.72, 0.77);  // Sakura pink
        half3 color2 = half3(0.85, 0.75, 0.85);  // Lavender
        half3 color3 = half3(0.70, 0.92, 0.95);  // Sky blue

        half3 color = mix(color1, color2, smoothstep(0.3, 0.7, noise));
        color = mix(color, color3, smoothstep(0.5, 0.8, uv.y));

        // Add subtle sparkle
        float sparkle = sin(uv.x * 100.0 + uTime) * sin(uv.y * 100.0 + uTime * 1.3);
        sparkle = smoothstep(0.95, 1.0, abs(sparkle));
        color += half3(sparkle * 0.05);

        return half4(color, 1.0);
    }
""".trimIndent()

private val LIGHT_GRADIENT_SHADER = """
    uniform float2 uResolution;
    uniform float uTime;

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / uResolution;

        float angle = uTime * 0.1;
        float2 center = float2(0.5 + sin(angle) * 0.1, 0.5 + cos(angle * 1.3) * 0.08);
        float dist = distance(uv, center);

        half3 color1 = half3(1.0, 0.96, 0.97);  // Light pink
        half3 color2 = half3(0.95, 0.90, 0.98);  // Light lavender
        half3 color3 = half3(0.93, 0.97, 1.0);   // Light sky

        float t = dist * 1.5 + sin(uv.x * 2.0 + uTime * 0.2) * 0.1;
        half3 color = mix(color1, color2, smoothstep(0.2, 0.6, t));
        color = mix(color, color3, smoothstep(0.5, 0.9, t));

        // Soft vignette
        float vignette = 1.0 - smoothstep(0.4, 1.2, dist) * 0.15;
        color *= vignette;

        return half4(color, 1.0);
    }
""".trimIndent()

private val DARK_GRADIENT_SHADER = """
    uniform float2 uResolution;
    uniform float uTime;

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / uResolution;

        float angle = uTime * 0.08;
        float2 center = float2(0.5 + sin(angle) * 0.08, 0.5 + cos(angle * 1.2) * 0.06);
        float dist = distance(uv, center);

        half3 color1 = half3(0.05, 0.04, 0.08);   // Deep navy
        half3 color2 = half3(0.10, 0.06, 0.16);   // Indigo
        half3 color3 = half3(0.12, 0.04, 0.10);   // Dark plum

        float t = dist * 1.3 + sin(uv.y * 3.0 - uTime * 0.15) * 0.08;
        half3 color = mix(color1, color2, smoothstep(0.3, 0.7, t));
        color = mix(color, color3, smoothstep(0.6, 1.0, t));

        // Subtle stars
        float star = sin(uv.x * 200.0) * sin(uv.y * 200.0);
        star = smoothstep(0.998, 1.0, abs(star));
        color += half3(star * 0.03, star * 0.02, star * 0.05);

        float vignette = 1.0 - smoothstep(0.4, 1.2, dist) * 0.3;
        color *= vignette;

        return half4(color, 1.0);
    }
""".trimIndent()
