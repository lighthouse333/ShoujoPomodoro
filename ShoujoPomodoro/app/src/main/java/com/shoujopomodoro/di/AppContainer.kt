package com.shoujopomodoro.di

import android.content.Context
import com.shoujopomodoro.data.local.AppDatabase
import com.shoujopomodoro.data.preferences.TimerPreferences
import com.shoujopomodoro.data.repository.TaskRepositoryImpl
import com.shoujopomodoro.data.repository.TimerSettingsRepositoryImpl
import com.shoujopomodoro.domain.usecase.PomodoroCycleUseCase
import com.shoujopomodoro.domain.usecase.TaskListUseCase
import com.shoujopomodoro.notification.NotificationHelper

class AppContainer(context: Context) {

    // Data sources
    val database: AppDatabase = AppDatabase.getInstance(context)
    val timerPreferences: TimerPreferences = TimerPreferences(context)

    // Repositories
    val taskRepository = TaskRepositoryImpl(database.taskDao())
    val timerSettingsRepository = TimerSettingsRepositoryImpl(timerPreferences)

    // Use cases
    val pomodoroCycleUseCase = PomodoroCycleUseCase()
    val taskListUseCase = TaskListUseCase(taskRepository)

    // Notification helper
    val notificationHelper = NotificationHelper(context)

    // Timer state holder (shared between ViewModel and ForegroundService)
    val timerStateHolder = TimerStateHolder()
}
