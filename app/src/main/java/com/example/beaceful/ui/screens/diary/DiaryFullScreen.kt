package com.example.beaceful.ui.screens.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.beaceful.R
import com.example.beaceful.ui.viewmodel.DiaryViewModel

@Composable
fun DiaryFullScreen(
    diaryId: Int,
    modifier: Modifier = Modifier,
    onDiaryClick: () -> Unit = {},
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val diary = viewModel.getDiary(diaryId)
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (diary != null) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(diary.emotion.iconRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.di6_record_your_thought),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    // --- Diary Text Area ---
                    item {
                        Text(
                            "Nhật ký",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.background,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        if (diary.content != null)
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                TextField(
                                    value = diary.content,
                                    onValueChange = { },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.secondary,
                                            RoundedCornerShape(24.dp)
                                        ),
                                )
                            }
                    }
                    // --- Image/Video Upload ---
                    item {
                        Text(
                            "Hình ảnh",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.background,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                        Spacer(Modifier.height(4.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            UploadButton(label = "Từ album") {
                                // TODO: Mở picker
                            }
                            UploadButton(label = "Máy ảnh") {
                                // TODO: Mở camera
                            }
                        }
                    }

                    // --- Voice Record ---
                    item {
                        Text(
                            "Ghi âm",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.background,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                        Spacer(Modifier.height(4.dp))

                        UploadButton(label = "Nhấn để ghi âm") {
                            // TODO: Start recording
                        }
                    }

                    // --- Confirm Button ---
                    item {
                        Button(
                            onClick = { },
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
    }


}

