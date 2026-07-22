package com.shoujopomodoro.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoujopomodoro.R
import com.shoujopomodoro.domain.model.TimerPhase
import com.shoujopomodoro.ui.theme.*

@Composable
fun PremiumControlButtons(
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) DarkCard.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.5f)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(40.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reset
        NeumorphicIconButton(
            icon = Icons.Default.Refresh,
            contentDescription = stringResource(R.string.reset),
            onClick = onReset,
            containerColor = if (isDark) DarkSurface else Color(0xFFF0F0F5),
            size = 52.dp,
            iconSize = 24.dp
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Play/Pause — larger, gradient
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(SakuraDeep, CoralRose)
                    )
                )
                .shadow(8.dp, CircleShape, ambientColor = SakuraDeep.copy(alpha = 0.3f), spotColor = SakuraDeep.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { if (isRunning) onPause() else onStart() },
                modifier = Modifier.size(68.dp)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) stringResource(R.string.pause) else stringResource(R.string.start),
                    modifier = Modifier.size(34.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Skip
        NeumorphicIconButton(
            icon = Icons.Default.SkipNext,
            contentDescription = stringResource(R.string.skip),
            onClick = onSkip,
            containerColor = if (isDark) DarkSurface else Color(0xFFF0F0F5),
            size = 52.dp,
            iconSize = 24.dp
        )
    }
}

@Composable
private fun NeumorphicIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    containerColor: Color,
    size: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp
) {
    // Soft neumorphic press animation
    val isDark = isSystemInDarkTheme()
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .shadow(4.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.06f), spotColor = Color.Black.copy(alpha = 0.06f))
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(size)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
                tint = if (isDark) Color(0xFFE0E0E0) else Color(0xFF555555)
            )
        }
    }
}

// ── Enhanced Phase Label ──
@Composable
fun EnhancedPhaseLabel(
    phase: com.shoujopomodoro.domain.model.TimerPhase,
    currentCycle: Int,
    totalCycles: Int,
    modifier: Modifier = Modifier
) {
    val (label, bgColor, textColor) = when (phase) {
        com.shoujopomodoro.domain.model.TimerPhase.FOCUS -> Triple(
            "🌸 Focus · $currentCycle/$totalCycles",
            SakuraDeep.copy(alpha = 0.12f),
            SakuraDeep
        )
        com.shoujopomodoro.domain.model.TimerPhase.SHORT_BREAK -> Triple(
            "🍵 Short Break",
            MatchaMint.copy(alpha = 0.15f),
            Color(0xFF4CAF50)
        )
        com.shoujopomodoro.domain.model.TimerPhase.LONG_BREAK -> Triple(
            "🌙 Long Break",
            SkyBlue.copy(alpha = 0.15f),
            Color(0xFF1976D2)
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        androidx.compose.material3.Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            color = textColor
        )
    }
}

// ── Premium Music Player Bar ──
@Composable
fun PremiumMusicPlayerBar(
    musicPaths: List<String>,
    modifier: Modifier = Modifier
) {
    if (musicPaths.isEmpty()) return

    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) DarkCard.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.4f)

    // Reuse original EnhancedMusicPlayerBar but wrap in glass container
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(bgColor)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        EnhancedMusicPlayerBar(
            musicPaths = musicPaths,
            modifier = Modifier.padding(0.dp)
        )
    }
}
