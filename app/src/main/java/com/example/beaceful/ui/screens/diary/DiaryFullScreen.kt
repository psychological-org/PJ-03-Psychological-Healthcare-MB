package com.example.beaceful.ui.screens.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.beaceful.core.util.formatDiaryDate
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.ui.viewmodel.DiaryViewModel
import java.time.format.DateTimeFormatter

@Composable
fun DiaryFullScreen(
    diaryId: Int,
    modifier: Modifier = Modifier,
    onDiaryClick: () -> Unit = {},
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val diary = viewModel.getDiary(diaryId)
    val expanded by remember { mutableStateOf(true) }
    if (diary != null) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
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
                    Text(
                        formatDiaryDate(diary.createdAt),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
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

}
