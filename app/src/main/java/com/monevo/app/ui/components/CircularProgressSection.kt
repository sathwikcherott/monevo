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
import com.monevo.app.ui.motion.LocalMotionSettings
import com.monevo.app.ui.theme.*

@Composable
fun CircularProgressSection(
    progress: Float,
    totalSaved: Int,
    modifier: Modifier = Modifier,
    isMomentumActive: Boolean = false,
) {
    val motionSettings = LocalMotionSettings.current
    val infiniteTransition = rememberInfiniteTransition(label = "ringGlow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.04f,
        targetValue = if (isMomentumActive) motionSettings.scaleValue(0.12f, 0.06f) else 0.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = motionSettings.scaleDuration(2500), 
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            // Reduced top padding to move the ring slightly upward
            .padding(top = 12.dp, bottom = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        val strokeWidth = 16.dp
        val ringSize = 200.dp
        
        Canvas(modifier = Modifier.size(ringSize)) {
            // Background Track - Consistent baseline
            drawArc(
                color = DividerColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            
            // Highly diffused ambient glow (blended into active arc)
            drawArc(
                color = AccentGold.copy(alpha = pulseAlpha),
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = (strokeWidth + 16.dp).toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = AccentGold.copy(alpha = pulseAlpha * 2f),
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = (strokeWidth + 8.dp).toPx(), cap = StrokeCap.Round)
            )
            
            // Active Progress Arc - Sized precisely to match track visually
            drawArc(
                color = AccentGold,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = (strokeWidth - 0.5.dp).toPx(), cap = StrokeCap.Round)
            )
        }
        
        // Magnetized Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                letterSpacing = (-1).sp
            )
            
            Text(
                text = "of target",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText,
                letterSpacing = 0.5.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "₹%,d saved".format(totalSaved),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = SoftGold
            )
        }
    }
}
