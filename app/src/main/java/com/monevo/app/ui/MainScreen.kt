package com.monevo.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.monevo.app.ui.components.MonevoBottomNavigation
import com.monevo.app.ui.screens.HomeScreen
import com.monevo.app.ui.screens.InsightsScreen
import com.monevo.app.ui.screens.OnboardingScreen
import com.monevo.app.ui.screens.ProgressScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Outlined.Home)
    object Progress : Screen("progress", "Progress", Icons.Outlined.DateRange)
    object Growth : Screen("growth", "Growth", Icons.AutoMirrored.Outlined.ShowChart)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val viewModel: SavingsViewModel = viewModel()
    
    if (!viewModel.isOnboardingCompleted) {
        OnboardingScreen(onFinish = { viewModel.completeOnboarding() })
    } else {
        val items = listOf(
            Screen.Home,
            Screen.Progress,
            Screen.Growth
        )

        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                MonevoBottomNavigation(
                    screens = items,
                    currentDestination = currentDestination,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { HomeScreen(viewModel) }
                composable(Screen.Progress.route) { ProgressScreen(viewModel) }
                composable(Screen.Growth.route) { InsightsScreen() }
            }
        }
    }
}
