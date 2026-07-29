package com.shoujopomodoro.ui.screen.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shoujopomodoro.ShoujoPomodoroApp
import com.shoujopomodoro.data.preferences.TimerSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class SettingsUiState(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val cyclesBeforeLongBreak: Int = 4,
    val language: String = "en",
    val clockPosition: String = "top_bar",
    val musicPaths: List<String> = emptyList(),
    val builtInMusicPaths: List<String> = emptyList()
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as ShoujoPomodoroApp).container
    private val settingsRepo = container.timerSettingsRepository

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // Callback that the Activity sets to trigger a recreate for hot locale switching
    var onLanguageChanged: (() -> Unit)? = null

    private var migrationDone = false

    init {
        viewModelScope.launch {
            settingsRepo.settingsFlow.collect { settings ->
                // One-time migration: mark all existing music as built-in (non-deletable)
                if (!migrationDone && settings.builtInMusicPaths.isEmpty() && settings.musicPaths.isNotEmpty()) {
                    settingsRepo.updateBuiltInMusicPaths(settings.musicPaths)
                    migrationDone = true
                }

                _uiState.value = SettingsUiState(
                    focusMinutes = settings.focusDurationMinutes,
                    shortBreakMinutes = settings.shortBreakMinutes,
                    longBreakMinutes = settings.longBreakMinutes,
                    cyclesBeforeLongBreak = settings.cyclesBeforeLongBreak,
                    language = settings.language,
                    clockPosition = settings.clockPosition,
                    musicPaths = settings.musicPaths,
                    builtInMusicPaths = if (!migrationDone && settings.builtInMusicPaths.isEmpty() && settings.musicPaths.isNotEmpty()) {
                        settings.musicPaths
                    } else {
                        settings.builtInMusicPaths
                    }
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
                    // Query the real display name from the content URI
                    var fileName = "track_${System.currentTimeMillis()}.mp3"
                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (nameIndex >= 0) {
                                val displayName = cursor.getString(nameIndex)
                                if (displayName.isNotBlank()) {
                                    fileName = displayName
                                }
                            }
                        }
                    }

                    // Ensure the fileName has a usable extension
                    if (!fileName.contains(".")) {
                        fileName += ".mp3"
                    }

                    val destFile = File(musicDir, fileName)
                    // Avoid overwriting: append number if file exists
                    var finalDestFile = destFile
                    var counter = 1
                    while (finalDestFile.exists()) {
                        val dotIndex = fileName.lastIndexOf('.')
                        val baseName = if (dotIndex >= 0) fileName.substring(0, dotIndex) else fileName
                        val ext = if (dotIndex >= 0) fileName.substring(dotIndex) else ".mp3"
                        finalDestFile = File(musicDir, "${baseName}_($counter)$ext")
                        counter++
                    }

                    // Copy file content on IO dispatcher
                    val copied = withContext(Dispatchers.IO) {
                        try {
                            context.contentResolver.openInputStream(uri)?.use { input ->
                                FileOutputStream(finalDestFile).use { output ->
                                    input.copyTo(output)
                                }
                            }
                            true // file was actually copied
                        } catch (e: Exception) {
                            e.printStackTrace()
                            false
                        }
                    }

                    // Only add the path if the file was actually copied (exists and non-empty)
                    if (copied && finalDestFile.exists() && finalDestFile.length() > 0) {
                        newPaths.add(finalDestFile.absolutePath)
                    } else if (finalDestFile.exists()) {
                        // Clean up empty file
                        finalDestFile.delete()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (newPaths.isNotEmpty()) {
                val currentPaths = _uiState.value.musicPaths.toMutableList()
                currentPaths.addAll(newPaths)
                settingsRepo.updateMusicPaths(currentPaths)
            }
        }
    }

    fun removeMusic(path: String) {
        // Guard: built-in music files cannot be deleted
        if (_uiState.value.builtInMusicPaths.contains(path)) return
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
