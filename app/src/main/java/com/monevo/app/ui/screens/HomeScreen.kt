package com.monevo.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.monevo.app.model.SavingsTile
import com.monevo.app.ui.SavingsViewModel
import com.monevo.app.ui.theme.*

@Composable
fun HomeScreen(viewModel: SavingsViewModel) {
    // Keep groupedTiles read isolated
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
        
        // Pass providers to avoid parent recomposition
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
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 16.dp)
                        ) {
                            TileGrid(group.tiles, viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TileGrid(tiles: List<SavingsTile>, viewModel: SavingsViewModel) {
    // Memoize the chunking operation
    val rows = remember(tiles) { tiles.chunked(5) }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEachIndexed { rowIndex, rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTiles.forEach { tile ->
                    Box(modifier = Modifier.weight(1f)) {
                        SavingsTileItem(tile) {
                            viewModel.toggleTile(tile.id)
                        }
                    }
                }
                if (rowTiles.size < 5) {
                    repeat(5 - rowTiles.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun TotalSavedCard(
    totalProvider: () -> Int,
    progressProvider: () -> Float,
    completedCountProvider: () -> Int,
    totalCountProvider: () -> Int,
    goalProvider: () -> Int
) {
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
                        text = "₹${totalProvider()}",
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
                        text = "${completedCountProvider()}/${totalCountProvider()}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val progressValue = progressProvider()
            LinearProgressIndicator(
                progress = { progressValue.coerceIn(0f, 1f) },
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
                    text = "${(progressValue * 100).toInt()}% of goal",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentGold
                )
                Text(
                    text = "Goal: ₹%,d".format(goalProvider()),
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
        targetValue = if (tile.isCompleted) SuccessGreen else ElevatedCard,
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

@Composable
fun ProgressionChoiceDialog(onChoiceSelected: (Int) -> Unit) {
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
                    onClick = { onChoiceSelected(1) }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ChoiceButton(
                    title = "Expand Progress",
                    subtitle = "Unlock 2 next milestones",
                    isPrimary = true,
                    onClick = { onChoiceSelected(2) }
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

@Composable
fun MilestoneAccordionHeader(
    name: String,
    isExpanded: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    Surface(
        onClick = if (isLocked) ({}) else onClick,
        color = PrimaryCard,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isLocked) 0.6f else 1f)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = SecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isExpanded) SoftGold else if (isLocked) SecondaryText else PrimaryText,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
            
            if (!isLocked) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = if (isExpanded) SoftGold else SecondaryText,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}
