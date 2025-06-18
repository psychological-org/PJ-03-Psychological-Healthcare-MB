package com.example.beaceful.ui.screens.home

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.beaceful.R
import com.example.beaceful.domain.model.CollectionType
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.ui.components.cards.MiniMusicPlayer
import com.example.beaceful.ui.components.cards.MusicListScreen
import com.example.beaceful.ui.navigation.NotificationRoute
import com.example.beaceful.ui.navigation.SelectEmotionDiary
import com.example.beaceful.ui.navigation.WriteDiary
import com.example.beaceful.ui.screens.diary.PieChartData
import com.example.beaceful.ui.viewmodel.CollectionViewModel
import com.example.beaceful.ui.viewmodel.MusicPlayerViewModel
import com.example.beaceful.ui.viewmodel.RecommendationViewModel
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId


@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel()
    val collectionViewModel: CollectionViewModel = hiltViewModel()
    val collectionsState by collectionViewModel.collections.collectAsState()
    val current by musicPlayerViewModel.currentCollection.collectAsState()
    val isPlaying by musicPlayerViewModel.isPlaying.collectAsState()
    val currentPosition by musicPlayerViewModel.currentPosition.collectAsState()
    val duration by musicPlayerViewModel.duration.collectAsState()

    val currentHour = remember { LocalTime.now().hour }
    val timePhase = when (currentHour) {
        in 5..10 -> 1  //sang
        in 11..13 -> 2 //trua
        in 14..17 -> 3 //chieu
        in 18..21 -> 4 // toi
        else -> 0 //dem
    }

    LaunchedEffect(Unit) {
        collectionViewModel.getAllCollections()

    }

    Box() {
        Image(
            painter = when (timePhase) {
                1 -> painterResource(R.drawable.home_background_sunrise)
                2 -> painterResource(R.drawable.home_background_afternoon)
                3 -> painterResource(R.drawable.home_background_sunset)
                4 -> painterResource(R.drawable.home_background_night)
                else -> painterResource(R.drawable.home_background_night)
            },
            contentDescription = null
        )
        Column(
            modifier.verticalScroll(rememberScrollState()),
        ) {
Row (Modifier
    .fillMaxWidth()
    .padding(16.dp), horizontalArrangement = Arrangement.End) {
    IconButton(
        onClick = { navController.navigate(NotificationRoute.route) },
        modifier = Modifier
            .size(28.dp),
    ) {
        Icon(
            Icons.Default.Notifications,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}
            Spacer(Modifier.height(100.dp))
//        Greeting block
            OutlinedTextByTime(
                text = when (timePhase) {
                    1 -> "Chào buổi sáng,"
                    2 -> "Chào buổi trưa,"
                    3 -> "Chào buổi chiều,"
                    4 -> "Chào buổi tối,"
                    else -> "Xin chào,"
                },
                modifier = Modifier.padding(horizontal = 24.dp),
                timePhase = timePhase,
            )
            OutlinedTextByTime(
                text = stringResource(R.string.ho1_greeting),
                modifier = Modifier.padding(horizontal = 24.dp), timePhase = timePhase
            )
            Spacer(Modifier.height(16.dp))

            HomeScreenEmotionsRow(
                navController = navController
            )
            Spacer(Modifier.height(36.dp))

//        Diary block
//            Column() {
//                HomeDiaryBlock(onClick = {
//                    navController.navigate(SelectEmotionDiary.route)
//                })
//            }
//            Spacer(Modifier.height(36.dp))

            RecommendationCard(
                recommendationText = "\"Trong bóng tối, hãy là ngọn nến của chính mình.\"\n\nTôi hiểu rằng bạn đang trải qua giai đoạn khó khăn. Những cảm xúc tiêu cực có thể rất nặng nề, nhưng bạn không đơn độc. Hãy cho phép bản thân được buồn, được mệt mỏi, và nhớ rằng đây chỉ là một phần của cuộc sống. Hãy thử bài tập hít thở sâu, tập trung vào hiện tại để xoa dịu tâm trí.\n\nTrong công việc hoặc học tập, hãy bắt đầu với những mục tiêu nhỏ, dễ đạt được. Chia nhỏ nhiệm vụ lớn thành những bước nhỏ hơn để giảm bớt áp lực. Đừng ngần ngại tìm kiếm sự giúp đỡ từ đồng nghiệp hoặc bạn bè khi cần.\n\nHãy kết nối với những người bạn tin tưởng. Chia sẻ cảm xúc của bạn một cách trung thực và mở lòng đón nhận sự hỗ trợ từ họ. Đôi khi, chỉ cần biết rằng có ai đó lắng nghe và quan tâm cũng có thể tạo ra sự khác biệt lớn.\n\nHôm nay, bạn có thể thử viết nhật ký để giải tỏa cảm xúc hoặc đi bộ nhẹ nhàng trong 15 phút để thư giãn đầu óc.\n\nNếu bạn cảm thấy những cảm xúc này quá sức chịu đựng, hãy tìm đến chuyên gia tâm lý để được hỗ trợ. Ứng dụng này có hỗ trợ đặt lịch khám nếu bạn cần, hoặc bạn có thể liên hệ đường dây nóng 179 hoặc 1900 9254. Bạn mạnh mẽ hơn bạn nghĩ đấy!\n",
                background = R.drawable.home_diary_background
            )

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
                ForYouRow(list = bookList) { item ->
                    BookItem(background = item.drawable, title = item.text)
                }
//                collectionsState?.let { result ->
//                    when {
//                        result.isSuccess -> {
//                            val collections = result.getOrNull()?.content.orEmpty()
//                            val musicCollections = collections.filter { it.type == CollectionType.OTHER }
//                            MusicListScreen(musicCollections)
//                        }
//                        result.isFailure -> {
//                            Text(
//                                text = "Error loading collections: ${result.exceptionOrNull()?.message}",
//                                color = MaterialTheme.colorScheme.error,
//                                modifier = Modifier.padding(16.dp)
//                            )
//                        }
//                    }
//                } ?: run {
//                    Text(
//                        text = "Loading collections...",
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
            }

//        Podcast
            HomeSection(
                title = R.string.ho7_podcast_for_u,
                onClickSeeMore = {}
            ) {
                ForYouRow(list = podcastList) { item ->
                    PodcastItem(background = item.drawable, title = item.text)
                }
//                collectionsState?.let { result ->
//                    when {
//                        result.isSuccess -> {
//                            val collections = result.getOrNull()?.content.orEmpty()
//                            val musicCollections = collections.filter { it.type == CollectionType.PODCAST }
//                            MusicListScreen(musicCollections)
//                        }
//                        result.isFailure -> {
//                            Text(
//                                text = "Error loading collections: ${result.exceptionOrNull()?.message}",
//                                color = MaterialTheme.colorScheme.error,
//                                modifier = Modifier.padding(16.dp)
//                            )
//                        }
//                    }
//                } ?: run {
//                    Text(
//                        text = "Loading collections...",
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
            }
            if (current != null) {
                Spacer(Modifier.height(80.dp))
            }
            else {
                Spacer(Modifier.height(8.dp))
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
                    .offset(x = (-16).dp, y = (-56).dp)
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
fun BookItem(
    onClick: () -> Unit = {},
    @DrawableRes background: Int,
    @StringRes title: Int,
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
            text = stringResource(title),
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
    @StringRes title: Int
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
            text = stringResource(title),
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
    @StringRes buttonText: Int = R.string.ho4_write_diary,
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RecommendationCard(
    recommendationText: String,
    background: Int,
//    onClick: () -> Unit
) {
    val paragraphs = remember(recommendationText) {
        recommendationText
            .split(Regex("\\n+"))
            .map { "\u00A0\u00A0\u00A0\u00A0" + it.trimStart('\n', ' ') }
            .filter { it.isNotBlank() }
    }
    var currentIndex by remember { mutableStateOf(0) }
    var isPaused by remember { mutableStateOf(false) }

    // Tự động chuyển slide sau 5 giây nếu không bị tạm dừng
    LaunchedEffect(currentIndex, isPaused) {
        if (!isPaused && paragraphs.size > 1) {
            delay(5000)
            currentIndex = (currentIndex + 1) % paragraphs.size
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    }
                )
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Hình nền
            Image(
                painter = painterResource(background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.8f,
                modifier = Modifier.fillMaxSize(),
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .padding(bottom = 24.dp)
                ,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(
                    start = 40.dp,
                    end = 40.dp,
                    top = 16.dp,
                    bottom = 0.dp
                )
            ) {
                // Animated chuyển nội dung
                item{
                    AnimatedContent(
                        targetState = paragraphs[currentIndex],
                        transitionSpec = {
                            (slideInHorizontally { fullWidth -> fullWidth } + fadeIn()).togetherWith(
                                slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut())
                        }
                    ) { paragraph ->
                        Text(
                            text = paragraph,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }


            }

            // Mũi tên (ẩn nếu chỉ có 1 đoạn)
            if (paragraphs.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .offset(y = 50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        currentIndex =
                            if (currentIndex > 0) currentIndex - 1 else paragraphs.size - 1
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", tint =MaterialTheme.colorScheme.onPrimary)
                    }

                    IconButton(onClick = {
                        currentIndex = (currentIndex + 1) % paragraphs.size
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }

            // Dấu chấm chỉ vị trí hiện tại (ẩn nếu chỉ có 1 đoạn)
            if (paragraphs.size > 1) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-4).dp)
                ) {
                    paragraphs.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (index == currentIndex) 12.dp else 8.dp)
                                .background(
                                    color = if (index == currentIndex) Color.White else Color.LightGray,
                                    shape = CircleShape
                                )
                        )
                    }
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
                    navController.navigate(SelectEmotionDiary.route)
//                    navController.navigate(
//                        WriteDiary.createRoute(
//                            item, datetime = LocalDateTime.now(
//                                ZoneId.of("UTC+7")
//                            )
//                        )
//                    )
                }
            )
        }
    }
}

data class DrawableStringPair(
    @DrawableRes val drawable: Int,
    @StringRes val text: Int
)

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

@Composable
fun OutlinedTextByTime(
    timePhase: Int,
    text: String,
    outlineColor: Color = if (timePhase == 1 || timePhase == 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
    textColor: Color = if (timePhase == 1 || timePhase == 3) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
    strokeWidth: Int = 1,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineLarge,
    fontWeight: FontWeight = FontWeight.Medium,
) {
    Box {
        if (timePhase == 2){
            for (dx in -strokeWidth..strokeWidth) {
                for (dy in -strokeWidth..strokeWidth) {
                    if (dx != 0 || dy != 0) {
                        Text(
                            text = text,
                            style = style,
                            color = outlineColor,
                            modifier = modifier.offset(x = dx.dp, y = dy.dp),
                            fontWeight = fontWeight
                        )
                    }
                }
            }
        }

        Text(
            text = text,
            modifier = modifier,
            color = textColor,
            style = style,
            fontWeight = fontWeight,
        )

    }
}
