package com.shoujopomodoro.ui.component

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoujopomodoro.R
import com.shoujopomodoro.service.MusicPlayerService
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@Composable
fun EnhancedMusicPlayerBar(
    musicPaths: List<String>,
    modifier: Modifier = Modifier
) {
    if (musicPaths.isEmpty()) return

    val context = LocalContext.current
    var service by remember { mutableStateOf<MusicPlayerService?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableIntStateOf(-1) }
    var volumePercent by remember { mutableIntStateOf(50) }
    var showVolumeSlider by remember { mutableStateOf(false) }
    var bindAttempts by remember { mutableIntStateOf(0) }

    val serviceConnection = remember {
        object : android.content.ServiceConnection {
            override fun onServiceConnected(name: android.content.ComponentName?, binder: android.os.IBinder?) {
                if (binder == null) return
                try {
                    val musicService = (binder as MusicPlayerService.MusicBinder).getService()
                    service = musicService
                    // Sync the playlist to the service
                    musicService.setPlaylist(musicPaths)
                    // Sync volume with system
                    musicService.refreshVolume()
                } catch (e: Exception) {
                    Log.e("MusicPlayerBar", "Service connection error", e)
                    service = null
                }
            }

            override fun onServiceDisconnected(name: android.content.ComponentName?) {
                service = null
            }
        }
    }

    // Bind to the service whenever musicPaths changes
    DisposableEffect(musicPaths) {
        val intent = Intent(context, MusicPlayerService::class.java)
        try {
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            Log.e("MusicPlayerBar", "Failed to bind music service", e)
        }

        onDispose {
            try {
                context.unbindService(serviceConnection)
            } catch (e: Exception) {
                // Service may already be unbound
            }
        }
    }

    // Sync local state with service StateFlows
    LaunchedEffect(service) {
        service?.isPlaying?.collectLatest { playing ->
            isPlaying = playing
        }
    }
    LaunchedEffect(service) {
        service?.currentTrackIndex?.collectLatest { index ->
            currentIndex = index
        }
    }
    LaunchedEffect(service) {
        service?.errorMessage?.collectLatest { error ->
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                service?.clearError()
            }
        }
    }
    LaunchedEffect(service) {
        service?.volumePercent?.collectLatest { vol ->
            volumePercent = vol
        }
    }

    // Retry binding if service not connected within a short time
    LaunchedEffect(musicPaths, bindAttempts) {
        if (service == null) {
            kotlinx.coroutines.delay(500)
            if (service == null && bindAttempts < 2) {
                bindAttempts++
            }
        }
    }

    val currentTrackName = if (currentIndex in musicPaths.indices) {
        File(musicPaths[currentIndex]).nameWithoutExtension
    } else {
        ""
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(CircleShape),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        shape = CircleShape
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side - Track info with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp).padding(end = 8.dp)
                    )

                    val textWidth = remember { mutableIntStateOf(0) }
                    val containerWidth = remember { mutableIntStateOf(0) }
                    val shouldMarquee =
                        currentTrackName.isNotBlank() && textWidth.intValue > containerWidth.intValue

                    val infiniteTransition = rememberInfiniteTransition()
                    val scrollOffset by infiniteTransition.animateFloat(
                        initialValue = if (shouldMarquee) containerWidth.intValue.toFloat() else 0f,
                        targetValue = if (shouldMarquee) -textWidth.intValue.toFloat() else 0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = ((textWidth.intValue + containerWidth.intValue) * 30)
                                    .coerceAtLeast(3000),
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Restart
                        )
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .onSizeChanged { containerWidth.intValue = it.width }
                    ) {
                        Text(
                            text = currentTrackName.ifBlank {
                                stringResource(R.string.no_music_playing)
                            },
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier
                                .onSizeChanged { textWidth.intValue = it.width }
                                .offset { IntOffset(scrollOffset.toInt(), 0) }
                        )
                    }
                }

                // Right side - Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(40.dp)
                ) {
                    IconButton(
                        onClick = { service?.playPrevious() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = stringResource(R.string.music_prev),
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            val svc = service
                            if (svc == null) {
                                Toast.makeText(context, R.string.music_service_starting, Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }
                            if (isPlaying) {
                                svc.pause()
                            } else {
                                if (svc.isMediaPlayerPlaying().not()) {
                                    // Start playing from the first track if nothing is loaded
                                    val idx = if (currentIndex >= 0) currentIndex else 0
                                    svc.play(idx)
                                } else {
                                    svc.resume()
                                }
                            }
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .padding(4.dp)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying)
                                    stringResource(R.string.music_pause)
                                else
                                    stringResource(R.string.music_play),
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = { service?.playNext() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = stringResource(R.string.music_next),
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { service?.stop() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = stringResource(R.string.music_stop),
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    IconButton(
                        onClick = { showVolumeSlider = !showVolumeSlider },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(
                            text = "🔊",
                            fontSize = 16.sp,
                            color = if (showVolumeSlider) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Volume slider (hidden by default, shown on icon tap)
            AnimatedVisibility(
                visible = showVolumeSlider,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔊",
                        fontSize = 12.sp
                    )
                    Slider(
                        value = volumePercent.toFloat(),
                        onValueChange = { service?.setVolumePercent(it.toInt()) },
                        valueRange = 0f..100f,
                        steps = 99,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 6.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    Text(
                        text = "${volumePercent}%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(36.dp)
                    )
                }
            }
        }
    }
}

// Logger for UI component (avoids dependency on android.util.Log in Compose previews)
private object Log {
    fun e(tag: String, msg: String, tr: Throwable? = null) {
        android.util.Log.e(tag, msg, tr)
    }
}
