package com.shoujopomodoro.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp

@Composable
fun AnimeDecoration(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            // Floating hearts
            val heartPath = Path().apply {
                moveTo(0f, 0f)
                cubicTo(0.5f, 1f, 1f, 0.5f, 1f, 0f)
                cubicTo(0.5f, -0.5f, 0f, -1f, 0f, 0f)
            }

            // Heart 1 - pink
            withTransform({
                translate(left = w * 0.2f, top = h * 0.3f)
                scale(20f, 20f)
            }) {
                drawPath(heartPath, Color(0x66FF69B4))
            }

            // Heart 2 - lavender
            withTransform({
                translate(left = w * 0.7f, top = h * 0.4f)
                scale(15f, 15f)
            }) {
                drawPath(heartPath, Color(0x66D8BFD8))
            }

            // Heart 3 - yellow
            withTransform({
                translate(left = w * 0.4f, top = h * 0.6f)
                scale(12f, 12f)
            }) {
                drawPath(heartPath, Color(0x66FFFF00))
            }

            // Star
            val starPath = Path().apply {
                moveTo(0f, -10f)
                lineTo(3f, -3f)
                lineTo(10f, -3f)
                lineTo(5f, 2f)
                lineTo(7f, 10f)
                lineTo(0f, 6f)
                lineTo(-7f, 10f)
                lineTo(-5f, 2f)
                lineTo(-10f, -3f)
                lineTo(-3f, -3f)
                close()
            }

            withTransform({
                translate(left = w * 0.8f, top = h * 0.2f)
                scale(2.5f, 2.5f)
            }) {
                drawPath(starPath, Color(0x80FFD700))
            }
        }
    }
}
