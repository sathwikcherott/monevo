package com.monevo.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

/**
 * Premium cinematic AMOLED reconfiguration overlay.
 * Uses selective lighting in deep darkness.
 */
@Composable
fun ReconfiguringOverlay(isVisible: Boolean, targetGoal: Int) {
    val motionSettings = LocalMotionSettings.current
    
    // Memory-stable state
    val targetGoalInK = remember(targetGoal) { targetGoal / 1000 }
    
    val primaryMessage = remember(targetGoal) {
        when {
            targetGoal >= 100000 -> "Building Elite Journey"
            targetGoal >= 50000 -> "Reconfiguring Path"
            else -> "Preparing New Goal"
        }
    }
    
    val secondaryMessage = remember(targetGoalInK) {
        "Synchronizing your ₹${targetGoalInK}K progression"
    }

    // Optimization: Simplified transition to reduce offscreen rendering overhead
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = if (motionSettings.isReducedMotionEnabled) {
                tween(200, easing = LinearEasing)
            } else {
                tween(600)
            }
        ),
        exit = fadeOut(
            animationSpec = if (motionSettings.isReducedMotionEnabled) {
                tween(200, easing = LinearEasing)
            } else {
                tween(800)
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryBackground)
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                var percentage by remember { mutableIntStateOf(0) }
                
                LaunchedEffect(isVisible) {
                    if (isVisible) {
                        percentage = 0
                        if (motionSettings.isReducedMotionEnabled) {
                            animate(0f, 100f, animationSpec = tween(500, easing = LinearOutSlowInEasing)) { v, _ -> percentage = v.toInt() }
                        } else {
                            animate(0f, 40f, animationSpec = tween(800, easing = LinearOutSlowInEasing)) { v, _ -> percentage = v.toInt() }
                            kotlinx.coroutines.delay(150)
                            animate(40f, 85f, animationSpec = tween(1000, easing = FastOutSlowInEasing)) { v, _ -> percentage = v.toInt() }
                            kotlinx.coroutines.delay(200)
                            animate(85f, 100f, animationSpec = tween(600, easing = LinearEasing)) { v, _ -> percentage = v.toInt() }
                        }
                    }
                }

                Text(
                    text = primaryMessage,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = secondaryMessage,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary.copy(alpha = 0.5f),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(64.dp))

                // Premium Loading Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (motionSettings.isReducedMotionEnabled) 0.8f else 1f)
                        .height(2.dp)
                        .background(DividerStroke.copy(alpha = 0.2f))
                ) {
                    val widthScale by animateFloatAsState(
                        targetValue = percentage / 100f,
                        animationSpec = if (motionSettings.isReducedMotionEnabled) tween(300) else spring(stiffness = Spring.StiffnessVeryLow),
                        label = "loadingWidth"
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(widthScale)
                            .fillMaxHeight()
                            // Removed shadow to reduce compositing pressure during high-load reconfiguration
                            .background(PrimaryAccentPink)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftAccentPink.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
