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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoujopomodoro.R
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
            stringResource(R.string.phase_focus_format, currentCycle, totalCycles),
            MaterialTheme.colorScheme.primary
        )
        TimerPhase.SHORT_BREAK -> Pair(
            stringResource(R.string.phase_short_break),
            Color(0xFF66BB6A)
        )
        TimerPhase.LONG_BREAK -> Pair(
            stringResource(R.string.phase_long_break),
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
