package com.monevo.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.monevo.app.ui.theme.*

@Composable
fun ProgressionChoiceDialog(onChoiceSelected: (Int) -> Unit) {
    val haptic = LocalHapticFeedback.current
    
    Dialog(onDismissRequest = { }) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = PrimaryCard,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Milestone Complete",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "You’ve completed this milestone.\nHow would you like to continue?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                ChoiceButton(
                    title = "Focus Mode",
                    subtitle = "Unlock 1 next milestone",
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onChoiceSelected(1) 
                    }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ChoiceButton(
                    title = "Expand Progress",
                    subtitle = "Unlock 2 next milestones",
                    isPrimary = true,
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onChoiceSelected(2) 
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ChoiceButton(
    title: String,
    subtitle: String,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isPrimary) SoftGold else ElevatedCard,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPrimary) Background else PrimaryText
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (isPrimary) Background.copy(alpha = 0.7f) else SecondaryText
            )
        }
    }
}
