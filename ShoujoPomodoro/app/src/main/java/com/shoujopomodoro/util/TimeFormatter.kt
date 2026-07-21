package com.shoujopomodoro.util

object TimeFormatter {

    /**
     * Format milliseconds to "MM:SS" string.
     */
    fun formatMs(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    /**
     * Format milliseconds to a human-readable short label.
     * e.g., 25 min, 5 min
     */
    fun formatMinutes(ms: Long): String {
        val minutes = ms / 60_000
        return "$minutes min"
    }

    /**
     * Get progress as a float from 0.0 to 1.0.
     */
    fun getProgress(remainingMs: Long, totalMs: Long): Float {
        if (totalMs == 0L) return 0f
        return ((totalMs - remainingMs).toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
    }
}
