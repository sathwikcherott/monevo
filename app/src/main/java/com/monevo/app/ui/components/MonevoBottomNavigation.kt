package com.monevo.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import com.monevo.app.ui.theme.*

@Composable
fun MonevoBottomNavigation(
    screens: List<Screen>,
    currentDestination: NavDestination?,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = PrimaryBackground,
        tonalElevation = 0.dp,
        modifier = Modifier
            .height(84.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        screens.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            
            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1.0f,
                animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
                label = "iconScale"
            )

            val labelAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.5f,
                animationSpec = spring(),
                label = "labelAlpha"
            )

            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = screen.icon, 
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer {
                                scaleX = iconScale
                                scaleY = iconScale
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
                        modifier = Modifier.graphicsLayer { alpha = labelAlpha }
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
