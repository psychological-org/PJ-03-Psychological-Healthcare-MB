package com.example.beaceful.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.beaceful.R
import com.example.beaceful.domain.model.Comment
import com.example.beaceful.domain.model.DumpDataProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.beaceful.core.util.formatDateWithHour
import com.example.beaceful.domain.model.PostVisibility
import com.example.beaceful.domain.model.User
import com.example.beaceful.ui.viewmodel.PostDetailsViewModel
import java.time.LocalDateTime


@Composable
fun PostDetailsScreen(
    postId: Int,
    viewModel: PostDetailsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val post = viewModel.getPost(postId)
    val author = viewModel.getAuthor(postId)
    val initialComments = remember { viewModel.getComments(postId) }

    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.localComments.clear()
        viewModel.localComments.addAll(initialComments)
    }
    if (post == null) {
        Text("Post không tồn tại")
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (author != null) {
                item {
                    Row {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(author.avatarUrl)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.doctor_placeholder_avatar),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                author.fullName, color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row {
                                Text(
                                    formatDateWithHour(post.createdAt),
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    imageVector = when (post.visibility) {
                                        PostVisibility.PUBLIC -> Icons.Default.Public
                                        PostVisibility.PRIVATE -> Icons.Default.Lock
                                        PostVisibility.FRIEND -> Icons.Default.PeopleAlt
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = post.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                }
            }
            item {
                Text(
                    "Bình luận:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            item {
                CustomInputField(
                    placeholder = R.string.write_your_comment,
                    inputText = commentText,
                    onTextChange = { commentText = it },
                    onSent = {
                        viewModel.submitComment(post.id, 0, commentText)
                        commentText = ""
                    }
                )
            }
            if (viewModel.localComments.isEmpty()) {
                item {
                    Text("Chưa có bình luận", color = MaterialTheme.colorScheme.secondary)
                }
            } else {
                items(viewModel.localComments) { comment ->
                    CommentCard(comment = comment, commenter = viewModel.getCommenter(comment))
                }
            }
        }
    }
}

@Composable
fun CommentCard(comment: Comment, commenter: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary
        )

        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(commenter?.avatarUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.doctor_placeholder_avatar),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    commenter?.fullName ?: "Ẩn danh",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    formatDateWithHour(comment.createdAt),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    comment.content,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostDetailsPreview() {
    PostDetailsScreen(
        postId = 1
    )
}