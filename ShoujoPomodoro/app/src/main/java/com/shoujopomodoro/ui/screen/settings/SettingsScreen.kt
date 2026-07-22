package com.shoujopomodoro.ui.screen.settings

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoujopomodoro.R
import com.shoujopomodoro.domain.model.AppLanguage
import com.shoujopomodoro.ui.component.SakuraParticleBackground
import com.shoujopomodoro.ui.component.SoftGradientBackground
import com.shoujopomodoro.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val musicPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) viewModel.importMusic(uris)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkCard.copy(alpha = 0.85f) else SakuraLight.copy(alpha = 0.85f),
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            SoftGradientBackground(isDark = isDark)
            SakuraParticleBackground(intensity = 0.2f)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Language ──
                SettingsSection(title = stringResource(R.string.language)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        PremiumToggleButton(
                            text = stringResource(R.string.language_en),
                            selected = uiState.language == AppLanguage.ENGLISH.code,
                            onClick = { viewModel.updateLanguage(AppLanguage.ENGLISH.code) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        PremiumToggleButton(
                            text = stringResource(R.string.language_zh),
                            selected = uiState.language == AppLanguage.CHINESE.code,
                            onClick = { viewModel.updateLanguage(AppLanguage.CHINESE.code) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Clock Position ──
                SettingsSection(title = stringResource(R.string.clock_position)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        PremiumToggleButton(
                            text = stringResource(R.string.clock_position_top_bar),
                            selected = uiState.clockPosition == "top_bar",
                            onClick = { viewModel.updateClockPosition("top_bar") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        PremiumToggleButton(
                            text = stringResource(R.string.clock_position_center),
                            selected = uiState.clockPosition == "center",
                            onClick = { viewModel.updateClockPosition("center") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Timer Durations ──
                SettingsSlider(
                    label = stringResource(R.string.focus_duration),
                    value = uiState.focusMinutes,
                    valueRange = 5f..60f, steps = 10,
                    unit = stringResource(R.string.focus_duration_unit),
                    onValueChange = { viewModel.updateFocus(it.toInt()) },
                    activeColor = SakuraDeep
                )
                Spacer(modifier = Modifier.height(20.dp))

                SettingsSlider(
                    label = stringResource(R.string.short_break_duration),
                    value = uiState.shortBreakMinutes,
                    valueRange = 1f..15f, steps = 13,
                    unit = stringResource(R.string.short_break_unit),
                    onValueChange = { viewModel.updateShortBreak(it.toInt()) },
                    activeColor = MatchaMint
                )
                Spacer(modifier = Modifier.height(20.dp))

                SettingsSlider(
                    label = stringResource(R.string.long_break_duration),
                    value = uiState.longBreakMinutes,
                    valueRange = 5f..30f, steps = 4,
                    unit = stringResource(R.string.long_break_unit),
                    onValueChange = { viewModel.updateLongBreak(it.toInt()) },
                    activeColor = SkyBlue
                )
                Spacer(modifier = Modifier.height(20.dp))

                SettingsSlider(
                    label = stringResource(R.string.cycles_count),
                    value = uiState.cyclesBeforeLongBreak,
                    valueRange = 1f..10f, steps = 8,
                    unit = stringResource(R.string.cycles_unit),
                    onValueChange = { viewModel.updateCycles(it.toInt()) },
                    activeColor = WisteriaDeep
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ── Music ──
                SettingsSection(title = stringResource(R.string.music_player)) {
                    Button(
                        onClick = { musicPickerLauncher.launch(arrayOf("audio/*")) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WisteriaDeep)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.import_music), fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.musicPaths.isEmpty()) {
                        Text(
                            text = stringResource(R.string.music_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Text(
                            text = "${stringResource(R.string.music_imported)} (${uiState.musicPaths.size})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        uiState.musicPaths.forEach { path ->
                            MusicTrackCard(path = path, onRemove = { viewModel.removeMusic(path) })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ── Reset ──
                Button(
                    onClick = { viewModel.resetDefaults() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRose.copy(alpha = 0.8f))
                ) {
                    Text(stringResource(R.string.reset_defaults), fontSize = 15.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    val isDark = isSystemInDarkTheme()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDark) DarkCard.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.4f))
            .padding(20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun PremiumToggleButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) SakuraDeep
            else if (isDark) DarkSurface else Color(0xFFF0F0F5),
            contentColor = if (selected) Color.White
            else if (isDark) Color(0xFFCCCCCC) else Color(0xFF666666)
        )
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SettingsSlider(
    label: String, value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int, unit: String,
    onValueChange: (Float) -> Unit,
    activeColor: Color
) {
    val isDark = isSystemInDarkTheme()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDark) DarkCard.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "$value $unit",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = activeColor
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = activeColor,
                activeTrackColor = activeColor,
                inactiveTrackColor = activeColor.copy(alpha = 0.15f)
            )
        )
    }
}

@Composable
private fun MusicTrackCard(path: String, onRemove: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) DarkSurface.copy(alpha = 0.5f) else Color(0xFFF8F8FF).copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.MusicNote, contentDescription = null, tint = SakuraDeep, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = File(path).nameWithoutExtension,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_task), tint = CoralRose.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
            }
        }
    }
}
