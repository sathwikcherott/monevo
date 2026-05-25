package com.monevo.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.atmosphere.JourneyAtmosphere
import com.monevo.app.ui.atmosphere.getAdaptiveGold
import com.monevo.app.ui.theme.*

@Composable
fun MilestonesProgress(
    totalSaved: Int,
    goalAmount: Int,
    modifier: Modifier = Modifier,
    atmosphere: JourneyAtmosphere = JourneyAtmosphere.FreshStart
) {
    val adaptiveGold = atmosphere.getAdaptiveGold()
    val milestones = remember(goalAmount) {
        val step = goalAmount / 5
        List(6) { index ->
            val amount = index * step
            val label = if (amount == 0) "0" else "₹${amount / 1000}K"
            MilestoneData(amount, label)
        }
    }

    val targetIndex = milestones.indexOfFirst { totalSaved < it.amount }.let {
        if (it == -1) milestones.size - 1 else it
    }
    
    val currentTarget = milestones[targetIndex]
    val lowerBound = if (targetIndex > 0) milestones[targetIndex - 1].amount else 0
    val savedInThisMilestone = (totalSaved - lowerBound).coerceAtLeast(0)
    val milestoneTotal = currentTarget.amount - lowerBound
    val milestonePercentage = if (milestoneTotal > 0) (savedInThisMilestone.toFloat() / milestoneTotal * 100).toInt() else 0

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryCard),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Milestone Path",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )
                
                if (totalSaved < milestones.last().amount) {
                    Text(
                        text = "₹%,d left to ${currentTarget.label}".format(currentTarget.amount - totalSaved),
                        style = MaterialTheme.typography.labelSmall,
                        color = adaptiveGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(DividerColor.copy(alpha = 0.2f)))

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val totalSegments = milestones.size - 1
                    val segmentWidth = maxWidth / totalSegments
                    val progressInCurrent = if (milestoneTotal > 0) savedInThisMilestone.toFloat() / milestoneTotal else 0f
                    val activeWidth = (segmentWidth * (targetIndex - 1).coerceAtLeast(0)) + (segmentWidth * progressInCurrent)
                    
                    Box(modifier = Modifier.width(activeWidth).height(2.dp).background(adaptiveGold))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    milestones.forEachIndexed { index, milestone ->
                        val isReached = totalSaved >= milestone.amount
                        val isTarget = index == targetIndex && !isReached
                        
                        MilestoneNode(
                            label = milestone.label,
                            isReached = isReached,
                            isTarget = isTarget,
                            accentColor = adaptiveGold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "$milestonePercentage% of this milestone",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryText,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun MilestoneNode(
    label: String, 
    isReached: Boolean, 
    isTarget: Boolean,
    accentColor: Color = AccentGold
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (isTarget) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .border(2.dp, accentColor, CircleShape)
                    .background(Background, CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isReached) accentColor else SecondaryText.copy(alpha = 0.3f))
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            fontWeight = if (isTarget) FontWeight.Bold else FontWeight.Medium,
            color = if (isTarget) PrimaryText else SecondaryText.copy(alpha = if (isReached) 0.8f else 0.3f)
        )
    }
}

private data class MilestoneData(val amount: Int, val label: String)
