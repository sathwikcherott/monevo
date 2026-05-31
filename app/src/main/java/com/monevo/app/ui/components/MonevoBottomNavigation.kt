package com.monevo.app.ui.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.monevo.app.ui.Screen
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

@Composable
fun MonevoBottomNavigation(
    screens: List<Screen>,
    currentDestination: NavDestination?,
    onNavigate: (Screen) -> Unit
) {
    val motionSettings = LocalMotionSettings.current
    val isReducedMotion = motionSettings.isReducedMotionEnabled
    
    NavigationBar(
        containerColor = PrimaryBackground,
        tonalElevation = 0.dp,
        modifier = Modifier
            .height(84.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        screens.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            
            // Restrained animations for everyday navigation to improve 120Hz stability
            val iconScale by animateFloatAsState(
                targetValue = if (isSelected && !isReducedMotion) 1.05f else 1.0f,
                animationSpec = tween(if (isReducedMotion) 0 else 300, easing = EaseOutCubic),
                label = "iconScale"
            )

            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = screen.icon, 
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .let {
                                if (iconScale != 1f) {
                                    it.graphicsLayer {
                                        scaleX = iconScale
                                        scaleY = iconScale
                                    }
                                } else it
                            }
                    ) 
                },
                label = { 
                    Text(
                        text = screen.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        // Reduced alpha stacking: apply alpha only when not selected to reduce pressure
                        color = if (isSelected) PrimaryAccentPink else TextSecondary.copy(alpha = 0.4f)
                    ) 
                },
                selected = isSelected,
                onClick = { onNavigate(screen) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryAccentPink,
                    selectedTextColor = PrimaryAccentPink,
                    unselectedIconColor = TextSecondary.copy(alpha = 0.4f),
                    unselectedTextColor = TextSecondary.copy(alpha = 0.4f),
                    indicatorColor = SurfaceElevated.copy(alpha = 0.3f)
                )
            )
        }
    }
}
