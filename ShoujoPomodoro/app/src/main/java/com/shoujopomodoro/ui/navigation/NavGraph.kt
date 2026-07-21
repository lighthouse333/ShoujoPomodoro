package com.shoujopomodoro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shoujopomodoro.ui.screen.settings.SettingsScreen
import com.shoujopomodoro.ui.screen.tasklist.TaskListScreen
import com.shoujopomodoro.ui.screen.timer.TimerScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
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

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
