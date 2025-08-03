package com.example.beaceful.ui.components.cards

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.beaceful.core.util.formatDiaryDate
import java.time.format.DateTimeFormatter
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.ui.navigation.DiaryDetails
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun DiaryCard(
    diary: Diary,
    onDiaryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onDiaryClick() },
        colors = CardDefaults.cardColors(
            containerColor = diary.emotion.backgroundColor
        )
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painterResource(diary.emotion.iconRes), contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .size(48.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(formatDiaryDate(diary.createdAt), color = MaterialTheme.colorScheme.onPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(diary.emotion.descriptionRes),
                        color = diary.emotion.textColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        " - ${diary.createdAt.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(diary.title, color = MaterialTheme.colorScheme.onPrimary)
                if (diary.content != null) {
                    Box {
                        val contentModifier = if (expanded) {
                            Modifier.fillMaxWidth()
                        } else {
                            Modifier
                                .fillMaxWidth()
                                .heightIn(max = 120.dp)
                        }

                        Text(
                            text = diary.content,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = if (expanded) Int.MAX_VALUE else 5,
                            overflow = TextOverflow.Ellipsis,
                            modifier = contentModifier
                        )
                    }
                }
            }
        }
    }
}

//@Composable
//fun DiaryList(
//    modifier: Modifier = Modifier,
//    diaries: List<Diary>,
//    navController: NavHostController
//) {
//    LazyColumn(
//        contentPadding = PaddingValues(horizontal = 16.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//        modifier = modifier,
//    ) {
//        items(diaries) { diary ->
//            DiaryCard(diary,
//                onDiaryClick = {
//                    navController.navigate(DiaryDetails.createRoute(diary.id))
//                })
//        }
//        item {
//            Spacer(Modifier.height(80.dp))
//        }
//    }
//}

@Composable
fun DiaryList(
    modifier: Modifier = Modifier,
    diaries: List<Diary>,
    navController: NavHostController,
    onDeleteDiary: (Diary) -> Unit
) {
    val selectedIds = remember { mutableStateListOf<Int>() }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(
            items = diaries,
            key = { diary -> diary.id }
        ) { diary ->
            val isSelected = diary.id in selectedIds

            SwipeableDiaryCard(
                diary = diary,
                onDiaryClick = {
                        navController.navigate(DiaryDetails.createRoute(diary.id))
                },
                onDelete = { onDeleteDiary(diary) },
                isSelected = isSelected,
                onLongPress = {
                    if (!isSelected) selectedIds.add(diary.id)
                }
            )
        }

        item {
            Spacer(Modifier.height(80.dp))
        }
    }

    if (selectedIds.isNotEmpty()) {
        FloatingActionButton(
            onClick = {
                // delete all selected
                selectedIds.forEach { id ->
                    val diary = diaries.find { it.id == id }
                    if (diary != null) onDeleteDiary(diary)
                }
                selectedIds.clear()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SwipeableDiaryCard(
    diary: Diary,
    onDiaryClick: () -> Unit,
    onDelete: () -> Unit,
    isSelected: Boolean,
    onLongPress: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
//                    onDelete()
                    showDialog = true
                    false
                }

                else -> false
            }
        },
        positionalThreshold = { it * 0.75f }
    )


    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            DismissBackground(dismissState)
        },
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        // Main card content
//        Box(
//            modifier = Modifier
//                .background(
//                    if (isSelected) Color.LightGray else diary.emotion.backgroundColor,
//                    RoundedCornerShape(18.dp)
//                )
//                .combinedClickable(
//                    onClick = onDiaryClick,
//                    onLongClick = onLongPress
//                )
//        ) {
        DiaryCard(diary = diary, onDiaryClick)
//        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                coroutineScope.launch { dismissState.reset() }
            },
            title = {
                Text("Xóa nhật ký?", color = MaterialTheme.colorScheme.primary)
            },
            text = {
                Text(
                    "Bạn có chắc chắn muốn xóa nhật ký này không?",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDialog = false
                        coroutineScope.launch { dismissState.dismiss(SwipeToDismissBoxValue.EndToStart) }
                        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        coroutineScope.launch { dismissState.reset() }
                    }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Color(0xFFFF1744)
        SwipeToDismissBoxValue.Settled -> Color.Transparent
        SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF1744)
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color, shape = RoundedCornerShape(18.dp))
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier)
        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
    }
}


