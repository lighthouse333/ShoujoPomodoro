package com.shoujopomodoro.ui.screen.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shoujopomodoro.ShoujoPomodoroApp
import com.shoujopomodoro.data.preferences.TimerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class SettingsUiState(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val cyclesBeforeLongBreak: Int = 4,
    val language: String = "en",
    val clockPosition: String = "top_bar",
    val musicPaths: List<String> = emptyList()
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
                    language = settings.language,
                    clockPosition = settings.clockPosition,
                    musicPaths = settings.musicPaths
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

    fun updateClockPosition(position: String) {
        viewModelScope.launch {
            settingsRepo.updateClockPosition(position)
        }
    }

    fun importMusic(uris: List<Uri>) {
        viewModelScope.launch {
            val context = getApplication<ShoujoPomodoroApp>()
            val musicDir = File(context.filesDir, "music")
            if (!musicDir.exists()) musicDir.mkdirs()

            val newPaths = mutableListOf<String>()
            for (uri in uris) {
                try {
                    val fileName = uri.lastPathSegment ?: "track_${System.currentTimeMillis()}.mp3"
                    val destFile = File(musicDir, fileName)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(destFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    newPaths.add(destFile.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val currentPaths = _uiState.value.musicPaths.toMutableList()
            currentPaths.addAll(newPaths)
            settingsRepo.updateMusicPaths(currentPaths)
        }
    }

    fun removeMusic(path: String) {
        viewModelScope.launch {
            val currentPaths = _uiState.value.musicPaths.toMutableList()
            currentPaths.remove(path)
            settingsRepo.updateMusicPaths(currentPaths)
            // Also delete the file
            try {
                File(path).delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
