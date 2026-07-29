package com.shoujopomodoro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shoujopomodoro.MainActivity
import com.shoujopomodoro.ui.screen.settings.SettingsScreen
import com.shoujopomodoro.ui.screen.stats.StatsScreen
import com.shoujopomodoro.ui.screen.tasklist.TaskListScreen
import com.shoujopomodoro.ui.screen.timer.TimerScreen
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    activity: MainActivity? = null
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Timer.route
    ) {
        composable(Screen.Timer.route) {
            TimerScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToTasks = {
                    navController.navigate(Screen.TaskList.route)
                },
                onNavigateToStats = {
                    navController.navigate(Screen.Stats.route)
                }
            )
        }

        composable(Screen.TaskList.route) {
            TaskListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            val settingsViewModel: com.shoujopomodoro.ui.screen.settings.SettingsViewModel = viewModel()
            // Wire up the hot-switch callback to Activity.recreate()
            val ctx = LocalContext.current
            settingsViewModel.onLanguageChanged = {
                (ctx as? MainActivity)?.recreate()
            }
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = settingsViewModel
            )
        }
    }
}
