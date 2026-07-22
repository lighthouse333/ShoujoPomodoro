package com.shoujopomodoro.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shoujopomodoro.ui.theme.ShoujoPink
import com.shoujopomodoro.ui.theme.ShoujoLavender

@Composable
fun AnimeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    trackHeight: Dp = 6.dp,
    thumbSize: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        // Track background
        Canvas(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
                .height(trackHeight)
                .padding(end = 8.dp)
                .clip(CircleShape)
        ) {
            drawRect(
                color = ShoujoLavender.copy(alpha = 0.3f),
                topLeft = Offset(0f, 0f),
                size = androidx.compose.ui.geometry.Size(size.width, size.height)
            )
        }
        
        // Slider with custom styling
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.align(Alignment.Center),
            colors = SliderDefaults.colors(
                thumbColor = ShoujoPink,
                activeTrackColor = ShoujoPink,
                inactiveTrackColor = ShoujoLavender.copy(alpha = 0.5f)
            )
        )
        
        // Thumb decoration
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(thumbSize)
                .clip(CircleShape)
                .background(ShoujoPink, CircleShape)
                .padding(2.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2 - 2
                
                // Inner circle
                drawCircle(
                    color = Color.White,
                    radius = radius * 0.6f,
                    center = center
                )
                
                // Sparkle effect
                drawCircle(
                    color = Color(0xFFD4AF37), // Gold
                    radius = radius * 0.2f,
                    center = Offset(center.x + radius * 0.4f, center.y - radius * 0.4f)
                )
            }
        }
    }
}