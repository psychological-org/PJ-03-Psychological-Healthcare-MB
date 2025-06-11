package com.example.beaceful.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.beaceful.R
import com.example.beaceful.domain.model.CollectionType
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.ui.components.cards.MiniMusicPlayer
import com.example.beaceful.ui.components.cards.MusicListScreen
import com.example.beaceful.ui.navigation.SelectEmotionDiary
import com.example.beaceful.ui.navigation.WriteDiary
import com.example.beaceful.ui.viewmodel.CollectionViewModel
import com.example.beaceful.ui.viewmodel.MusicPlayerViewModel
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel()
    val collectionViewModel: CollectionViewModel = hiltViewModel()
    val collectionsState by collectionViewModel.collections.collectAsState()
    val current by musicPlayerViewModel.currentCollection.collectAsState()
    val isPlaying by musicPlayerViewModel.isPlaying.collectAsState()
    val currentPosition by musicPlayerViewModel.currentPosition.collectAsState()
    val duration by musicPlayerViewModel.duration.collectAsState()

    LaunchedEffect(Unit) {
        collectionViewModel.getAllCollections()
    }

    Box() {
        Image(
            painter = painterResource(R.drawable.home_background_night),
            contentDescription = null
        )
        Column(
            modifier.verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(256.dp))
//        Greeting block
            Text(
                text = stringResource(R.string.ho1_greeting),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            HomeScreenEmotionsRow(
                navController = navController
            )

//        Diary block
            HomeSection(
                title = R.string.ho2_share_thought,
                onClickSeeMore = {}
            ) {
                HomeDiaryBlock(onClick = {
                    navController.navigate(SelectEmotionDiary.route)
                })
            }
//        Music
            HomeSection(
                title = R.string.ho5_music_for_u,
                onClickSeeMore = {}
            ) {
                collectionsState?.let { result ->
                    when {
                        result.isSuccess -> {
                            val collections = result.getOrNull()?.content.orEmpty()
                            val musicCollections = collections.filter { it.type == CollectionType.MUSIC }
                            MusicListScreen(musicCollections)
                        }
                        result.isFailure -> {
                            Text(
                                text = "Error loading collections: ${result.exceptionOrNull()?.message}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } ?: run {
                    Text(
                        text = "Loading collections...",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

//        Book
            HomeSection(
                title = R.string.ho6_book_for_u,
                onClickSeeMore = {}
            ) {
//                ForYouRow(list = bookList) { item ->
//                    BookItem(background = item.drawable, title = item.text)
//                }
                collectionsState?.let { result ->
                    when {
                        result.isSuccess -> {
                            val collections = result.getOrNull()?.content.orEmpty()
                            val musicCollections = collections.filter { it.type == CollectionType.OTHER }
                            MusicListScreen(musicCollections)
                        }
                        result.isFailure -> {
                            Text(
                                text = "Error loading collections: ${result.exceptionOrNull()?.message}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } ?: run {
                    Text(
                        text = "Loading collections...",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

//        Podcast
            HomeSection(
                title = R.string.ho7_podcast_for_u,
                onClickSeeMore = {}
            ) {
//                ForYouRow(list = podcastList) { item ->
//                    PodcastItem(background = item.drawable, title = item.text)
//                }
                collectionsState?.let { result ->
                    when {
                        result.isSuccess -> {
                            val collections = result.getOrNull()?.content.orEmpty()
                            val musicCollections = collections.filter { it.type == CollectionType.PODCAST }
                            MusicListScreen(musicCollections)
                        }
                        result.isFailure -> {
                            Text(
                                text = "Error loading collections: ${result.exceptionOrNull()?.message}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } ?: run {
                    Text(
                        text = "Loading collections...",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            if (current != null) {
                Spacer(Modifier.height(80.dp))
            }
        }

        if (current != null) {
            MiniMusicPlayer(
                collection = current!!,
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                onTogglePlayPause = { musicPlayerViewModel.togglePlayback() },
                onSeekTo = { pos -> musicPlayerViewModel.seekTo(pos) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            IconButton(
                onClick = { musicPlayerViewModel.stopAndClear() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x= (-16).dp, y = (-56).dp)
                    .size(28.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Composable
fun MusicItem(
    onClick: () -> Unit = {},
    @DrawableRes background: Int = R.drawable.home_music_1,
    @StringRes title: Int = R.string.relax,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Box {
            Image(
                painter = painterResource(background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.20f))
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(title),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(4.dp)
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun BookItem(
    onClick: () -> Unit = {},
    @DrawableRes background: Int,
    title: String,
) {
    Column {
        Card(
            onClick = onClick,
            modifier = Modifier
                .size(150.dp, 250.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Box {
                Image(
                    painter = painterResource(background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(4.dp)
                        )
                    }
                }

            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun PodcastItem(
    onClick: () -> Unit = {},
    @DrawableRes background: Int,
    title: String
) {
    Column {
        Card(
            onClick = onClick,
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Box {
                Image(
                    painter = painterResource(background),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(4.dp)
                        )
                    }
                }

            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )
    }
}


@Composable
fun ForYouRow(
    list: List<DrawableStringPair>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (DrawableStringPair) -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(list) { item ->
            itemContent(item)
        }
    }
}

@Composable
fun HomeDiaryBlock(
    onClick: () -> Unit,
    @DrawableRes background: Int = R.drawable.home_diary_background,
    @StringRes title: Int = R.string.ho3_share_thought2,
    @StringRes buttonText: Int = R.string.ho4_write_diary
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp)
    ) {
        Box {
            Image(
                painter = painterResource(background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.8f,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onClick,
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.8f),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(buttonText),
                    )
                }
            }
        }
    }
}

@Composable
fun HomeSection(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    onClickSeeMore: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(modifier.padding(top = 12.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier
                    .padding(horizontal = 16.dp),
            )
            TextButton(
                onClick = { onClickSeeMore() },
            ) {
                Text(text = "Xem thêm", color = MaterialTheme.colorScheme.secondary)
            }
        }
        content()
    }
}

@Composable
fun EmotionItem(
    modifier: Modifier = Modifier,
    @DrawableRes drawable: Int,
    @StringRes text: Int,
    onClick: () -> Unit = {},
) {
    Image(
        painter = painterResource(drawable),
        contentDescription = stringResource(text),
        contentScale = ContentScale.Crop,
        modifier = modifier
//            .padding(horizontal = 8.dp)
            .size(width = 50.dp, height = 75.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    )
}

@Composable
fun HomeScreenEmotionsRow(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        contentPadding = PaddingValues(horizontal = 36.dp),
    ) {
        items(Emotions.entries) { item ->
            EmotionItem(
                drawable = item.iconRes,
                text = item.descriptionRes,
                onClick = {
                    navController.navigate(
                        WriteDiary.createRoute(
                            item, datetime = LocalDateTime.now(
                                ZoneId.of("UTC+7")
                            )
                        )
                    )
                }
            )
        }
    }
}

//@Preview(widthDp = 360, heightDp = 640, backgroundColor = 0xFFFFFFFF, showBackground = true)
//@Composable
//fun SectionPreview() {
//    Column {
//        EmotionRow(selectedEmotion = R.string.confused)
//    HomeSection(title = R.string.ho2_share_thought, onClickSeeMore = {})
//    { ForYouRow(musicList) }
//    }
//    HomeDiaryBlock(onClick = {})
//}

data class DrawableStringPair(
    @DrawableRes val drawable: Int,
    @StringRes val text: Int
)

private val emotionList = listOf(
    R.drawable.diary_mood_angry to R.string.angry,
    R.drawable.diary_mood_worried to R.string.worried,
    R.drawable.diary_mood_confused to R.string.confused,
    R.drawable.diary_mood_grateful to R.string.grateful,
    R.drawable.diary_mood_inlove to R.string.in_love,
).map { DrawableStringPair(it.first, it.second) }

private val musicList = listOf(
    R.drawable.home_music_1 to R.string.relax,
    R.drawable.home_music_2 to R.string.focus,
    R.drawable.home_music_3 to R.string.peace,
    R.drawable.home_music_4 to R.string.sleep,
    R.drawable.home_music_5 to R.string.mood,
    R.drawable.home_music_6 to R.string.nostalgia,
).map { DrawableStringPair(it.first, it.second) }

private val bookList = listOf(
    R.drawable.home_book_1 to R.string.relax,
    R.drawable.home_book_2 to R.string.focus,
    R.drawable.home_book_3 to R.string.peace,
    R.drawable.home_book_4 to R.string.sleep,
).map { DrawableStringPair(it.first, it.second) }

private val podcastList = listOf(
    R.drawable.home_podcast_1 to R.string.relax,
    R.drawable.home_podcast_2 to R.string.focus,
    R.drawable.home_podcast_3 to R.string.peace,
    R.drawable.home_podcast_4 to R.string.sleep,
).map { DrawableStringPair(it.first, it.second) }