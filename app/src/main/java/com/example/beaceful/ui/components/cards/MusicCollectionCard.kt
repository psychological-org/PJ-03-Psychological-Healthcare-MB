package com.example.beaceful.ui.components.cards

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.beaceful.R
import com.example.beaceful.domain.model.Collection
import com.example.beaceful.ui.viewmodel.MusicPlayerViewModel

@Composable
fun MusicCollectionCard(
    collection: Collection,
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    val randomImage = remember(collection.id) {
        val images = listOf(
            R.drawable.home_music_1,
            R.drawable.home_music_2,
            R.drawable.home_music_3,
            R.drawable.home_music_4,
            R.drawable.home_music_5,
            R.drawable.home_music_6
        )
        images[collection.id % images.size]
    }

    Card(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onPlayPause() }
    ) {
        Box {
            Image(
                painter = painterResource(randomImage),
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
                            text = collection.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
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
fun MusicListScreen(
    collections: List<Collection>,
    viewModel: MusicPlayerViewModel = hiltViewModel()
) {
    val current by viewModel.currentCollection.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(collections) { collection ->
            MusicCollectionCard(
                collection = collection,
                isPlaying = current?.id == collection.id && isPlaying,
                onPlayPause = {
                    if (current?.id == collection.id && isPlaying) {
                        viewModel.togglePlayback()
                    } else {
                        viewModel.playCollection(collection)
                    }
                }
            )
        }
    }
}

@Composable
fun MiniMusicPlayer(
    collection: Collection,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val randomImage = remember(collection.id) {
        val images = listOf(
            R.drawable.home_music_1,
            R.drawable.home_music_2,
            R.drawable.home_music_3,
            R.drawable.home_music_4,
            R.drawable.home_music_5,
            R.drawable.home_music_6
        )
        images[collection.id % images.size]
    }

        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                )
                .clickable { onTogglePlayPause() },
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Column() {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    Box() {
                        Image(
                            painter = painterResource(randomImage),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(18.dp))
                        )
                        IconButton(
                            onClick = onTogglePlayPause, modifier = Modifier
                                .size(56.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Box(
                    ) {
                        Text(
                            formatTime(currentPosition),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.align(Alignment.CenterStart).offset(y = (-4).dp)
                        )
                        Slider(
                            value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                            onValueChange = { ratio ->
                                val newPos = (ratio * duration).toLong()
                                onSeekTo(newPos)
                            },
                            modifier = Modifier
                                .padding(horizontal = 36.dp)
                                .align(Alignment.Center).offset(y = (-4).dp)
                        )
                        Text(
                            formatTime(duration),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.align(Alignment.CenterEnd).offset(y = (-4).dp)
                        )
                        Text(
                            text = collection.name,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1, color = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.align(Alignment.BottomCenter).offset(y = (4).dp)

                        )
                    }


                }
                Column {

                }
            }
        }

}

fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

