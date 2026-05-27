package com.monevo.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.atmosphere.JourneyAtmosphere
import com.monevo.app.ui.atmosphere.getAdaptiveAccent
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

@Composable
fun CircularProgressSection(
    progress: Float,
    totalSaved: Int,
    modifier: Modifier = Modifier,
    isMomentumActive: Boolean = false,
    atmosphere: JourneyAtmosphere = JourneyAtmosphere.FreshStart
) {
    val motionSettings = LocalMotionSettings.current
    val adaptiveAccent = atmosphere.getAdaptiveAccent()
    
    val infiniteTransition = rememberInfiniteTransition(label = "ringGlow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.015f,
        targetValue = if (isMomentumActive) {
            motionSettings.scaleValue(0.06f, 0.03f) * atmosphere.glowIntensity
        } else {
            0.015f * atmosphere.glowIntensity
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = motionSettings.scaleDuration(5000), 
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        val strokeWidth = 10.dp // Elegant thin stroke for AMOLED
        val ringSize = 200.dp
        
        Canvas(modifier = Modifier.size(ringSize)) {
            // Background Track
            drawArc(
                color = DividerStroke.copy(alpha = 0.5f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            
            // Tight, almost invisible glow for AMOLED
            drawArc(
                color = adaptiveAccent.copy(alpha = pulseAlpha),
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = (strokeWidth + 6.dp).toPx(), cap = StrokeCap.Round)
            )
            
            // Active Progress Arc
            drawArc(
                color = adaptiveAccent,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = (-1).sp
            )
            
            Text(
                text = "of target".uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary.copy(alpha = 0.5f),
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = "₹%,d saved".format(totalSaved),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = adaptiveAccent,
                letterSpacing = (-0.5).sp
            )
        }
    }
}
