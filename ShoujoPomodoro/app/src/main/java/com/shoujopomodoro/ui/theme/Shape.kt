package com.shoujopomodoro.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════
//  Shoujo Shapes — 少女圆润形状系统
//  Pill, bubble, soft corners
// ═══════════════════════════════════════════════

val ShoujoShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

// Full pill shape for chips, badges
val PillShape = RoundedCornerShape(50)

// Bubble shape — slightly asymmetric for a hand-drawn feel
// (expressed as regular rounded corners in code, designer can tweak)
val BubbleShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 24.dp,
    bottomStart = 8.dp,
    bottomEnd = 24.dp
)

// Legacy alias
val Shapes = ShoujoShapes
