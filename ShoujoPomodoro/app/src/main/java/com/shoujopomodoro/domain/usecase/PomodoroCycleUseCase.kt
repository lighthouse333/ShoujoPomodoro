package com.shoujopomodoro.domain.usecase

import com.shoujopomodoro.domain.model.CharacterState
import com.shoujopomodoro.domain.model.TimerPhase

class PomodoroCycleUseCase {

    /**
     * Determine the next phase based on current phase and cycle count.
     */
    fun getNextPhase(
        currentPhase: TimerPhase,
        currentCycle: Int,
        cyclesBeforeLongBreak: Int
    ): TimerPhase {
        return when (currentPhase) {
            TimerPhase.FOCUS -> {
                if (currentCycle >= cyclesBeforeLongBreak) {
                    TimerPhase.LONG_BREAK
                } else {
                    TimerPhase.SHORT_BREAK
                }
            }
            TimerPhase.SHORT_BREAK, TimerPhase.LONG_BREAK -> {
                TimerPhase.FOCUS
            }
        }
    }

    /**
     * Get the next cycle number after phase change.
     */
    fun getNextCycle(
        currentPhase: TimerPhase,
        currentCycle: Int,
        cyclesBeforeLongBreak: Int
    ): Int {
        return when (currentPhase) {
            TimerPhase.FOCUS -> currentCycle // cycle increments only after completing a break
            TimerPhase.SHORT_BREAK -> currentCycle + 1
            TimerPhase.LONG_BREAK -> 1 // reset cycle after long break
        }
    }

    /**
     * Determine character state from timer phase and running state.
     */
    fun getCharacterState(phase: TimerPhase, isRunning: Boolean, isComplete: Boolean): CharacterState {
        if (isComplete) return CharacterState.ALERTING
        return when {
            !isRunning -> CharacterState.IDLE
            phase == TimerPhase.FOCUS -> CharacterState.FOCUSING
            else -> CharacterState.RESTING
        }
    }

    /**
     * Get duration in milliseconds for a given phase.
     */
    fun getPhaseDurationMs(
        phase: TimerPhase,
        focusMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int
    ): Long {
        return when (phase) {
            TimerPhase.FOCUS -> focusMinutes * 60 * 1000L
            TimerPhase.SHORT_BREAK -> shortBreakMinutes * 60 * 1000L
            TimerPhase.LONG_BREAK -> longBreakMinutes * 60 * 1000L
        }
    }
}
