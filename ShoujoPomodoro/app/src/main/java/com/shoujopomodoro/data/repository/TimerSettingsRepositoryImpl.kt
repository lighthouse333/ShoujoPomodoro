package com.shoujopomodoro.data.repository

import com.shoujopomodoro.data.preferences.TimerPreferences
import com.shoujopomodoro.data.preferences.TimerSettings
import kotlinx.coroutines.flow.Flow

class TimerSettingsRepositoryImpl(
    private val preferences: TimerPreferences
) : TimerSettingsRepository {

    override val settingsFlow: Flow<TimerSettings> = preferences.settingsFlow

    override suspend fun updateFocusDuration(minutes: Int) {
        preferences.updateFocusDuration(minutes)
    }

    override suspend fun updateShortBreak(minutes: Int) {
        preferences.updateShortBreak(minutes)
    }

    override suspend fun updateLongBreak(minutes: Int) {
        preferences.updateLongBreak(minutes)
    }

    override suspend fun updateCycles(cycles: Int) {
        preferences.updateCycles(cycles)
    }

    override suspend fun updateLanguage(lang: String) {
        preferences.updateLanguage(lang)
    }

    override suspend fun updateClockPosition(position: String) {
        preferences.updateClockPosition(position)
    }

    override suspend fun updateMusicPaths(paths: List<String>) {
        preferences.updateMusicPaths(paths)
    }

    override suspend fun updateCurrentMusicIndex(index: Int) {
        preferences.updateCurrentMusicIndex(index)
    }
}
