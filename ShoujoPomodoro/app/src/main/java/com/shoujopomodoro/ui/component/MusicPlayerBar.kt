package com.shoujopomodoro.ui.component

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoujopomodoro.R
import com.shoujopomodoro.service.MusicPlayerService
import java.io.File

@Composable
fun MusicPlayerBar(
    musicPaths: List<String>,
    modifier: Modifier = Modifier
) {
    if (musicPaths.isEmpty()) return

    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableStateOf(0) }
    var service by remember { mutableStateOf<MusicPlayerService?>(null) }

    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val musicService = (binder as MusicPlayerService.MusicBinder).getService()
                service = musicService
                musicService.setPlaylist(musicPaths)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
            }
        }
    }

    DisposableEffect(musicPaths) {
        val intent = Intent(context, MusicPlayerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        onDispose {
            try {
                context.unbindService(serviceConnection)
            } catch (e: Exception) {
                // Service may already be unbound
            }
        }
    }

    val currentTrackName = if (currentIndex in musicPaths.indices) {
        File(musicPaths[currentIndex]).nameWithoutExtension
    } else {
        ""
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.MusicNote,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = currentTrackName.ifBlank {
                stringResource(R.string.no_music_playing)
            },
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                service?.playPrevious()
                currentIndex = service?.currentTrackIndex?.value ?: currentIndex
                isPlaying = true
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.SkipPrevious,
                contentDescription = stringResource(R.string.music_prev),
                modifier = Modifier.size(18.dp)
            )
        }

        IconButton(
            onClick = {
                if (isPlaying) {
                    service?.pause()
                    isPlaying = false
                } else {
                    if (service?.isMediaPlayerPlaying() == false) {
                        service?.play(currentIndex)
                    } else {
                        service?.resume()
                    }
                    isPlaying = true
                }
            },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying)
                    stringResource(R.string.music_pause)
                else
                    stringResource(R.string.music_play),
                modifier = Modifier.size(22.dp)
            )
        }

        IconButton(
            onClick = {
                service?.playNext()
                currentIndex = service?.currentTrackIndex?.value ?: currentIndex
                isPlaying = true
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.SkipNext,
                contentDescription = stringResource(R.string.music_next),
                modifier = Modifier.size(18.dp)
            )
        }

        IconButton(
            onClick = {
                service?.stop()
                isPlaying = false
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.Stop,
                contentDescription = stringResource(R.string.music_stop),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}