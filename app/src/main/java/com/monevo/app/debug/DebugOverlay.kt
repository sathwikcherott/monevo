package com.monevo.app.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.SavingsViewModel
import com.monevo.app.BuildConfig
import kotlin.math.roundToInt

private const val SHOW_DEBUG_MENU = false

/**
 * [DEBUG ONLY] Floating, draggable, and minimizable debug panel.
 * This should be removed before production.
 */
@Composable
fun DebugMilestoneOverlay(viewModel: SavingsViewModel) {
    if (!SHOW_DEBUG_MENU) return
    if (!BuildConfig.DEBUG) return

    var isExpanded by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    
    // Floating position state
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    // Confirmation Dialog for Reset
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset App Data?") },
            text = { Text("This will clear all savings, milestones, and onboarding progress.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        DebugResetController.resetAppData(viewModel)
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // [DEBUG] Visual Confirmation for Haptics
        DebugHapticVisualizer()

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .align(Alignment.CenterEnd)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        ) {
            if (isExpanded) {
                // Expanded Panel
                Column(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                        .padding(12.dp)
                        .width(IntrinsicSize.Min),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header with Drag Handle and Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Drag",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "DEBUG",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        IconButton(
                            onClick = { isExpanded = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Minimize",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Progress Controls
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { DebugMilestoneController.reverseMilestone(viewModel) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("- Prog", color = Color.White, fontSize = 11.sp)
                        }
                        
                        Button(
                            onClick = { DebugMilestoneController.advanceMilestone(viewModel) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC5A059)),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+ Prog", color = Color.Black, fontSize = 11.sp)
                        }
                    }

                    // Near Complete Control
                    Button(
                        onClick = { DebugMilestoneController.nearComplete(viewModel) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray.copy(alpha = 0.8f)),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier.fillMaxWidth().height(34.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Near Complete", color = Color.White, fontSize = 11.sp)
                    }

                    // Haptic Viz Toggle
                    Button(
                        onClick = { 
                            DebugHapticController.isVizModeEnabled = !DebugHapticController.isVizModeEnabled
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (DebugHapticController.isVizModeEnabled) Color(0xFFC5A059) else Color.Gray.copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier.fillMaxWidth().height(34.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (DebugHapticController.isVizModeEnabled) "Haptic Viz: ON" else "Haptic Viz: OFF",
                            color = if (DebugHapticController.isVizModeEnabled) Color.Black else Color.White,
                            fontSize = 11.sp
                        )
                    }

                    // Reset Control
                    Button(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.6f)),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier.fillMaxWidth().height(34.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Data", color = Color.White, fontSize = 11.sp)
                    }
                }
            } else {
                // Minimized Pill
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.8f))
                        .clickable { isExpanded = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = "Debug Menu",
                        tint = Color(0xFFC5A059),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
