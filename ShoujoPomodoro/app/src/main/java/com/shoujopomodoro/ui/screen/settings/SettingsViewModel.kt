package com.shoujopomodoro.ui.screen.settings

import android.app.Application
import android.content.Context
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
    val cyclesBeforeLongBreak: Int = 4,
    val language: String = "en"
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as ShoujoPomodoroApp).container
    private val settingsRepo = container.timerSettingsRepository

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // Callback that the Activity sets to trigger a recreate for hot locale switching
    var onLanguageChanged: (() -> Unit)? = null

    init {
        viewModelScope.launch {
            settingsRepo.settingsFlow.collect { settings ->
                _uiState.value = SettingsUiState(
                    focusMinutes = settings.focusDurationMinutes,
                    shortBreakMinutes = settings.shortBreakMinutes,
                    longBreakMinutes = settings.longBreakMinutes,
                    cyclesBeforeLongBreak = settings.cyclesBeforeLongBreak,
                    language = settings.language
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

    fun updateLanguage(lang: String) {
        viewModelScope.launch {
            settingsRepo.updateLanguage(lang)
            // Save to SharedPreferences for synchronous read in MainActivity.attachBaseContext
            val sp = getApplication<ShoujoPomodoroApp>()
                .getSharedPreferences("app_locale_prefs", Context.MODE_PRIVATE)
            sp.edit().putString("current_locale", lang).commit()
            // Update UI state immediately
            _uiState.value = _uiState.value.copy(language = lang)
            // Trigger Activity recreate for hot locale switching
            onLanguageChanged?.invoke()
        }
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
