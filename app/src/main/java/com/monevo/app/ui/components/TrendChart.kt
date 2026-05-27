package com.monevo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.theme.*
import java.util.*

@Composable
fun TrendChart(
    heights: List<Float>,
    modifier: Modifier = Modifier
) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    
    val currentDayIndex = remember {
        val calendar = Calendar.getInstance()
        when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceBase),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Weekly Momentum",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    heights.forEachIndexed { index, height ->
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp) // Extremely thin for premium AMOLED feel
                                    .fillMaxHeight(height.coerceIn(0.01f, 1f))
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(
                                        if (index == currentDayIndex) PrimaryAccentPink 
                                        else TextMuted.copy(alpha = 0.15f + (height * 0.1f))
                                    )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DividerStroke.copy(alpha = 0.3f))
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    days.forEachIndexed { index, day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center,
                            color = if (index == currentDayIndex) TextPrimary else TextMuted.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
