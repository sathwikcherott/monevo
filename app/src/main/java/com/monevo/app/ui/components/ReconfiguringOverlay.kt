package com.monevo.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

/**
 * Premium reconfiguration overlay shown when regenerating milestones.
 */
@Composable
fun ReconfiguringOverlay(isVisible: Boolean) {
    val motionSettings = LocalMotionSettings.current
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(motionSettings.scaleDuration(500))),
        exit = fadeOut(animationSpec = tween(motionSettings.scaleDuration(800)))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated percentage counter logic
                var percentage by remember { mutableIntStateOf(0) }
                LaunchedEffect(isVisible) {
                    if (isVisible) {
                        percentage = 0
                        animate(
                            initialValue = 0f,
                            targetValue = 100f,
                            animationSpec = tween(
                                durationMillis = motionSettings.scaleDuration(1800), 
                                easing = LinearEasing
                            )
                        ) { value, _ -> percentage = value.toInt() }
                    }
                }

                Text(
                    text = "Updating Milestones",
                    style = MaterialTheme.typography.headlineSmall,
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Rebuilding your journey for the new goal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText,
                    letterSpacing = 0.2.sp
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Elegant horizontal gold loading bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(ElevatedCard)
                ) {
                    val widthScale by animateFloatAsState(
                        targetValue = percentage / 100f,
                        animationSpec = tween(100),
                        label = "loadingWidth"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(widthScale)
                            .fillMaxHeight()
                            .background(AccentGold)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.labelLarge,
                    color = SoftGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
