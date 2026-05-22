package com.monevo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.theme.*

@Composable
fun ConsistencySection(
    streakWeeks: Int,
    bestWeekAmount: Int,
    avgWeeklyAmount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryCard),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        ) {
            Text(
                text = "Consistency",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                ConsistencyItem(
                    label = "Current Streak",
                    value = if (streakWeeks == 1) "1 week" else "$streakWeeks weeks",
                    modifier = Modifier.weight(1f)
                )
                
                Box(modifier = Modifier.height(24.dp).width(1.dp).background(DividerColor.copy(alpha = 0.2f)))

                ConsistencyItem(
                    label = "Best Week",
                    value = "₹%,d".format(bestWeekAmount),
                    modifier = Modifier.weight(1f)
                )

                Box(modifier = Modifier.height(24.dp).width(1.dp).background(DividerColor.copy(alpha = 0.2f)))

                ConsistencyItem(
                    label = "Avg Weekly",
                    value = "₹%,d".format(avgWeeklyAmount),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ConsistencyItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = SecondaryText,
            fontWeight = FontWeight.Medium
        )
    }
}
