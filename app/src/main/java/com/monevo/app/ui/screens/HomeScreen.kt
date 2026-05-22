package com.monevo.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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

    // Isolate Dialog visibility check
    Box {
        if (viewModel.showUnlockDialog) {
            ProgressionChoiceDialog(
                onChoiceSelected = { count -> viewModel.unlockMilestones(count) }
            )
        }
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
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            groupedTiles.forEachIndexed { index, group ->
                val isExpanded = expandedSectionIndex == index && !group.isLocked
                
                item(key = "header_${group.name}") {
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
                
                item(key = "content_${group.name}") {
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) + 
                                expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)),
                        exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessLow)) + 
                                shrinkVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
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
