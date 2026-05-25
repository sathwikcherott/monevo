package com.monevo.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

/**
 * Premium cinematic reconfiguration overlay.
 * Replaces generic loading with an intentional journey-building experience.
 */
@Composable
fun ReconfiguringOverlay(isVisible: Boolean, targetGoal: Int) {
    val motionSettings = LocalMotionSettings.current
    
    // Dynamic messaging based on goal
    val primaryMessage = remember(targetGoal) {
        when {
            targetGoal >= 100000 -> "Building Elite Journey"
            targetGoal >= 50000 -> "Reconfiguring Path"
            else -> "Preparing New Goal"
        }
    }
    
    val secondaryMessage = remember(targetGoal) {
        "Synchronizing your ₹${targetGoal / 1000}K progression"
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(motionSettings.scaleDuration(600))),
        exit = fadeOut(animationSpec = tween(motionSettings.scaleDuration(1000)))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Animated Percentage & Staged Pacing
                var percentage by remember { mutableIntStateOf(0) }
                
                LaunchedEffect(isVisible) {
                    if (isVisible) {
                        percentage = 0
                        // Stage 1: Initial sync (0-40%)
                        animate(0f, 40f, animationSpec = tween(600, easing = LinearOutSlowInEasing)) { v, _ -> percentage = v.toInt() }
                        kotlinx.coroutines.delay(100)
                        
                        // Stage 2: Milestone generation (40-85%)
                        animate(40f, 85f, animationSpec = tween(800, easing = FastOutSlowInEasing)) { v, _ -> percentage = v.toInt() }
                        kotlinx.coroutines.delay(150)
                        
                        // Stage 3: Final calibration (85-100%)
                        animate(85f, 100f, animationSpec = tween(500, easing = LinearEasing)) { v, _ -> percentage = v.toInt() }
                    }
                }

                // 2. Messaging
                Text(
                    text = primaryMessage,
                    style = MaterialTheme.typography.headlineSmall,
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = secondaryMessage,
                    style = MaterialTheme.typography.labelMedium,
                    color = SecondaryText.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(56.dp))

                // 3. Premium Loading Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (motionSettings.isReducedMotionEnabled) 0.8f else 1f)
                        .height(2.dp)
                        .background(ElevatedCard.copy(alpha = 0.3f))
                ) {
                    val widthScale by animateFloatAsState(
                        targetValue = percentage / 100f,
                        animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
                        label = "loadingWidth"
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(widthScale)
                            .fillMaxHeight()
                            .shadow(
                                elevation = if (motionSettings.isReducedMotionEnabled) 0.dp else 8.dp,
                                spotColor = AccentGold,
                                ambientColor = AccentGold
                            )
                            .background(AccentGold)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Subtle Gold Counter
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftGold.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
