package com.shoujopomodoro.ui.screen.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shoujopomodoro.ShoujoPomodoroApp
import com.shoujopomodoro.data.local.dao.DailyStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class StatsUiState(
    val todayMinutes: Long = 0,
    val weekMinutes: Long = 0,
    val selectedYear: Int = 2026,
    val selectedMonth: Int = 1,   // 0-based (0=January)
    val dailyStats: Map<String, Long> = emptyMap()  // "YYYY-MM-DD" -> totalMs
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as ShoujoPomodoroApp).container
    private val focusRepo = container.focusSessionRepository

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        val now = Calendar.getInstance()
        _uiState.value = _uiState.value.copy(
            selectedYear = now.get(Calendar.YEAR),
            selectedMonth = now.get(Calendar.MONTH)
        )
        loadTodayAndWeek()
        loadMonthStats()
    }

    fun navigateMonth(delta: Int) {
        val current = _uiState.value
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, current.selectedYear)
            set(Calendar.MONTH, current.selectedMonth)
            add(Calendar.MONTH, delta)
        }
        _uiState.value = _uiState.value.copy(
            selectedYear = cal.get(Calendar.YEAR),
            selectedMonth = cal.get(Calendar.MONTH)
        )
        loadMonthStats()
    }

    private fun loadTodayAndWeek() {
        val today = dateFormat.format(Date())
        val weekDates = getWeekDates()

        viewModelScope.launch {
            focusRepo.getTodayTotalMs(today).collect { ms ->
                _uiState.value = _uiState.value.copy(todayMinutes = ms / 60_000)
            }
        }
        viewModelScope.launch {
            focusRepo.getWeekTotalMs(weekDates).collect { ms ->
                _uiState.value = _uiState.value.copy(weekMinutes = ms / 60_000)
            }
        }
    }

    private fun loadMonthStats() {
        val current = _uiState.value
        val yearMonth = String.format("%04d-%02d", current.selectedYear, current.selectedMonth + 1)

        viewModelScope.launch {
            focusRepo.getMonthlyStats(yearMonth).collect { stats ->
                val map = stats.associate { it.date to it.totalMs }
                _uiState.value = _uiState.value.copy(dailyStats = map)
            }
        }
    }

    private fun getWeekDates(): List<String> {
        val cal = Calendar.getInstance()
        // Set to Monday of current week
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val diff = if (dayOfWeek == Calendar.SUNDAY) -6 else Calendar.MONDAY - dayOfWeek
        cal.add(Calendar.DAY_OF_MONTH, diff)

        val dates = mutableListOf<String>()
        for (i in 0..6) {
            dates.add(dateFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dates
    }
}
