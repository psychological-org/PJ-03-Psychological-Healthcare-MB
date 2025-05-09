package com.example.beaceful.ui.components.cards

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.beaceful.core.util.formatDiaryDate
import java.time.format.DateTimeFormatter
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.DumpDataProvider
import java.time.LocalDate
import java.time.LocalDateTime

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
        Row (modifier = Modifier.padding(4.dp)) {
            Image(painterResource(diary.emotion.iconRes), contentDescription = null,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)))
            Spacer(Modifier.width(8.dp))
            Column {
                Text(formatDiaryDate(diary.createdAt),  color = MaterialTheme.colorScheme.onPrimary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(diary.emotion.descriptionRes),
                        color = diary.emotion.textColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(" - ${diary.createdAt.format(DateTimeFormatter.ofPattern("HH:mm"))}", color = MaterialTheme.colorScheme.onPrimary)
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

@Preview(showBackground = true)
@Composable
fun DiaryPreview() {
    DiaryCard(
        diary = DumpDataProvider.diaries[0],
        onDiaryClick = {},
    )
}