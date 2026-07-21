package com.shoujopomodoro.domain.model

data class TimerSession(
    val phase: TimerPhase = TimerPhase.FOCUS,
    val isRunning: Boolean = false,
    val totalDurationMs: Long = 25 * 60 * 1000L,
    val remainingMs: Long = 25 * 60 * 1000L,
    val elapsedMs: Long = 0L,
    val currentCycle: Int = 1,
    val characterState: CharacterState = CharacterState.IDLE
)
