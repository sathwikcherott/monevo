package com.monevo.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.monevo.app.ui.CelebrationType
import com.monevo.app.ui.theme.*

@Composable
fun CelebrationDialog(
    celebration: CelebrationType,
    onDismiss: () -> Unit
) {
    val isFinal = celebration is CelebrationType.FinalGoal
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(if (isFinal) 32.dp else 28.dp),
            color = PrimaryCard,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(if (isFinal) 32.dp else 28.dp),
                    spotColor = AccentGold.copy(alpha = 0.2f)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon Section
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(if (isFinal) 80.dp else 64.dp)
                        .clip(CircleShape)
                        .background(ElevatedCard)
                ) {
                    Icon(
                        imageVector = if (isFinal) Icons.Outlined.EmojiEvents else Icons.Outlined.Grade,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(if (isFinal) 40.dp else 32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = when (celebration) {
                        is CelebrationType.MilestoneReached -> "Milestone Reached"
                        is CelebrationType.FinalGoal -> "₹50,000 Goal Completed"
                    },
                    style = if (isFinal) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Body
                Text(
                    text = when (celebration) {
                        is CelebrationType.MilestoneReached -> "You’ve officially crossed ₹${celebration.amount / 1000}K saved. Consistency is paying off."
                        is CelebrationType.FinalGoal -> "You stayed consistent and completed your full savings journey. This wasn’t luck. This was discipline. You turned small steps into a major milestone."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Action Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFinal) AccentGold else SoftGold,
                        contentColor = Background
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = if (isFinal) "Finish Journey" else "Continue",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
