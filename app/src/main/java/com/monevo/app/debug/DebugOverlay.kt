package com.monevo.app.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.SavingsViewModel

/**
 * [DEBUG ONLY] Floating UI overlay for milestone testing.
 * This should be removed before production.
 */
@Composable
fun DebugMilestoneOverlay(viewModel: SavingsViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp, end = 16.dp), // Positioned above bottom nav
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "DEBUG TOOLS",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { DebugMilestoneController.reverseMilestone(viewModel) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("- Progress", color = Color.White, fontSize = 12.sp)
                }
                
                Button(
                    onClick = { DebugMilestoneController.advanceMilestone(viewModel) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC5A059)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("+ Progress", color = Color.Black, fontSize = 12.sp)
                }
            }
        }
    }
}
