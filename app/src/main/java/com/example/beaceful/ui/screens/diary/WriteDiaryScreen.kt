package com.example.beaceful.ui.screens.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.beaceful.R
import com.example.beaceful.core.util.formatDiaryDate
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.ui.navigation.WriteDiary
import com.example.beaceful.ui.navigation.WriteDiaryExpand
import com.example.beaceful.ui.screens.home.EmotionItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val SAVED_DIARY_KEY = "diary_content"

@Composable
fun SelectEmotionScreen(
    dateTime: LocalDateTime = LocalDateTime.now(),
    navController: NavHostController,
) {

    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()
    ) {
        Text(stringResource(R.string.di5_greeting))
        Text(
            "${formatDiaryDate(dateTime)} tháng ${dateTime.monthValue} lúc ${
                dateTime.format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
            }"
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            contentPadding = PaddingValues(horizontal = 36.dp),
        ) {
            items(Emotions.entries) { item ->
                EmotionItem(
                    drawable = item.iconRes,
                    text = item.descriptionRes,
                    onClick = { navController.navigate(WriteDiary.createRoute(item)) },
                )
            }
        }
    }
}

@Composable
fun WriteDiaryScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    selectedEmotion: Emotions,
) {
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val diaryContentFromFullScreen = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow(SAVED_DIARY_KEY, "")
    var diaryText by remember { mutableStateOf("") }
    val latestDiaryText by diaryContentFromFullScreen?.collectAsState() ?: remember { mutableStateOf("") }

    LaunchedEffect(latestDiaryText) {
        diaryText = latestDiaryText
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row {
            IconButton(onClick = {navController.popBackStack()}) {
                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Image(painter = painterResource(selectedEmotion.iconRes), contentDescription = null, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(18.dp)))
        }
        Spacer(Modifier.height(6.dp))
        Card(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.di6_record_your_thought),
                    style = MaterialTheme.typography.titleMedium,
                )

                // --- Diary Text Area ---
                Text(
                    "Nhật ký",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                    TextField(
                        value = diaryText,
                        onValueChange = { diaryText = it },
                        placeholder = { Text("Nhập ghi chép...", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(24.dp)
                            ),
//                        colors = TextFieldDefaults.colors(
//                            containerColor = Color.Transparent,
//                            textColor = Color(0xFF1A0033)
//                        )
                    )
                    IconButton(onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(SAVED_DIARY_KEY, diaryText)
                        navController.navigate(WriteDiaryExpand.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Mở rộng",
                            tint = Color(0xFF1A0033)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // --- Image/Video Upload ---
                Text(
                    "Hình ảnh",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    UploadButton(label = "Từ album") {
                        // TODO: Mở picker
                    }
                    UploadButton(label = "Máy ảnh") {
                        // TODO: Mở camera
                    }
                }

                Spacer(Modifier.height(12.dp))

                // --- Voice Record ---
                Text(
                    "Ghi âm",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                UploadButton(label = "Nhấn để ghi âm") {
                    // TODO: Start recording
                }

                Spacer(Modifier.height(16.dp))

                // --- Confirm Button ---
                Button(
                    onClick = {  },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Xác nhận", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun UploadButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
        modifier = Modifier
            .height(48.dp)
    ) {
        Text(label, color = Color(0xFF1A0033))
    }
}

@Composable
fun FullscreenDiaryScreen(navController: NavHostController) {
    val previousBackStackEntry = navController.previousBackStackEntry
    val savedStateHandle = previousBackStackEntry?.savedStateHandle
    val existingContent = savedStateHandle?.get<String>(SAVED_DIARY_KEY) ?: ""

    var diaryText by remember { mutableStateOf(existingContent) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Ghi chép chi tiết", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = diaryText,
            onValueChange = { diaryText = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = { Text("Viết mọi điều bạn muốn...") },
            maxLines = Int.MAX_VALUE,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                savedStateHandle?.set(SAVED_DIARY_KEY, diaryText)
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Lưu")
        }
    }
}


@Preview(showBackground = true, heightDp = 400)
@Composable
fun PreviewSelectEmotionScreen() {
    SelectEmotionScreen(
        navController = rememberNavController()
    )
}

//@Preview(showBackground = true, heightDp = 800)
//@Composable
//fun PreviewWriteDiaryScreen() {
//    WriteDiaryScreen(
//    )
//}