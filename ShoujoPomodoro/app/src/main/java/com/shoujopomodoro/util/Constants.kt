package com.shoujopomodoro.util

object Constants {
    // Notification channels
    const val TIMER_CHANNEL_ID = "timer_channel"
    const val ALERT_CHANNEL_ID = "alert_channel"

    // Notification IDs
    const val TIMER_NOTIFICATION_ID = 1
    const val ALERT_NOTIFICATION_ID = 2

    // Default durations (in minutes)
    const val DEFAULT_FOCUS_MINUTES = 25
    const val DEFAULT_SHORT_BREAK_MINUTES = 5
    const val DEFAULT_LONG_BREAK_MINUTES = 15
    const val DEFAULT_CYCLES = 4

    // Timer service actions
    const val ACTION_START = "com.shoujopomodoro.action.START"
    const val ACTION_PAUSE = "com.shoujopomodoro.action.PAUSE"
    const val ACTION_RESET = "com.shoujopomodoro.action.RESET"
    const val ACTION_SKIP = "com.shoujopomodoro.action.SKIP"
    const val ACTION_STOP_SERVICE = "com.shoujopomodoro.action.STOP_SERVICE"
}
