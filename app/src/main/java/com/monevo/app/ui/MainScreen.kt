package com.monevo.app.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.monevo.app.debug.DebugHapticInterceptor
import com.monevo.app.debug.DebugMilestoneOverlay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.monevo.app.ui.components.MonevoBottomNavigation
import com.monevo.app.ui.components.ReconfiguringOverlay
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.motion.ProvideMotionSettings
import com.monevo.app.ui.screens.HomeScreen
import com.monevo.app.ui.screens.OnboardingScreen
import com.monevo.app.ui.screens.ProgressScreen
import com.monevo.app.ui.screens.ProfileScreen
import com.monevo.app.ui.screens.CinematicEntrance

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Outlined.Home)
    object Progress : Screen("progress", "Progress", Icons.Outlined.DateRange)
    object Profile : Screen("profile", "Profile", Icons.Outlined.Person)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val viewModel: SavingsViewModel = viewModel()
    
    ProvideMotionSettings(isReducedMotionEnabled = viewModel.isReducedMotionEnabled) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!viewModel.isOnboardingCompleted) {
                DebugHapticInterceptor(isAppHapticsEnabled = viewModel.isHapticsEnabled) {
                    OnboardingScreen(onFinish = { viewModel.completeOnboarding() })
                }
            } else {
                val items = listOf(
                    Screen.Home,
                    Screen.Progress,
                    Screen.Profile
                )

                DebugHapticInterceptor(isAppHapticsEnabled = viewModel.isHapticsEnabled) {
                    Scaffold(
                        bottomBar = {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            
                            val isBottomBarVisible = remember { mutableStateOf(false) }
                            LaunchedEffect(viewModel.isFreshStartArrival, viewModel.isAppLaunchEntrance) {
                                if (viewModel.isFreshStartArrival || viewModel.isAppLaunchEntrance) {
                                    isBottomBarVisible.value = true
                                }
                            }

                            CinematicEntrance(
                                index = 8, 
                                isTriggered = isBottomBarVisible.value
                            ) {
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
                        }
                    ) { innerPadding ->
                        val motionSettings = LocalMotionSettings.current
                        val navDuration = motionSettings.scaleDuration(400, 0.5f)
                        
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding),
                            enterTransition = { fadeIn(animationSpec = tween(navDuration)) },
                            exitTransition = { fadeOut(animationSpec = tween(navDuration)) },
                            popEnterTransition = { fadeIn(animationSpec = tween(navDuration)) },
                            popExitTransition = { fadeOut(animationSpec = tween(navDuration)) }
                        ) {
                            composable(Screen.Home.route) { HomeScreen(viewModel) }
                            composable(Screen.Progress.route) { 
                                ProgressScreen(
                                    viewModel = viewModel,
                                    onNavigateHome = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ) 
                            }
                            composable(Screen.Profile.route) { 
                                ProfileScreen(
                                    viewModel = viewModel,
                                    onNavigateHome = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                ) 
                            }
                        }
                    }
                }
            }

            // [DEBUG] Milestone progression tester - Always mounted to support developer testing
            DebugMilestoneOverlay(viewModel)

            // Goal reconfiguration overlay - Highest priority layer
            ReconfiguringOverlay(
                isVisible = viewModel.isReconfiguring,
                targetGoal = viewModel.reconfiguringGoal
            )
        }
    }
}
