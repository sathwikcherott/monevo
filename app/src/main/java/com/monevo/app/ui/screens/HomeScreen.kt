package com.monevo.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monevo.app.ui.SavingsViewModel
import com.monevo.app.ui.components.*
import com.monevo.app.ui.theme.PrimaryText

@Composable
fun HomeScreen(viewModel: SavingsViewModel) {
    val groupedTiles = viewModel.groupedTiles
    var expandedSectionIndex by remember { mutableIntStateOf(0) }

    // Celebration Dialog
    viewModel.activeCelebration?.let { celebration ->
        CelebrationDialog(
            celebration = celebration,
            onDismiss = { viewModel.dismissCelebration() }
        )
    }

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
            totalProvider = { viewModel.totalSaved },
            progressProvider = { viewModel.progress },
            completedCountProvider = { viewModel.tiles.count { it.isCompleted } },
            totalCountProvider = { viewModel.tiles.size },
            goalProvider = { viewModel.goalAmount }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedTiles.forEachIndexed { index, group ->
                val isExpanded = expandedSectionIndex == index && !group.isLocked
                
                item(key = "header_${group.id}") {
                    MilestoneAccordionHeader(
                        name = group.name,
                        isExpanded = isExpanded,
                        isLocked = group.isLocked,
                        onClick = {
                            if (!group.isLocked) {
                                expandedSectionIndex = if (isExpanded) -1 else index
                            }
                        }
                    )
                }
                
                item(key = "content_${group.id}") {
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) + 
                                expandVertically(animationSpec = tween(400, easing = FastOutSlowInEasing)),
                        exit = fadeOut(animationSpec = tween(400, easing = FastOutSlowInEasing)) + 
                                shrinkVertically(animationSpec = tween(400, easing = FastOutSlowInEasing))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 16.dp)
                        ) {
                            TileGrid(
                                tiles = group.tiles, 
                                onTileClick = { viewModel.toggleTile(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}
