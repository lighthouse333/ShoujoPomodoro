package com.shoujopomodoro.di

import com.shoujopomodoro.domain.model.CharacterState
import com.shoujopomodoro.domain.model.TimerPhase
import com.shoujopomodoro.domain.model.TimerSession
import com.shoujopomodoro.domain.usecase.PomodoroCycleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerStateHolder {

    private val _session = MutableStateFlow(TimerSession())
    val session: StateFlow<TimerSession> = _session.asStateFlow()

    private var tickerJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val cycleUseCase = PomodoroCycleUseCase()

    // Settings that can be updated externally
    var focusMinutes: Int = 25
    var shortBreakMinutes: Int = 5
    var longBreakMinutes: Int = 15
    var cyclesBeforeLongBreak: Int = 4

    // Callbacks for notifications
    var onTimerComplete: ((TimerPhase) -> Unit)? = null
    var onPhaseChange: ((TimerPhase, Int) -> Unit)? = null

    fun start() {
        val current = _session.value
        if (current.isRunning) return

        _session.update {
            it.copy(
                isRunning = true,
                characterState = if (it.phase == TimerPhase.FOCUS)
                    CharacterState.FOCUSING else CharacterState.RESTING
            )
        }
        startTicking()
    }

    fun pause() {
        tickerJob?.cancel()
        tickerJob = null
        _session.update { it.copy(isRunning = false, characterState = CharacterState.IDLE) }
    }

    fun reset() {
        tickerJob?.cancel()
        tickerJob = null
        val current = _session.value
        val duration = getDurationForPhase(current.phase)
        _session.update {
            it.copy(
                isRunning = false,
                totalDurationMs = duration,
                remainingMs = duration,
                elapsedMs = 0L,
                characterState = CharacterState.IDLE
            )
        }
    }

    fun skipToNextPhase() {
        tickerJob?.cancel()
        tickerJob = null
        advancePhase()
    }

    fun updateSettings(focusMin: Int, shortMin: Int, longMin: Int, cycles: Int) {
        focusMinutes = focusMin
        shortBreakMinutes = shortMin
        longBreakMinutes = longMin
        cyclesBeforeLongBreak = cycles
    }

    private fun startTicking() {
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (true) {
                delay(1000L)
                val current = _session.value
                if (!current.isRunning) break

                val newRemaining = current.remainingMs - 1000L
                val newElapsed = current.elapsedMs + 1000L

                if (newRemaining <= 0) {
                    // Timer complete!
                    _session.update {
                        it.copy(
                            remainingMs = 0L,
                            elapsedMs = it.totalDurationMs,
                            isRunning = false,
                            characterState = CharacterState.ALERTING
                        )
                    }
                    onTimerComplete?.invoke(current.phase)
                    // Auto-advance after 10 seconds
                    delay(10_000L)
                    advancePhase()
                    break
                } else {
                    _session.update {
                        it.copy(remainingMs = newRemaining, elapsedMs = newElapsed)
                    }
                }
            }
        }
    }

    private fun advancePhase() {
        val current = _session.value
        val nextPhase = cycleUseCase.getNextPhase(
            current.phase, current.currentCycle, cyclesBeforeLongBreak
        )
        val nextCycle = cycleUseCase.getNextCycle(
            current.phase, current.currentCycle, cyclesBeforeLongBreak
        )
        val duration = getDurationForPhase(nextPhase)

        _session.update {
            TimerSession(
                phase = nextPhase,
                isRunning = false,
                totalDurationMs = duration,
                remainingMs = duration,
                elapsedMs = 0L,
                currentCycle = nextCycle,
                characterState = CharacterState.IDLE
            )
        }
        onPhaseChange?.invoke(nextPhase, nextCycle)
    }

    private fun getDurationForPhase(phase: TimerPhase): Long {
        return when (phase) {
            TimerPhase.FOCUS -> focusMinutes * 60 * 1000L
            TimerPhase.SHORT_BREAK -> shortBreakMinutes * 60 * 1000L
            TimerPhase.LONG_BREAK -> longBreakMinutes * 60 * 1000L
        }
    }
}
