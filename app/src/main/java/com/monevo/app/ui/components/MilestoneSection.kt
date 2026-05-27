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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.model.SavingsTile
import com.monevo.app.ui.atmosphere.JourneyAtmosphere
import com.monevo.app.ui.atmosphere.getAdaptiveAccent
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
    atmosphere: JourneyAtmosphere = JourneyAtmosphere.FreshStart
) {
    val motionSettings = LocalMotionSettings.current
    val haptic = LocalHapticFeedback.current
    val adaptiveAccent = atmosphere.getAdaptiveAccent()

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
        animationSpec = tween(
            durationMillis = motionSettings.scaleDuration(600), 
            easing = FastOutSlowInEasing
        ),
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
        color = SurfaceBase,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .let { 
                if (containerAlpha < 1f) it.graphicsLayer { alpha = containerAlpha } else it 
            }
            .then(
                if (isGlowActive) {
                    Modifier.shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = adaptiveAccent.copy(alpha = glowAlpha),
                        ambientColor = Color.Transparent
                    )
                } else Modifier
            )
    ) {
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

@Composable
fun TileGrid(
    tiles: List<SavingsTile>,
    onTileClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isEntering: Boolean = false,
    baseStaggerIndex: Int = 0
) {
    val rows = remember(tiles) { tiles.chunked(5) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        rows.forEachIndexed { rowIndex, rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowTiles.forEach { tile ->
                    Box(modifier = Modifier.weight(1f)) {
                        val staggerIndex = baseStaggerIndex + rowIndex // Simple row-based stagger
                        StaggeredEntranceWrapper(
                            index = staggerIndex,
                            isTriggered = isEntering
                        ) {
                            SavingsTileItem(tile = tile, onClick = { onTileClick(tile.id) })
                        }
                    }
                }
                if (rowTiles.size < 5) {
                    repeat(5 - rowTiles.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun StaggeredEntranceWrapper(
    index: Int,
    isTriggered: Boolean,
    content: @Composable () -> Unit
) {
    val motionSettings = LocalMotionSettings.current
    val isReducedMotion = motionSettings.isReducedMotionEnabled
    
    var hasTriggered by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(!isTriggered) }

    LaunchedEffect(isTriggered) {
        if (isTriggered && !hasTriggered) {
            hasTriggered = true
            isVisible = false
            if (!isReducedMotion) {
                delay(150L + (index * 40L))
            }
            isVisible = true
        }
    }

    // Use a single graphicsLayer animation for both alpha and translation to reduce overhead
    val entranceProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (isReducedMotion) 300 else 400, 
            easing = EaseOutCubic
        ),
        label = "entranceProgress"
    )

    // Optimization: Avoid graphicsLayer once entrance is complete
    val modifier = if (entranceProgress < 1f) {
        Modifier.graphicsLayer {
            alpha = entranceProgress
            if (!isReducedMotion) {
                translationY = (1f - entranceProgress) * 8.dp.toPx()
            }
        }
    } else Modifier

    Box(modifier = modifier) {
        content()
    }
}
