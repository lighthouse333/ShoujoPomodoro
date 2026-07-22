package com.shoujopomodoro.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.shoujopomodoro.MainActivity
import com.shoujopomodoro.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class MusicPlayerService : Service() {

    companion object {
        const val NOTIFICATION_ID = 200
        const val CHANNEL_ID = "music_player_channel"
        const val ACTION_STOP = "com.shoujopomodoro.action.STOP_MUSIC"
    }

    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentTrackIndex = MutableStateFlow(-1)
    val currentTrackIndex: StateFlow<Int> = _currentTrackIndex.asStateFlow()

    private var playlist: List<String> = emptyList()
    private var onPlaybackComplete: (() -> Unit)? = null

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

    fun play(index: Int) {
        if (index < 0 || index >= playlist.size) return
        releasePlayer()
        val file = File(playlist[index])
        if (!file.exists()) return
        mediaPlayer = MediaPlayer().apply {
            setDataSource(playlist[index])
            prepare()
            start()
            _isPlaying.value = true
            _currentTrackIndex.value = index
            setOnCompletionListener {
                playNext()
            }
        }
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    fun playNext() {
        if (playlist.isEmpty()) return
        val nextIndex = (_currentTrackIndex.value + 1) % playlist.size
        play(nextIndex)
    }

    fun playPrevious() {
        if (playlist.isEmpty()) return
        val prevIndex = if (_currentTrackIndex.value - 1 < 0) playlist.size - 1 else _currentTrackIndex.value - 1
        play(prevIndex)
    }

    fun resume() {
        mediaPlayer?.start()
        _isPlaying.value = true
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    fun stop() {
        releasePlayer()
        _isPlaying.value = false
        _currentTrackIndex.value = -1
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun isMediaPlayerPlaying(): Boolean = mediaPlayer?.isPlaying == true

    private fun releasePlayer() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
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

    fun setOnPlaybackComplete(callback: () -> Unit) {
        onPlaybackComplete = callback
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }
}
