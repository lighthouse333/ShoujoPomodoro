package com.shoujopomodoro.ui.screen.timer

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shoujopomodoro.ShoujoPomodoroApp
import com.shoujopomodoro.data.local.entity.FocusSessionEntity
import com.shoujopomodoro.data.preferences.TimerSettings
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TimerUiState(
    val timeText: String = "25:00",
    val progress: Float = 0f,
    val phase: TimerPhase = TimerPhase.FOCUS,
    val isRunning: Boolean = false,
    val currentCycle: Int = 1,
    val characterState: CharacterState = CharacterState.IDLE,
    val totalCycles: Int = 4,
    val phaseLabel: String = "Focus",
    val showPermissionRequest: Boolean = false,
    val clockPosition: String = "top_bar",
    val musicPaths: List<String> = emptyList()
)

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as ShoujoPomodoroApp).container
    private val stateHolder = container.timerStateHolder
    private val settingsRepo = container.timerSettingsRepository
    private val notificationHelper = container.notificationHelper
    private val focusSessionRepo = container.focusSessionRepository

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    // Track previous character state to detect session completions
    private var prevCharacterState: CharacterState = CharacterState.IDLE

    init {
        // Observe timer session + settings
        viewModelScope.launch {
            combine(
                stateHolder.session,
                settingsRepo.settingsFlow
            ) { session, settings ->
                buildUiState(session, settings)
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

        // Track focus session completions
        viewModelScope.launch {
            stateHolder.session.collect { session ->
                // Detect transition to ALERTING state
                if (prevCharacterState != CharacterState.ALERTING
                    && session.characterState == CharacterState.ALERTING
                    && session.phase == TimerPhase.FOCUS
                ) {
                    // A focus session just completed — persist it
                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    focusSessionRepo.recordSession(
                        date = dateStr,
                        durationMs = session.totalDurationMs
                    )
                }
                prevCharacterState = session.characterState
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

    private fun buildUiState(session: TimerSession, settings: TimerSettings): TimerUiState {
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
            totalCycles = settings.cyclesBeforeLongBreak,
            phaseLabel = phaseLabel,
            clockPosition = settings.clockPosition,
            musicPaths = settings.musicPaths
        )
    }
}
