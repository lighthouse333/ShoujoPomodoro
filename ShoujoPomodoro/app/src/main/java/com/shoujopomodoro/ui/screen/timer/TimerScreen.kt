package com.shoujopomodoro.ui.screen.timer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoujopomodoro.R
import com.shoujopomodoro.domain.model.TimerPhase
import com.shoujopomodoro.ui.component.CelebrationOverlay
import com.shoujopomodoro.ui.component.CelebrationStyle
import com.shoujopomodoro.ui.component.CharacterOutfit
import com.shoujopomodoro.ui.component.EnhancedPhaseLabel
import com.shoujopomodoro.ui.component.EnhancedShoujoCharacter
import com.shoujopomodoro.ui.component.ParticleType
import com.shoujopomodoro.ui.component.PremiumCircularTimer
import com.shoujopomodoro.ui.component.PremiumControlButtons
import com.shoujopomodoro.ui.component.PremiumMusicPlayerBar
import com.shoujopomodoro.ui.component.SakuraParticleBackground
import com.shoujopomodoro.ui.component.SoftGradientBackground
import com.shoujopomodoro.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToStats: () -> Unit = {},
    viewModel: TimerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    // Live clock
    var currentTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        while (true) {
            currentTime = timeFormat.format(Date())
            delay(1000)
        }
    }

    // Celebration trigger
    var showCelebration by remember { mutableStateOf(false) }
    var prevPhase by remember { mutableStateOf(uiState.phase) }
    LaunchedEffect(uiState.phase) {
        // Trigger celebration on phase change to break (task completed!)
        if (prevPhase == TimerPhase.FOCUS && uiState.phase != TimerPhase.FOCUS) {
            showCelebration = true
        }
        prevPhase = uiState.phase
    }

    // Determine character outfit based on phase
    val outfit = when (uiState.phase) {
        TimerPhase.FOCUS -> CharacterOutfit.SAILOR
        TimerPhase.SHORT_BREAK -> CharacterOutfit.PASTEL
        TimerPhase.LONG_BREAK -> CharacterOutfit.MAGICAL
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkCard.copy(alpha = 0.85f)
                    else SakuraLight.copy(alpha = 0.85f),
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    if (uiState.clockPosition == "top_bar") {
                        Text(
                            text = currentTime,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Default.DateRange, contentDescription = "Focus Stats")
                    }
                    IconButton(onClick = onNavigateToTasks) {
                        Icon(Icons.Default.Checklist, contentDescription = stringResource(R.string.tasks))
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // ── Animated gradient background ──
            SoftGradientBackground(isDark = isDark)

            // ── Sakura particle layer ──
            SakuraParticleBackground(
                intensity = if (uiState.phase == TimerPhase.FOCUS) 0.6f else 0.9f,
                preferredType = if (uiState.phase != TimerPhase.FOCUS) ParticleType.SAKURA_PETAL else null
            )

            // ── Main content ──
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Clock at center
                if (uiState.clockPosition == "center") {
                    Text(
                        text = currentTime,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Phase label with enhanced style
                EnhancedPhaseLabel(
                    phase = uiState.phase,
                    currentCycle = uiState.currentCycle,
                    totalCycles = uiState.totalCycles
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── Character + Premium Timer overlay ──
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(320.dp)
                ) {
                    // Premium circular timer
                    PremiumCircularTimer(
                        progress = uiState.progress,
                        phase = uiState.phase,
                        containerSize = 320.dp,
                        strokeWidth = 10.dp
                    )

                    // Enhanced Shoujo character
                    EnhancedShoujoCharacter(
                        characterState = uiState.characterState,
                        size = 210.dp,
                        outfit = outfit,
                        hairStyle = 0
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Time display with glass effect ──
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            if (isDark) DarkCard.copy(alpha = 0.4f)
                            else Color.White.copy(alpha = 0.4f)
                        )
                        .padding(horizontal = 32.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = uiState.timeText,
                        fontSize = 68.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-2).sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Enhanced control buttons ──
                PremiumControlButtons(
                    isRunning = uiState.isRunning,
                    onStart = { viewModel.onStart() },
                    onPause = { viewModel.onPause() },
                    onReset = { viewModel.onReset() },
                    onSkip = { viewModel.onSkip() }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Music player bar ──
                AnimatedVisibility(
                    visible = uiState.musicPaths.isNotEmpty(),
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it },
                    exit = fadeOut(tween(200))
                ) {
                    Column {
                        PremiumMusicPlayerBar(musicPaths = uiState.musicPaths)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // ── Celebration overlay ──
            CelebrationOverlay(
                isVisible = showCelebration,
                style = CelebrationStyle.SAKURA_STORM,
                onFinished = { showCelebration = false }
            )
        }
    }
}
