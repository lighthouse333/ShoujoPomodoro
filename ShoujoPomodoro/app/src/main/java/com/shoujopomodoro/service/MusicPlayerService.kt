package com.shoujopomodoro.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.shoujopomodoro.MainActivity
import com.shoujopomodoro.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class MusicPlayerService : Service() {

    companion object {
        const val TAG = "MusicPlayerService"
        const val NOTIFICATION_ID = 200
        const val CHANNEL_ID = "music_player_channel"
        const val ACTION_STOP = "com.shoujopomodoro.action.STOP_MUSIC"
    }

    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(-1)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var playlist: List<String> = emptyList()

    inner class MusicBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                getString(R.string.music_player),
                android.app.NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun setPlaylist(paths: List<String>) {
        playlist = paths
    }

    /**
     * Safe foreground start — uses the 3-parameter version on Android 14+
     * which requires explicit foregroundServiceType since API 34.
     */
    private fun startForegroundSafe(notification: android.app.Notification) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not start foreground: ${e.message}")
        }
    }

    /**
     * All MediaPlayer operations MUST run on the main thread.
     * MediaPlayer callbacks (onCompletion, onError) fire from native threads,
     * so we always post to the main looper.
     */
    private fun runOnMainThread(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            mainHandler.post(action)
        }
    }

    fun play(index: Int): Boolean {
        if (index < 0 || index >= playlist.size) {
            Log.w(TAG, "Invalid track index: $index, playlist size: ${playlist.size}")
            return false
        }
        val filePath = playlist[index]
        val file = File(filePath)
        if (!file.exists()) {
            Log.w(TAG, "File not found: $filePath")
            _errorMessage.value = "File not found: ${file.nameWithoutExtension}"
            return false
        }

        // Release previous player (must be on main thread)
        runOnMainThread { releasePlayerInternal() }

        // Set index synchronously so notification sees the correct value
        _currentTrackIndex.value = index

        val mp = MediaPlayer()
        return try {
            mp.setDataSource(filePath)
            mp.setOnCompletionListener {
                // Callback from native thread — post to main to avoid threading issues
                mainHandler.post {
                    if (mediaPlayer === mp) {
                        playNext()
                    }
                }
            }
            mp.setOnErrorListener { _, what, extra ->
                Log.e(TAG, "MediaPlayer error: what=$what extra=$extra")
                _errorMessage.value = "Playback error (code: $what)"
                // Post to main thread
                mainHandler.post {
                    if (mediaPlayer === mp) {
                        releasePlayerInternal()
                    }
                }
                true // error handled
            }
            mp.prepare()
            mp.start()
            mediaPlayer = mp
            _isPlaying.value = true

            startForegroundSafe(buildNotification())
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play: ${file.nameWithoutExtension}", e)
            _errorMessage.value = "Cannot play: ${file.nameWithoutExtension}"
            try { mp.reset() } catch (_: Exception) {}
            try { mp.release() } catch (_: Exception) {}
            _isPlaying.value = false
            false
        }
    }

    fun playNext(): Boolean {
        if (playlist.isEmpty()) return false
        val nextIndex = (_currentTrackIndex.value + 1) % playlist.size
        return play(nextIndex)
    }

    fun playPrevious(): Boolean {
        if (playlist.isEmpty()) return false
        val prevIndex = if (_currentTrackIndex.value - 1 < 0) playlist.size - 1 else _currentTrackIndex.value - 1
        return play(prevIndex)
    }

    fun resume() {
        runOnMainThread {
            val mp = mediaPlayer ?: return@runOnMainThread
            try {
                if (!mp.isPlaying) {
                    mp.start()
                    _isPlaying.value = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to resume", e)
                return@runOnMainThread
            }
            startForegroundSafe(buildNotification())
        }
    }

    fun pause() {
        runOnMainThread {
            try {
                mediaPlayer?.pause()
                _isPlaying.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Failed to pause", e)
            }
            try {
                stopForeground(STOP_FOREGROUND_DETACH)
            } catch (e: Exception) {
                Log.w(TAG, "Could not stop foreground: ${e.message}")
            }
        }
    }

    fun stop() {
        runOnMainThread {
            releasePlayerInternal()
            _isPlaying.value = false
            _currentTrackIndex.value = -1
            try {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } catch (e: Exception) {
                Log.w(TAG, "Could not stop foreground: ${e.message}")
            }
        }
        stopSelf()
    }

    fun isMediaPlayerPlaying(): Boolean = mediaPlayer?.isPlaying == true

    fun clearError() {
        _errorMessage.value = null
    }

    /** Must be called on the main thread */
    private fun releasePlayerInternal() {
        val mp = mediaPlayer
        mediaPlayer = null
        mp?.apply {
            try {
                if (isPlaying) stop()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping player", e)
            }
            try {
                reset()
            } catch (e: Exception) {
                Log.e(TAG, "Error resetting player", e)
            }
            try {
                release()
            } catch (e: Exception) {
                Log.e(TAG, "Error releasing player", e)
            }
        }
        _isPlaying.value = false
    }

    private fun buildNotification(): android.app.Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val trackName = if (_currentTrackIndex.value in playlist.indices) {
            File(playlist[_currentTrackIndex.value]).nameWithoutExtension
        } else ""

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.music_player))
            .setContentText(trackName.ifBlank { getString(R.string.no_music_playing) })
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        releasePlayerInternal()
        super.onDestroy()
    }
}
