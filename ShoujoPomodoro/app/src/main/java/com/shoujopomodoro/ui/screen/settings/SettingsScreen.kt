package com.shoujopomodoro.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoujopomodoro.R
import com.shoujopomodoro.domain.model.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Language section - using Buttons for reliable click handling
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.updateLanguage(AppLanguage.ENGLISH.code) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.language == AppLanguage.ENGLISH.code)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (uiState.language == AppLanguage.ENGLISH.code)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = stringResource(R.string.language_en),
                        fontWeight = if (uiState.language == AppLanguage.ENGLISH.code)
                            FontWeight.Bold
                        else
                            FontWeight.Normal,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { viewModel.updateLanguage(AppLanguage.CHINESE.code) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.language == AppLanguage.CHINESE.code)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (uiState.language == AppLanguage.CHINESE.code)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = stringResource(R.string.language_zh),
                        fontWeight = if (uiState.language == AppLanguage.CHINESE.code)
                            FontWeight.Bold
                        else
                            FontWeight.Normal,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Focus duration
            SettingsSlider(
                label = stringResource(R.string.focus_duration),
                value = uiState.focusMinutes,
                valueRange = 5f..60f,
                steps = 10,
                unit = stringResource(R.string.focus_duration_unit),
                onValueChange = { viewModel.updateFocus(it.toInt()) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Short break
            SettingsSlider(
                label = stringResource(R.string.short_break_duration),
                value = uiState.shortBreakMinutes,
                valueRange = 1f..15f,
                steps = 13,
                unit = stringResource(R.string.short_break_unit),
                onValueChange = { viewModel.updateShortBreak(it.toInt()) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Long break
            SettingsSlider(
                label = stringResource(R.string.long_break_duration),
                value = uiState.longBreakMinutes,
                valueRange = 5f..30f,
                steps = 4,
                unit = stringResource(R.string.long_break_unit),
                onValueChange = { viewModel.updateLongBreak(it.toInt()) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Cycles
            SettingsSlider(
                label = stringResource(R.string.cycles_count),
                value = uiState.cyclesBeforeLongBreak,
                valueRange = 1f..10f,
                steps = 8,
                unit = stringResource(R.string.cycles_unit),
                onValueChange = { viewModel.updateCycles(it.toInt()) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Reset button
            Button(
                onClick = { viewModel.resetDefaults() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(stringResource(R.string.reset_defaults))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SettingsSlider(
    label: String,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    unit: String,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label: $value $unit",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps
        )
        // Min / Max labels
        Text(
            text = "${valueRange.start.toInt()} $unit  —  ${valueRange.endInclusive.toInt()} $unit",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
