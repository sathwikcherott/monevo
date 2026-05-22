package com.monevo.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.model.SavingsTile
import com.monevo.app.ui.SavingsViewModel
import com.monevo.app.ui.theme.*

@Composable
fun HomeScreen(viewModel: SavingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Monevo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            letterSpacing = (-0.5).sp
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        TotalSavedCard(
            total = viewModel.totalSaved,
            progress = viewModel.progress,
            completedCount = viewModel.tiles.count { it.isCompleted },
            totalCount = viewModel.tiles.size
        )
        
        Spacer(modifier = Modifier.height(28.dp))
        
        Text(
            text = "Savings Tiles",
            style = MaterialTheme.typography.labelLarge,
            color = SecondaryText,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(viewModel.tiles, key = { it.id }) { tile ->
                SavingsTileItem(tile) {
                    viewModel.toggleTile(tile.id)
                }
            }
        }
    }
}

@Composable
fun TotalSavedCard(total: Int, progress: Float, completedCount: Int, totalCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryCard),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total Saved",
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryText
                    )
                    Text(
                        text = "₹$total",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }
                
                Surface(
                    color = ElevatedCard,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$completedCount/$totalCount",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = AccentGold,
                trackColor = ElevatedCard,
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(progress * 100).toInt()}% of goal",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentGold
                )
                Text(
                    text = "Goal: ₹50,000",
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
            }
        }
    }
}

@Composable
fun SavingsTileItem(tile: SavingsTile, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (tile.isCompleted) SuccessGreen else PrimaryCard,
        animationSpec = spring(), label = "color"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (tile.isCompleted) Background else PrimaryText,
        animationSpec = spring(), label = "contentColor"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .aspectRatio(1.1f)
            .shadow(
                elevation = if (tile.isCompleted) 8.dp else 0.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = SuccessGreen.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "₹${tile.amount}",
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        )
    }
}
