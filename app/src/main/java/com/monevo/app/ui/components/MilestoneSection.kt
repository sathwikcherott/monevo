package com.monevo.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.model.SavingsTile
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

@Composable
fun MilestoneAccordionHeader(
    name: String,
    isExpanded: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isGlowActive: Boolean = false
) {
    val motionSettings = LocalMotionSettings.current
    val haptic = LocalHapticFeedback.current
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(
            durationMillis = motionSettings.scaleDuration(400),
            easing = FastOutSlowInEasing
        ),
        label = "rotation"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isGlowActive) motionSettings.scaleValue(0.5f, 0.2f) else 0f,
        animationSpec = tween(
            durationMillis = motionSettings.scaleDuration(600), 
            easing = FastOutSlowInEasing
        ),
        label = "glowAlpha"
    )

    Surface(
        onClick = if (isLocked) ({}) else {
            {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        },
        color = PrimaryCard,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (isLocked) 0.6f else 1f)
            .shadow(
                elevation = if (isGlowActive) motionSettings.scaleDp(16.dp, 4.dp) else 0.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = AccentGold.copy(alpha = glowAlpha)
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
                        tint = SecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isExpanded) SoftGold else if (isLocked) SecondaryText else PrimaryText,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
            
            if (!isLocked) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = if (isExpanded) SoftGold else SecondaryText,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}

@Composable
fun TileGrid(
    tiles: List<SavingsTile>,
    onTileClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = remember(tiles) { tiles.chunked(5) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp) // Refined vertical rhythm
    ) {
        rows.forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Tighter horizontal rhythm
            ) {
                rowTiles.forEach { tile ->
                    Box(modifier = Modifier.weight(1f)) {
                        SavingsTileItem(tile = tile, onClick = { onTileClick(tile.id) })
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
