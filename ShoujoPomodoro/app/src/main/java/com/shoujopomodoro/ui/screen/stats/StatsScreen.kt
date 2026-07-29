package com.shoujopomodoro.ui.screen.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoujopomodoro.R
import com.shoujopomodoro.ui.component.SakuraParticleBackground
import com.shoujopomodoro.ui.component.SoftGradientBackground
import com.shoujopomodoro.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    val monthResIds = listOf(
        R.string.month_0, R.string.month_1, R.string.month_2, R.string.month_3,
        R.string.month_4, R.string.month_5, R.string.month_6, R.string.month_7,
        R.string.month_8, R.string.month_9, R.string.month_10, R.string.month_11
    )
    val dayResIds = listOf(
        R.string.day_0, R.string.day_1, R.string.day_2, R.string.day_3,
        R.string.day_4, R.string.day_5, R.string.day_6
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.focus_stats), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkCard.copy(alpha = 0.85f)
                    else SakuraLight.copy(alpha = 0.85f),
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            SoftGradientBackground(isDark = isDark)
            SakuraParticleBackground(intensity = 0.2f)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Stat Cards ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.stats_today),
                        value = "${uiState.todayMinutes}${stringResource(R.string.stats_min_unit)}",
                        accentColor = SakuraDeep,
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.stats_this_week),
                        value = "${uiState.weekMinutes}${stringResource(R.string.stats_min_unit)}",
                        accentColor = WisteriaDeep,
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Calendar Section ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDark) DarkCard.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.4f))
                        .padding(16.dp)
                ) {
                    // Month header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.navigateMonth(-1) }) {
                            Icon(
                                Icons.Default.ChevronLeft,
                                contentDescription = stringResource(R.string.stats_prev_month),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "${stringResource(monthResIds[uiState.selectedMonth])} ${uiState.selectedYear}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        IconButton(onClick = { viewModel.navigateMonth(1) }) {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = stringResource(R.string.stats_next_month),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Day-of-week headers
                    Row(modifier = Modifier.fillMaxWidth()) {
                        dayResIds.forEach { resId ->
                            Text(
                                text = stringResource(resId),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Calendar grid
                    val calendarDays = buildCalendarDays(uiState.selectedYear, uiState.selectedMonth)
                    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    val rows = calendarDays.chunked(7)
                    rows.forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            week.forEach { dayInfo ->
                                val dateKey = dayInfo?.let {
                                    String.format("%04d-%02d-%02d", it.year, it.month + 1, it.day)
                                }
                                val focusMs = dateKey?.let { uiState.dailyStats[it] } ?: 0L
                                val isToday = dateKey == todayStr

                                CalendarDayCell(
                                    dayInfo = dayInfo,
                                    focusMinutes = focusMs / 60_000,
                                    isToday = isToday,
                                    isDark = isDark,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

data class DayInfo(
    val year: Int,
    val month: Int,
    val day: Int
)

private fun buildCalendarDays(year: Int, month: Int): List<DayInfo?> {
    val cal = Calendar.getInstance()
    cal.set(year, month, 1)

    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sunday
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val days = mutableListOf<DayInfo?>()

    // Leading nulls for offset
    repeat(firstDayOfWeek - 1) {
        days.add(null)
    }

    // Actual days
    for (day in 1..daysInMonth) {
        days.add(DayInfo(year, month, day))
    }

    // Pad to complete the last week
    while (days.size % 7 != 0) {
        days.add(null)
    }

    return days
}

@Composable
private fun CalendarDayCell(
    dayInfo: DayInfo?,
    focusMinutes: Long,
    isToday: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    isToday -> SakuraGlow.copy(alpha = 0.6f)
                    focusMinutes > 0 -> if (isDark) SakuraDeep.copy(alpha = 0.15f) else SakuraPink.copy(alpha = 0.25f)
                    else -> Color.Transparent
                }
            )
            .then(
                if (isToday) Modifier.border(1.5.dp, SakuraDeep, RoundedCornerShape(8.dp))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (dayInfo != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${dayInfo.day}",
                    fontSize = 14.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isToday) SakuraDeep
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                if (focusMinutes > 0) {
                    Text(
                        text = "${focusMinutes}${stringResource(R.string.stats_min_short)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = SakuraDeep.copy(alpha = 0.8f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    accentColor: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDark) DarkCard.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.4f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
