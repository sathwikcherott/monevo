package com.monevo.app.debug

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

/**
 * [DEBUG ONLY] Visualizer that confirms haptic events are firing.
 */
@Composable
fun DebugHapticVisualizer() {
    var isVisible by remember { mutableStateOf(false) }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        DebugHapticController.hapticEvents.collectLatest {
            isVisible = true
            animProgress.snapTo(0f)
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
            )
            isVisible = false
        }
    }

    if (isVisible) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                val radius = size.minDimension / 2 * animProgress.value
                val alpha = 1f - animProgress.value
                
                drawCircle(
                    color = Color(0xFFC5A059).copy(alpha = alpha * 0.4f),
                    radius = radius,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }
    }
}
