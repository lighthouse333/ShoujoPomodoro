package com.shoujopomodoro.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timer_settings")

class TimerPreferences(private val context: Context) {

    companion object {
        const val SHARED_PREFS_NAME = "app_locale_prefs"
        const val SP_KEY_LANGUAGE = "current_locale"

        val FOCUS_DURATION = intPreferencesKey("focus_duration")
        val SHORT_BREAK_DURATION = intPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION = intPreferencesKey("long_break_duration")
        val CYCLES_BEFORE_LONG_BREAK = intPreferencesKey("cycles_before_long_break")
        val LANGUAGE = stringPreferencesKey("app_language")
        val CLOCK_POSITION = stringPreferencesKey("clock_position")
        val MUSIC_PATHS = stringPreferencesKey("music_paths")
        val BUILT_IN_MUSIC_PATHS = stringPreferencesKey("built_in_music_paths")
        val CURRENT_MUSIC_INDEX = intPreferencesKey("current_music_index")

        const val DEFAULT_FOCUS = 25
        const val DEFAULT_SHORT_BREAK = 5
        const val DEFAULT_LONG_BREAK = 15
        const val DEFAULT_CYCLES = 4
        const val DEFAULT_LANGUAGE = "en"
        const val DEFAULT_CLOCK_POSITION = "top_bar"
    }

    val settingsFlow: Flow<TimerSettings> = context.dataStore.data.map { prefs ->
        TimerSettings(
            focusDurationMinutes = prefs[FOCUS_DURATION] ?: DEFAULT_FOCUS,
            shortBreakMinutes = prefs[SHORT_BREAK_DURATION] ?: DEFAULT_SHORT_BREAK,
            longBreakMinutes = prefs[LONG_BREAK_DURATION] ?: DEFAULT_LONG_BREAK,
            cyclesBeforeLongBreak = prefs[CYCLES_BEFORE_LONG_BREAK] ?: DEFAULT_CYCLES,
            language = prefs[LANGUAGE] ?: DEFAULT_LANGUAGE,
            clockPosition = prefs[CLOCK_POSITION] ?: DEFAULT_CLOCK_POSITION,
            musicPaths = (prefs[MUSIC_PATHS] ?: "").let { serialized ->
                // New format uses \n delimiter; fall back to legacy comma-delimited
                if (serialized.contains("\n")) {
                    serialized.split("\n").filter { it.isNotBlank() }
                } else {
                    serialized.split(",").filter { it.isNotBlank() }
                }
            },
            builtInMusicPaths = (prefs[BUILT_IN_MUSIC_PATHS] ?: "").let { serialized ->
                if (serialized.contains("\n")) {
                    serialized.split("\n").filter { it.isNotBlank() }
                } else {
                    serialized.split(",").filter { it.isNotBlank() }
                }
            },
            currentMusicIndex = prefs[CURRENT_MUSIC_INDEX] ?: 0
        )
    }

    suspend fun updateFocusDuration(minutes: Int) {
        context.dataStore.edit { it[FOCUS_DURATION] = minutes }
    }

    suspend fun updateShortBreak(minutes: Int) {
        context.dataStore.edit { it[SHORT_BREAK_DURATION] = minutes }
    }

    suspend fun updateLongBreak(minutes: Int) {
        context.dataStore.edit { it[LONG_BREAK_DURATION] = minutes }
    }

    suspend fun updateCycles(cycles: Int) {
        context.dataStore.edit { it[CYCLES_BEFORE_LONG_BREAK] = cycles }
    }

    suspend fun updateLanguage(lang: String) {
        // Write to DataStore
        context.dataStore.edit { it[LANGUAGE] = lang }
        // Also write to SharedPreferences for synchronous read in MainActivity.attachBaseContext
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(SP_KEY_LANGUAGE, lang)
            .apply()
    }

    suspend fun updateClockPosition(position: String) {
        context.dataStore.edit { it[CLOCK_POSITION] = position }
    }

    suspend fun updateMusicPaths(paths: List<String>) {
        context.dataStore.edit { it[MUSIC_PATHS] = paths.joinToString("\n") }
    }

    suspend fun updateBuiltInMusicPaths(paths: List<String>) {
        context.dataStore.edit { it[BUILT_IN_MUSIC_PATHS] = paths.joinToString("\n") }
    }

    suspend fun updateCurrentMusicIndex(index: Int) {
        context.dataStore.edit { it[CURRENT_MUSIC_INDEX] = index }
    }
}

data class TimerSettings(
    val focusDurationMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val cyclesBeforeLongBreak: Int = 4,
    val language: String = "en",
    val clockPosition: String = "top_bar",
    val musicPaths: List<String> = emptyList(),
    val builtInMusicPaths: List<String> = emptyList(),
    val currentMusicIndex: Int = 0
)
