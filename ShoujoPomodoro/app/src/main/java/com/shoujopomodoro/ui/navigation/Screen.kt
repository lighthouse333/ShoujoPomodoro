package com.shoujopomodoro.ui.navigation

sealed class Screen(val route: String) {
    data object Timer : Screen("timer")
    data object TaskList : Screen("task_list")
    data object Settings : Screen("settings")
}
