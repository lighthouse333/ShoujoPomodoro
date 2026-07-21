package com.shoujopomodoro.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.shoujopomodoro.ShoujoPomodoroApp
import com.shoujopomodoro.util.Constants

class TimerForegroundService : Service() {

    private lateinit var stateHolder: com.shoujopomodoro.di.TimerStateHolder
    private lateinit var notificationHelper: com.shoujopomodoro.notification.NotificationHelper

    override fun onCreate() {
        super.onCreate()
        val container = (application as ShoujoPomodoroApp).container
        stateHolder = container.timerStateHolder
        notificationHelper = container.notificationHelper

        // Observe timer state for notification updates
        // This is handled by the ViewModel when in foreground;
        // When in background, the service updates notifications via onTimerComplete callback
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTION_START -> stateHolder.start()
            Constants.ACTION_PAUSE -> stateHolder.pause()
            Constants.ACTION_RESET -> stateHolder.reset()
            Constants.ACTION_SKIP -> stateHolder.skipToNextPhase()
            Constants.ACTION_STOP_SERVICE -> {
                stateHolder.pause()
                stopForeground(STOP_FOREGROUND_REMOVE)
                notificationHelper.cancelTimerNotification()
                stopSelf()
                return START_NOT_STICKY
            }
        }

        // Start foreground with ongoing notification
        val session = stateHolder.session.value
        val notification = notificationHelper.buildTimerNotification(
            phase = session.phase,
            remainingMs = session.remainingMs,
            currentCycle = session.currentCycle
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                Constants.TIMER_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(Constants.TIMER_NOTIFICATION_ID, notification)
        }

        // Setup callbacks
        stateHolder.onTimerComplete = { phase ->
            notificationHelper.cancelTimerNotification()
            notificationHelper.showAlertNotification(phase)
        }

        stateHolder.onPhaseChange = { phase, cycle ->
            // Update notification for the new phase
            val newSession = stateHolder.session.value
            notificationHelper.cancelAlertNotification()
            notificationHelper.showTimerNotification(phase, newSession.remainingMs, cycle)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        notificationHelper.cancelTimerNotification()
        super.onDestroy()
    }

    companion object {
        /**
         * Create intent to start the foreground service with a given action.
         */
        fun createIntent(context: android.content.Context, action: String): Intent {
            return Intent(context, TimerForegroundService::class.java).apply {
                this.action = action
            }
        }
    }
}
