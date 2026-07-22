package com.shoujopomodoro.ui.screen.tasklist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoujopomodoro.R
import com.shoujopomodoro.domain.model.Task
import com.shoujopomodoro.ui.component.SakuraParticleBackground
import com.shoujopomodoro.ui.component.SoftGradientBackground
import com.shoujopomodoro.ui.component.TaskInputDialog
import com.shoujopomodoro.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateBack: () -> Unit,
    viewModel: TaskListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDark = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.tasks),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkCard.copy(alpha = 0.85f)
                    else SakuraLight.copy(alpha = 0.85f),
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                containerColor = SakuraDeep,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.shadow(8.dp, CircleShape, ambientColor = SakuraDeep.copy(alpha = 0.3f))
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_task))
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Background
            SoftGradientBackground(isDark = isDark)
            SakuraParticleBackground(intensity = 0.3f)

            if (uiState.tasks.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (isDark) DarkCard.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.4f))
                            .padding(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlaylistAddCheck,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = SakuraDeep.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.empty_task_list),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    items(uiState.tasks, key = { it.id }) { task ->
                        PremiumTaskCard(
                            task = task,
                            onToggleComplete = { viewModel.toggleComplete(task) },
                            onSetCurrent = { viewModel.setCurrentTask(task.id) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) } // FAB space
                }
            }

            // Add task dialog
            if (uiState.showAddDialog) {
                TaskInputDialog(
                    onDismiss = { viewModel.dismissAddDialog() },
                    onConfirm = { name -> viewModel.addTask(name) }
                )
            }
        }
    }
}

@Composable
private fun PremiumTaskCard(
    task: Task,
    onToggleComplete: () -> Unit,
    onSetCurrent: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val cardColor = when {
        task.isCurrentTask -> SakuraGlow.copy(alpha = 0.5f)
        isDark -> DarkCard.copy(alpha = 0.4f)
        else -> Color.White.copy(alpha = 0.5f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Complete checkbox
            IconButton(onClick = onToggleComplete, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Outlined.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                    contentDescription = "Toggle complete",
                    tint = if (task.isCompleted) MatchaMint else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Task name
            Text(
                text = task.name,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (task.isCompleted)
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Current task indicator
            IconButton(onClick = onSetCurrent, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = if (task.isCurrentTask) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Set current",
                    tint = if (task.isCurrentTask) SakuraDeep else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(22.dp)
                )
            }

            // Delete
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = CoralRose.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
