package com.shoujopomodoro.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoujopomodoro.domain.model.TimerPhase

@Composable
fun PhaseLabel(
    phase: TimerPhase,
    currentCycle: Int,
    totalCycles: Int,
    modifier: Modifier = Modifier
) {
    val (label, bgColor) = when (phase) {
        TimerPhase.FOCUS -> Pair(
            "Focus ${currentCycle}/${totalCycles}",
            MaterialTheme.colorScheme.primary
        )
        TimerPhase.SHORT_BREAK -> Pair(
            "Short Break",
            Color(0xFF66BB6A)
        )
        TimerPhase.LONG_BREAK -> Pair(
            "Long Break",
            Color(0xFF42A5F5)
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor.copy(alpha = 0.15f))
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = bgColor
        )
    }
}
