package com.monevo.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.atmosphere.*
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun MilestoneAccordionHeader(
    name: String,
    isExpanded: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isGlowActive: Boolean = false,
    atmosphereProvider: () -> JourneyAtmosphere = { JourneyAtmosphere.FreshStart }
) {
    val motionSettings = LocalMotionSettings.current
    val haptic = LocalHapticFeedback.current
    val targetAtmosphere = atmosphereProvider()
    val atmosphere = rememberAnimatedAtmosphere(targetAtmosphere)

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(
            durationMillis = motionSettings.scaleDuration(400),
            easing = FastOutSlowInEasing
        ),
        label = "rotation"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isGlowActive) motionSettings.scaleValue(0.3f, 0.15f) else 0f,
        animationSpec = if (motionSettings.isReducedMotionEnabled) {
            tween(200, easing = LinearEasing)
        } else {
            tween(
                durationMillis = 800, 
                easing = EaseOutExpo
            )
        },
        label = "glowAlpha"
    )

    // Flattened visual layers: use alpha instead of complex shadows where possible
    val containerAlpha = if (isLocked) 0.5f else 1f
    
    Surface(
        onClick = if (isLocked) ({}) else {
            {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        },
        color = Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { 
                this.alpha = containerAlpha 
            }
            .drawBehind {
                val richness = atmosphere.richness.value
                drawRoundRect(
                    color = SurfaceBase.copy(alpha = 0.7f + (0.3f * richness)),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx())
                )
            }
            .then(
                if (isGlowActive) {
                    val richness = atmosphere.richness.value
                    val warmth = atmosphere.warmth.value
                    val accent = getAdaptiveAccent(warmth)
                    Modifier.shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = accent.copy(alpha = glowAlpha * richness),
                        ambientColor = Color.Transparent
                    )
                } else Modifier
            )
    ) {
        val warmth = atmosphere.warmth.value
        val adaptiveAccent = getAdaptiveAccent(warmth)
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isExpanded) adaptiveAccent else if (isLocked) TextSecondary else TextPrimary,
                    fontWeight = if (isExpanded) FontWeight.Bold else FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
            
            if (!isLocked) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = if (isExpanded) adaptiveAccent else TextSecondary,
                    modifier = Modifier.let {
                        if (rotation != 0f) it.graphicsLayer { rotationZ = rotation } else it
                    }
                )
            }
        }
    }
}
