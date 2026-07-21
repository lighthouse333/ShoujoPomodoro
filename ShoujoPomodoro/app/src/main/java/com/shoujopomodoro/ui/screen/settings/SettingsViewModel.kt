package com.shoujopomodoro.ui.screen.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shoujopomodoro.ShoujoPomodoroApp
import com.shoujopomodoro.data.preferences.TimerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val cyclesBeforeLongBreak: Int = 4
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as ShoujoPomodoroApp).container
    private val settingsRepo = container.timerSettingsRepository

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepo.settingsFlow.collect { settings ->
                _uiState.value = SettingsUiState(
                    focusMinutes = settings.focusDurationMinutes,
                    shortBreakMinutes = settings.shortBreakMinutes,
                    longBreakMinutes = settings.longBreakMinutes,
                    cyclesBeforeLongBreak = settings.cyclesBeforeLongBreak
                )
            }
        }
    }

    fun updateFocus(minutes: Int) {
        viewModelScope.launch { settingsRepo.updateFocusDuration(minutes) }
    }

    fun updateShortBreak(minutes: Int) {
        viewModelScope.launch { settingsRepo.updateShortBreak(minutes) }
    }

    fun updateLongBreak(minutes: Int) {
        viewModelScope.launch { settingsRepo.updateLongBreak(minutes) }
    }

    fun updateCycles(cycles: Int) {
        viewModelScope.launch { settingsRepo.updateCycles(cycles) }
    }

    fun resetDefaults() {
        viewModelScope.launch {
            settingsRepo.updateFocusDuration(25)
            settingsRepo.updateShortBreak(5)
            settingsRepo.updateLongBreak(15)
            settingsRepo.updateCycles(4)
        }
    }
}
