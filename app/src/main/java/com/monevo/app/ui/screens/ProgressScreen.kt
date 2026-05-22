package com.monevo.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.SavingsViewModel
import com.monevo.app.ui.theme.*

@Composable
fun ProgressScreen(viewModel: SavingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            letterSpacing = (-0.5).sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        CircularProgressSection(viewModel.progress)
        
        Spacer(modifier = Modifier.height(28.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnalyticsCard(
                title = "Completed",
                value = "${viewModel.tiles.count { it.isCompleted }}",
                modifier = Modifier.weight(1f)
            )
            AnalyticsCard(
                title = "Remaining",
                value = "₹${viewModel.goalAmount - viewModel.totalSaved}",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        ChartPlaceholder()
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun CircularProgressSection(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        val strokeWidth = 12.dp
        Canvas(modifier = Modifier.size(160.dp)) {
            drawArc(
                color = ElevatedCard,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = AccentGold,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            Text(
                text = "of target",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )
        }
    }
}

@Composable
fun AnalyticsCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = PrimaryCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
        }
    }
}

@Composable
fun ChartPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryCard),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                listOf(0.4f, 0.7f, 0.5f, 0.9f, 0.6f, 0.8f, 1.0f).forEach { height ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(height)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(SoftGold.copy(alpha = 0.2f))
                    )
                }
            }
            
            Text(
                text = "Weekly Savings Trend",
                modifier = Modifier.align(Alignment.TopStart),
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText
            )
        }
    }
}
