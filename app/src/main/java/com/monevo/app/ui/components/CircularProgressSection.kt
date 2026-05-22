package com.monevo.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.theme.*

@Composable
fun CircularProgressSection(
    progress: Float,
    totalSaved: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        val strokeWidth = 16.dp
        val ringSize = 200.dp
        
        Canvas(modifier = Modifier.size(ringSize)) {
            // Background Track - Using DividerColor for clean, premium visibility
            drawArc(
                color = DividerColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            
            // Outer Ambient Glow - Wide and extremely soft
            drawArc(
                color = AccentGold.copy(alpha = 0.05f),
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = (strokeWidth + 12.dp).toPx(), cap = StrokeCap.Round)
            )

            // Inner Soft Glow - More focused
            drawArc(
                color = AccentGold.copy(alpha = 0.12f),
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = (strokeWidth + 4.dp).toPx(), cap = StrokeCap.Round)
            )
            
            // Active Progress Arc
            drawArc(
                color = AccentGold,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
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
            
            Spacer(modifier = Modifier.height(4.dp)) // Tightened rhythm
            
            Text(
                text = "₹%,d saved".format(totalSaved),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = SoftGold
            )
        }
    }
}
