package com.monevo.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.model.SavingsTile
import com.monevo.app.ui.theme.*

@Composable
fun SavingsTileItem(
    tile: SavingsTile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (tile.isCompleted) DeepProgressGreen.copy(alpha = 0.85f) else SurfaceElevated.copy(alpha = 0.4f),
        animationSpec = spring(stiffness = Spring.StiffnessLow), 
        label = "color"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (tile.isCompleted) TextPrimary else TextPrimary.copy(alpha = 0.5f),
        animationSpec = spring(stiffness = Spring.StiffnessLow), 
        label = "contentColor"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .aspectRatio(1.15f)
            .shadow(
                elevation = if (tile.isCompleted) 0.5.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MainProgressGreen.copy(alpha = 0.05f),
                ambientColor = Color.Transparent
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
