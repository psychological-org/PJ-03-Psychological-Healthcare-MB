package com.example.beaceful.ui.screens.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.ui.components.cards.DiaryCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DiaryScreen() {
    val diaries = remember {
        DumpDataProvider.diaries
    }
    Box() {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
                }

                Text(LocalDate.now().format(DateTimeFormatter.ofPattern("MM, yyyy")))

                IconButton(
                    onClick = {}
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null)
                }
            }
            DiaryListScreen(
                diaries = diaries,

                )

        }
        FloatingActionButton(
            onClick = {},
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }

}

@Composable
fun DiaryListScreen(
    modifier: Modifier = Modifier,
    diaries: List<Diary>,
//    navController: NavHostController
) {

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        items(diaries) { diary ->
            DiaryCard(diary,
                onDiaryClick = {
//                    navController.navigate(SingleDoctorProfile.createRoute(diary.id))
                })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DiaryScreenPreview() {
    DiaryScreen()
}