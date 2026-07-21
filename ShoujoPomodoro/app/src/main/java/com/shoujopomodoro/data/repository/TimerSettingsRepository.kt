package com.shoujopomodoro.data.repository

import com.shoujopomodoro.data.preferences.TimerSettings
import kotlinx.coroutines.flow.Flow

interface TimerSettingsRepository {
    val settingsFlow: Flow<TimerSettings>
    suspend fun updateFocusDuration(minutes: Int)
    suspend fun updateShortBreak(minutes: Int)
    suspend fun updateLongBreak(minutes: Int)
    suspend fun updateCycles(cycles: Int)
    suspend fun updateLanguage(lang: String)
}
