package com.monevo.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.monevo.app.ui.atmosphere.JourneyAtmosphere
import com.monevo.app.ui.atmosphere.getAdaptiveGold
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

@Composable
fun TotalSavedCard(
    totalProvider: () -> Int,
    progressProvider: () -> Float,
    completedCountProvider: () -> Int,
    totalCountProvider: () -> Int,
    goalProvider: () -> Int,
    modifier: Modifier = Modifier,
    isGlowActive: Boolean = false,
    atmosphere: JourneyAtmosphere = JourneyAtmosphere.FreshStart
) {
    val motionSettings = LocalMotionSettings.current
    val progressValue = progressProvider()
    val isCompleted = progressValue >= 1f

    // Atmosphere-aware values
    val adaptiveGold = atmosphere.getAdaptiveGold()
    val basePulseIntensity = motionSettings.scaleValue(1.02f, 1.005f)
    // Scale breathing intensity with progress
    val breathingTarget = 1f + (basePulseIntensity - 1f) * atmosphere.glowIntensity

    // Subtle pulse for completion
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCompleted) basePulseIntensity else breathingTarget,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = motionSettings.scaleDuration(2000), 
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Cinematic one-time pulse/glow scale
    val recognitionScale by animateFloatAsState(
        targetValue = if (isGlowActive) motionSettings.scaleValue(1.04f, 1.01f) else 1f,
        animationSpec = motionSettings.gentleSpring(),
        label = "recognitionScale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isGlowActive) motionSettings.scaleValue(0.4f, 0.15f) else 0f,
        animationSpec = tween(
            durationMillis = motionSettings.scaleDuration(500), 
            easing = FastOutSlowInEasing
        ),
        label = "glowAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = if (isGlowActive) recognitionScale else breathingScale
                scaleY = if (isGlowActive) recognitionScale else breathingScale
            }
            .shadow(
                elevation = if (isGlowActive || isCompleted) motionSettings.scaleDp(12.dp, 2.dp) else 0.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = adaptiveGold.copy(alpha = if (isCompleted) 0.2f else glowAlpha)
            ),
        colors = CardDefaults.cardColors(containerColor = PrimaryCard),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Saved",
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryText
                    )
                    Text(
                        text = "₹${totalProvider()}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }
                
                Surface(
                    color = ElevatedCard,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${completedCountProvider()}/${totalCountProvider()}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = adaptiveGold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { progressValue.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = adaptiveGold,
                trackColor = ElevatedCard,
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(progressValue * 100).toInt()}% of goal",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = adaptiveGold
                )
                Text(
                    text = "Goal: ₹%,d".format(goalProvider()),
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
            }
        }
    }
}
