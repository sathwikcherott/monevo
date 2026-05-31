package com.monevo.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.atmosphere.JourneyAtmosphere
import com.monevo.app.ui.atmosphere.JourneyState
import com.monevo.app.ui.atmosphere.getAdaptiveAccent
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

@Composable
fun TotalSavedCard(
    totalProvider: () -> Int,
    progressProvider: () -> Float,
    completedCountProvider: () -> Int,
    totalCountProvider: () -> Int,
    goalProvider: () -> Int,
    atmosphereProvider: () -> JourneyAtmosphere,
    journeyStateProvider: () -> JourneyState,
    modifier: Modifier = Modifier,
    isGlowActive: Boolean = false,
) {
    val motionSettings = LocalMotionSettings.current
    val progressValue = progressProvider()
    val isCompleted = progressValue >= 1f

    val atmosphere = atmosphereProvider()
    val journeyState = journeyStateProvider()
    val adaptiveAccent = atmosphere.getAdaptiveAccent()
    
    // Removed infinite breathing animation to reduce rendering overhead and visual busyness
    // Constant ambient movement is reduced for a calmer premium feel.

    val recognitionScale by animateFloatAsState(
        targetValue = if (isGlowActive) motionSettings.scaleValue(1.02f, 1f) else 1f,
        animationSpec = motionSettings.gentleSpring(),
        label = "recognitionScale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isGlowActive) motionSettings.scaleValue(0.15f, 0.08f) else 0f,
        animationSpec = tween(
            durationMillis = if (motionSettings.isReducedMotionEnabled) 150 else motionSettings.scaleDuration(600),
            easing = FastOutSlowInEasing
        ),
        label = "glowAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                // Only scale during active recognition moments
                val s = recognitionScale
                scaleX = s
                scaleY = s
            }
            .shadow(
                elevation = if (isGlowActive || isCompleted) motionSettings.scaleDp(2.dp, 0.5.dp) else 0.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = adaptiveAccent.copy(alpha = if (isCompleted) 0.08f else glowAlpha),
                ambientColor = Color.Transparent
            ),
        colors = CardDefaults.cardColors(containerColor = SurfaceBase),
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
                        text = journeyState.title.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = adaptiveAccent.copy(alpha = 0.8f),
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${totalProvider()}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                }
                
                Surface(
                    color = SurfaceElevated,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "${completedCountProvider()}/${totalCountProvider()}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = adaptiveAccent
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = journeyState.message,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { progressValue.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = adaptiveAccent,
                trackColor = DividerStroke.copy(alpha = 0.4f),
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
                    color = adaptiveAccent
                )
                Text(
                    text = "Goal: ₹%,d".format(goalProvider()),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}
