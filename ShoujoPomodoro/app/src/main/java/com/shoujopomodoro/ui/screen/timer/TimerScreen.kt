package com.shoujopomodoro.ui.screen.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoujopomodoro.ui.component.CircularTimerIndicator
import com.shoujopomodoro.ui.component.PhaseLabel
import com.shoujopomodoro.ui.component.ShoujoCharacter
import com.shoujopomodoro.ui.component.TimerControlButtons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToTasks: () -> Unit,
    viewModel: TimerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Shoujo Pomodoro",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onNavigateToTasks) {
                        Icon(Icons.Default.Checklist, contentDescription = "Tasks")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Phase label
            PhaseLabel(
                phase = uiState.phase,
                currentCycle = uiState.currentCycle,
                totalCycles = uiState.totalCycles
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Character + Timer overlay
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(280.dp)
            ) {
                // Circular timer as background ring
                CircularTimerIndicator(
                    progress = uiState.progress,
                    timeText = "", // Time displayed below
                    phase = uiState.phase,
                    containerSize = 280.dp,
                    strokeWidth = 10.dp
                )

                // Character in the center
                ShoujoCharacter(
                    characterState = uiState.characterState,
                    size = 200.dp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time display
            Text(
                text = uiState.timeText,
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Control buttons
            TimerControlButtons(
                isRunning = uiState.isRunning,
                onStart = { viewModel.onStart() },
                onPause = { viewModel.onPause() },
                onReset = { viewModel.onReset() },
                onSkip = { viewModel.onSkip() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
