package com.shoujopomodoro.ui.screen.timer

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shoujopomodoro.ShoujoPomodoroApp
import com.shoujopomodoro.domain.model.CharacterState
import com.shoujopomodoro.domain.model.TimerPhase
import com.shoujopomodoro.domain.model.TimerSession
import com.shoujopomodoro.util.TimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class TimerUiState(
    val timeText: String = "25:00",
    val progress: Float = 0f,
    val phase: TimerPhase = TimerPhase.FOCUS,
    val isRunning: Boolean = false,
    val currentCycle: Int = 1,
    val characterState: CharacterState = CharacterState.IDLE,
    val totalCycles: Int = 4,
    val phaseLabel: String = "Focus",
    val showPermissionRequest: Boolean = false
)

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as ShoujoPomodoroApp).container
    private val stateHolder = container.timerStateHolder
    private val settingsRepo = container.timerSettingsRepository
    private val notificationHelper = container.notificationHelper

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    init {
        // Observe timer session + settings
        viewModelScope.launch {
            combine(
                stateHolder.session,
                settingsRepo.settingsFlow
            ) { session, settings ->
                buildUiState(session, settings.cyclesBeforeLongBreak)
            }.collectLatest { state ->
                _uiState.value = state

                // Update foreground notification when timer is running
                if (state.isRunning) {
                    notificationHelper.showTimerNotification(
                        phase = state.phase,
                        remainingMs = stateHolder.session.value.remainingMs,
                        currentCycle = state.currentCycle
                    )
                } else if (!state.isRunning && state.characterState != CharacterState.ALERTING) {
                    notificationHelper.cancelTimerNotification()
                }
            }
        }

        // Observe settings to update state holder durations
        viewModelScope.launch {
            settingsRepo.settingsFlow.collect { settings ->
                stateHolder.updateSettings(
                    focusMin = settings.focusDurationMinutes,
                    shortMin = settings.shortBreakMinutes,
                    longMin = settings.longBreakMinutes,
                    cycles = settings.cyclesBeforeLongBreak
                )
            }
        }
    }

    fun onStart() {
        stateHolder.start()
    }

    fun onPause() {
        stateHolder.pause()
    }

    fun onReset() {
        stateHolder.reset()
    }

    fun onSkip() {
        stateHolder.skipToNextPhase()
    }

    fun dismissPermissionRequest() {
        _uiState.value = _uiState.value.copy(showPermissionRequest = false)
    }

    private fun buildUiState(session: TimerSession, cycles: Int): TimerUiState {
        val phaseLabel = when (session.phase) {
            TimerPhase.FOCUS -> "Focus"
            TimerPhase.SHORT_BREAK -> "Short Break"
            TimerPhase.LONG_BREAK -> "Long Break"
        }

        return TimerUiState(
            timeText = TimeFormatter.formatMs(session.remainingMs),
            progress = TimeFormatter.getProgress(session.remainingMs, session.totalDurationMs),
            phase = session.phase,
            isRunning = session.isRunning,
            currentCycle = session.currentCycle,
            characterState = session.characterState,
            totalCycles = cycles,
            phaseLabel = phaseLabel
        )
    }
}
