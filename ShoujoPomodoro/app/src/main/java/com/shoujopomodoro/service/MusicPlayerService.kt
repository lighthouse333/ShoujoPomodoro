package com.shoujopomodoro.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
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
import java.io.FileInputStream

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

    // Audio focus
    private var audioManager: AudioManager? = null
    private var hasAudioFocus = false
    private var audioFocusRequest: AudioFocusRequest? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(-1)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _volumePercent = MutableStateFlow(50)
    val volumePercent: StateFlow<Int> = _volumePercent.asStateFlow()

    private var playlist: List<String> = emptyList()

    inner class MusicBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        // Notification channel
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                CHANNEL_ID,
                getString(R.string.music_player),
                android.app.NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
        // Audio manager for focus handling
        audioManager = getSystemService(AUDIO_SERVICE) as? AudioManager
    }

    fun setPlaylist(paths: List<String>) {
        playlist = paths
    }

    // ── Audio Focus ──────────────────────────────────────────────

    private fun requestAudioFocus(): Boolean {
        val am = audioManager ?: return false
        if (hasAudioFocus) return true

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setOnAudioFocusChangeListener(audioFocusListener)
                    .build()
                audioFocusRequest = focusRequest
                val result = am.requestAudioFocus(focusRequest)
                hasAudioFocus = (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                hasAudioFocus
            } else {
                @Suppress("DEPRECATION")
                val result = am.requestAudioFocus(
                    audioFocusListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
                hasAudioFocus = (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                hasAudioFocus
            }
        } catch (e: Exception) {
            Log.w(TAG, "Audio focus request failed: ${e.message}")
            false
        }
    }

    private fun abandonAudioFocus() {
        if (!hasAudioFocus) return
        val am = audioManager ?: return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { am.abandonAudioFocusRequest(it) }
                audioFocusRequest = null
            } else {
                @Suppress("DEPRECATION")
                am.abandonAudioFocus(audioFocusListener)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Audio focus abandon failed: ${e.message}")
        }
        hasAudioFocus = false
    }

    private val audioFocusListener = object : AudioManager.OnAudioFocusChangeListener {
        override fun onAudioFocusChange(focusChange: Int) {
            Log.d(TAG, "Audio focus change: $focusChange")
            mainHandler.post {
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_GAIN -> {
                        // Resume playback if it was paused due to transient loss
                        if (!isMediaPlayerPlaying()) {
                            resume()
                        }
                    }
                    AudioManager.AUDIOFOCUS_LOSS -> {
                        // Permanent loss — stop playback (e.g., another app started playing)
                        abandonAudioFocus()
                        pause()
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        // Transient loss — pause (e.g., phone call)
                        pause()
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                        // Transient loss but we can duck — lower volume or just keep playing
                        // For a pomodoro app, just keep playing at normal volume
                    }
                }
            }
        }
    }

    // ── Foreground Service ──────────────────────────────────────

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
        } catch (e: RuntimeException) {
            Log.w(TAG, "Could not start foreground: ${e.message}")
            // On some devices (Xiaomi, etc.), foreground service start may fail.
            // Music playback can continue without the notification.
        } catch (e: Exception) {
            Log.w(TAG, "Could not start foreground: ${e.message}")
        }
    }

    private fun stopForegroundSafe(detach: Boolean) {
        try {
            if (detach) {
                stopForeground(STOP_FOREGROUND_DETACH)
            } else {
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not stop foreground: ${e.message}")
        }
    }

    // ── Thread Safety ───────────────────────────────────────────

    /**
     * All MediaPlayer operations MUST run on the main thread.
     */
    private fun runOnMainThread(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            mainHandler.post(action)
        }
    }

    /** Ensure [action] runs on the main thread and blocks until it completes. */
    private fun ensureMainThread(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            val latch = java.util.concurrent.CountDownLatch(1)
            mainHandler.post {
                try { action() } finally { latch.countDown() }
            }
            try { latch.await() } catch (_: InterruptedException) {}
        }
    }

    // ── Playback ────────────────────────────────────────────────

    fun play(index: Int): Boolean {
        // Validate
        if (index < 0 || index >= playlist.size) {
            Log.w(TAG, "Invalid track index: $index, playlist size: ${playlist.size}")
            return false
        }
        val filePath = playlist[index]
        val file = File(filePath)
        if (!file.exists() || !file.canRead()) {
            Log.w(TAG, "File not found or unreadable: $filePath")
            _errorMessage.value = "File not found: ${file.nameWithoutExtension}"
            return false
        }

        // All MP operations must be on main thread — synchronize
        var result = false
        ensureMainThread {
            result = playOnMainThread(file)
            if (result) {
                _currentTrackIndex.value = index
            }
        }
        return result
    }

    /** Must be called from main thread */
    private fun playOnMainThread(file: File): Boolean {
        // Release any existing player
        releasePlayerInternal()

        val filePath = file.absolutePath

        // Request audio focus before attempting playback
        requestAudioFocus()

        val mp = MediaPlayer()
        var fis: FileInputStream? = null
        return try {
            // Set audio attributes (required for proper routing on multi-output devices)
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )

            // Use FileDescriptor — more compatible across devices than String path.
            // Some OEM MediaPlayer implementations (Xiaomi, older Samsung) handle
            // FileDescriptor more reliably than file-path strings.
            fis = FileInputStream(file)
            mp.setDataSource(fis.fd)
            fis.close() // FD is dup'd by MediaPlayer, safe to close
            fis = null

            mp.setOnCompletionListener {
                mainHandler.post {
                    if (mediaPlayer === mp) {
                        playNext()
                    }
                }
            }
            mp.setOnErrorListener { _, what, extra ->
                Log.e(TAG, "MediaPlayer error: what=$what extra=$extra file=${file.name}")
                val msg = when (what) {
                    MediaPlayer.MEDIA_ERROR_SERVER_DIED ->
                        "Audio server died — please restart the app"
                    MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                        if (extra == MediaPlayer.MEDIA_ERROR_IO) "Cannot read audio file"
                        else if (extra == MediaPlayer.MEDIA_ERROR_UNSUPPORTED) "Unsupported audio format"
                        else "Playback error (code: $what, extra: $extra)"
                    }
                    else -> "Playback error (code: $what)"
                }
                _errorMessage.value = msg
                mainHandler.post {
                    if (mediaPlayer === mp) {
                        releasePlayerInternal()
                        abandonAudioFocus()
                    }
                }
                true // error handled
            }
            mp.setOnInfoListener { _, what, _ ->
                Log.d(TAG, "MediaPlayer info: $what")
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    Log.d(TAG, "Buffering started for: ${file.name}")
                }
                false // let MediaPlayer handle it
            }

            // Use synchronous prepare() — for local files this is <50ms and avoids
            // the async-callback timing issues that can crash on some devices.
            mp.prepare()
            mp.start()
            mediaPlayer = mp
            _isPlaying.value = true

            startForegroundSafe(buildNotification())
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play: ${file.nameWithoutExtension}", e)
            val errorMsg = when {
                e is java.io.FileNotFoundException -> "File not accessible"
                e is java.io.IOException && e.message?.contains("setDataSource") == true ->
                    "Cannot read audio file (unsupported format or corrupted)"
                e is java.io.IOException -> "Audio playback IO error"
                e is IllegalStateException -> "Player error — please try again"
                else -> "Cannot play: ${file.nameWithoutExtension}"
            }
            _errorMessage.value = errorMsg
            try { fis?.close() } catch (_: Exception) {}
            try { mp.reset() } catch (_: Exception) {}
            try { mp.release() } catch (_: Exception) {}
            _isPlaying.value = false
            abandonAudioFocus()
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
                    requestAudioFocus()
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
            stopForegroundSafe(detach = true)
        }
    }

    fun stop() {
        runOnMainThread {
            releasePlayerInternal()
            abandonAudioFocus()
            _isPlaying.value = false
            _currentTrackIndex.value = -1
            stopForegroundSafe(detach = false)
        }
        stopSelf()
    }

    fun isMediaPlayerPlaying(): Boolean = mediaPlayer?.isPlaying == true

    fun clearError() {
        _errorMessage.value = null
    }

    // ── Volume Control ─────────────────────────────────────────

    fun refreshVolume() {
        val am = audioManager ?: return
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val current = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        _volumePercent.value = if (max > 0) (current * 100 / max) else 50
    }

    fun setVolumePercent(percent: Int) {
        val am = audioManager ?: return
        val clamped = percent.coerceIn(0, 100)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val target = (clamped * max / 100).coerceAtMost(max)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, target, 0)
        _volumePercent.value = clamped
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

    // ── Notification ────────────────────────────────────────────

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
        abandonAudioFocus()
        super.onDestroy()
    }
}
