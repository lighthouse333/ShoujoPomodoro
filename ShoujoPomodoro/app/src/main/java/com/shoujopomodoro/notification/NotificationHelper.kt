package com.shoujopomodoro.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.shoujopomodoro.MainActivity
import com.shoujopomodoro.R
import com.shoujopomodoro.domain.model.TimerPhase
import com.shoujopomodoro.util.Constants
import com.shoujopomodoro.util.TimeFormatter

class NotificationHelper(private val context: Context) {

    init {
        createChannels()
    }

    private fun createChannels() {
        val timerChannel = NotificationChannel(
            Constants.TIMER_CHANNEL_ID,
            context.getString(R.string.timer_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.timer_channel_desc)
            setShowBadge(false)
        }

        val alertChannel = NotificationChannel(
            Constants.ALERT_CHANNEL_ID,
            context.getString(R.string.alert_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.alert_channel_desc)
            enableVibration(true)
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(timerChannel)
        manager.createNotificationChannel(alertChannel)
    }

    fun buildTimerNotification(
        phase: TimerPhase,
        remainingMs: Long,
        currentCycle: Int
    ): android.app.Notification {
        val phaseText = when (phase) {
            TimerPhase.FOCUS -> context.getString(R.string.phase_focus)
            TimerPhase.SHORT_BREAK -> context.getString(R.string.phase_short_break)
            TimerPhase.LONG_BREAK -> context.getString(R.string.phase_long_break)
        }
        val timeText = TimeFormatter.formatMs(remainingMs)

        val openIntent = Intent(context, MainActivity::class.java).let {
            PendingIntent.getActivity(
                context, 0, it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        return NotificationCompat.Builder(context, Constants.TIMER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("$phaseText (${currentCycle}/4)")
            .setContentText("Time remaining: $timeText")
            .setOngoing(true)
            .setContentIntent(openIntent)
            .setProgress(100, ((remainingMs.toFloat() / (phase.toDefaultMs())) * 100).toInt(), false)
            .build()
    }

    fun buildAlertNotification(phase: TimerPhase): android.app.Notification {
        val title = "Time's up!"
        val message = if (phase == TimerPhase.FOCUS) {
            context.getString(R.string.time_up_focus)
        } else {
            context.getString(R.string.time_up_break)
        }

        val openIntent = Intent(context, MainActivity::class.java).let {
            PendingIntent.getActivity(
                context, 0, it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val builder = NotificationCompat.Builder(context, Constants.ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))

        // Full-screen intent for Android 10+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setFullScreenIntent(openIntent, true)
        }

        return builder.build()
    }

    fun showTimerNotification(phase: TimerPhase, remainingMs: Long, currentCycle: Int) {
        if (!hasNotificationPermission()) return
        val notification = buildTimerNotification(phase, remainingMs, currentCycle)
        NotificationManagerCompat.from(context).notify(
            Constants.TIMER_NOTIFICATION_ID, notification
        )
    }

    fun showAlertNotification(phase: TimerPhase) {
        if (!hasNotificationPermission()) return
        val notification = buildAlertNotification(phase)
        NotificationManagerCompat.from(context).notify(
            Constants.ALERT_NOTIFICATION_ID, notification
        )
    }

    fun cancelTimerNotification() {
        NotificationManagerCompat.from(context).cancel(Constants.TIMER_NOTIFICATION_ID)
    }

    fun cancelAlertNotification() {
        NotificationManagerCompat.from(context).cancel(Constants.ALERT_NOTIFICATION_ID)
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun TimerPhase.toDefaultMs(): Long {
        return when (this) {
            TimerPhase.FOCUS -> 25 * 60 * 1000L
            TimerPhase.SHORT_BREAK -> 5 * 60 * 1000L
            TimerPhase.LONG_BREAK -> 15 * 60 * 1000L
        }
    }
}
