package com.shoujopomodoro.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

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
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
            // Focus duration
            SettingsSlider(
                label = "Focus Duration",
                value = uiState.focusMinutes,
                valueRange = 5f..60f,
                steps = 10,
                unit = "min",
                onValueChange = { viewModel.updateFocus(it.toInt()) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Short break
            SettingsSlider(
                label = "Short Break",
                value = uiState.shortBreakMinutes,
                valueRange = 1f..15f,
                steps = 13,
                unit = "min",
                onValueChange = { viewModel.updateShortBreak(it.toInt()) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Long break
            SettingsSlider(
                label = "Long Break",
                value = uiState.longBreakMinutes,
                valueRange = 5f..30f,
                steps = 4,
                unit = "min",
                onValueChange = { viewModel.updateLongBreak(it.toInt()) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Cycles
            SettingsSlider(
                label = "Cycles before long break",
                value = uiState.cyclesBeforeLongBreak,
                valueRange = 1f..10f,
                steps = 8,
                unit = "×",
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
                Text("Reset to Defaults")
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
