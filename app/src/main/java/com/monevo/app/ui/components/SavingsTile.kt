package com.monevo.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.model.SavingsTile
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

@Composable
fun SavingsTileItem(
    tile: SavingsTile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val motionSettings = LocalMotionSettings.current
    val isReducedMotion = motionSettings.isReducedMotionEnabled
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) (if (isReducedMotion) 1f else 0.98f) else 1f,
        animationSpec = if (isReducedMotion) snap() else spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (tile.isCompleted) DeepProgressGreen.copy(alpha = 0.85f) else SurfaceElevated.copy(alpha = 0.4f),
        animationSpec = if (isReducedMotion) tween(200, easing = LinearEasing) else spring(stiffness = Spring.StiffnessMedium), 
        label = "color"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (tile.isCompleted) TextPrimary else TextPrimary.copy(alpha = 0.5f),
        animationSpec = if (isReducedMotion) tween(200, easing = LinearEasing) else spring(stiffness = Spring.StiffnessMedium),
        label = "contentColor"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .aspectRatio(1.15f)
            // Flattened visual layers: replaced shadow with a subtle border for 120Hz performance
            .border(
                border = BorderStroke(
                    width = 0.5.dp, 
                    color = if (tile.isCompleted) DeepProgressGreen.copy(alpha = 0.1f) else Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick() 
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "₹${tile.amount}",
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        )
    }
}
