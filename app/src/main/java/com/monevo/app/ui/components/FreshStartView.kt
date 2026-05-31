package com.monevo.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.atmosphere.JourneyStateProvider
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

@Composable
fun FreshStartView(
    onBeginSaving: () -> Unit,
    modifier: Modifier = Modifier
) {
    val motionSettings = LocalMotionSettings.current
    val isReducedMotion = motionSettings.isReducedMotionEnabled
    val journeyState = JourneyStateProvider.FreshStart
    
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val glowAlpha by if (isReducedMotion) {
        remember { mutableStateOf(0f) }
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 0.15f, // Very restrained for AMOLED
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glowAlpha"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                drawArc(
                    color = DividerStroke.copy(alpha = 0.3f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Surface(
                shape = CircleShape,
                color = SurfaceElevated,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = SoftAccentPink.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Text(
            text = journeyState.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = journeyState.message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 2.dp)
                    .alpha(glowAlpha),
                color = SoftAccentPink,
                shape = RoundedCornerShape(16.dp)
            ) {}

            Button(
                onClick = onBeginSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SoftAccentPink,
                    contentColor = PrimaryBackground
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Begin Saving",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
