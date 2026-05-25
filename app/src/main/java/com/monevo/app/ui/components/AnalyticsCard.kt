package com.monevo.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.atmosphere.JourneyAtmosphere
import com.monevo.app.ui.atmosphere.getAdaptiveGold
import com.monevo.app.ui.theme.*

@Composable
fun AnalyticsCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    atmosphere: JourneyAtmosphere = JourneyAtmosphere.FreshStart
) {
    val adaptiveGold = atmosphere.getAdaptiveGold()
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) ElevatedCard else PrimaryCard
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) adaptiveGold else PrimaryText,
                letterSpacing = (-0.2).sp
            )
        }
    }
}
