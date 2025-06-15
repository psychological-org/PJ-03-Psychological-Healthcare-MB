package com.example.beaceful.ui.components.cards

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.beaceful.R
import com.example.beaceful.core.util.formatDateWithHour
import com.example.beaceful.domain.model.Comment
import com.example.beaceful.domain.model.Community
import com.example.beaceful.domain.model.Post
import com.example.beaceful.domain.model.PostVisibility
import com.example.beaceful.domain.model.User

@Composable
fun PostCard(
    post: Post,
    user: User,
    commentCount: Int,
    isLiked: Boolean,
    onPostClick: () -> Unit,
    onToggleLike: () -> Unit,
    onDeletePost: () -> Unit,
    onEditPost: () -> Unit = {},
    modifier: Modifier = Modifier,
    community: Community? = null,
    userId: String,
    comments: List<Comment> = emptyList(),
    commentText: String = "",
    onCommentTextChange: (String) -> Unit = {},
    onLoadComments: () -> Unit = {},
    onSubmitComment: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var expandedMenu by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf<Int?>(null) }
    val isEditable = post.posterId == userId // Kiểm tra quyền chỉnh sửa/xóa

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPostClick() },
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary
        )

        Row(
            modifier = Modifier.padding(12.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.avatarUrl)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            user.fullName,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (community != null) {
                            Text(
                                text = "đăng trong ${community.name}",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Box {
                        IconButton(onClick = { expandedMenu = !expandedMenu }) {
                            Icon(
                                Icons.Default.MoreHoriz,
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            if (isEditable) {
                                DropdownMenuItem(
                                    text = { Text("Chỉnh sửa", color = MaterialTheme.colorScheme.primary) },
                                    onClick = onEditPost
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        selectedPostId = post.id
                                        showDialog = true
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.hide), color = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        selectedPostId = post.id
                                        showDialog = true
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Báo cáo", color = MaterialTheme.colorScheme.primary) },
                                onClick = { }
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                Box {
                    val contentModifier = if (expanded) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 120.dp)
                    }
                    Text(
                        text = post.content,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (expanded) Int.MAX_VALUE else 5,
                        overflow = TextOverflow.Ellipsis,
                        modifier = contentModifier
                    )
                }
                Spacer(Modifier.height(8.dp))
                if (!expanded && post.content.length > 200) {
                    Text(
                        text = "...xem thêm",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { expanded = true }
                    )
                }
                Spacer(Modifier.height(8.dp))
                // Hành động
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onToggleLike) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Text(
                        text = post.reactCount.toString(),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(onClick = onLoadComments) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Text(
                        text = commentCount.toString(),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                // Bình luận
//                Spacer(Modifier.height(8.dp))
//                comments.forEach { comment ->
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(start = 16.dp, top = 4.dp),
//                        verticalAlignment = Alignment.Top
//                    ) {
//                        Text(
//                            text = "${comment.userId}: ${comment.content}",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }
//                Spacer(Modifier.height(8.dp))
//                TextField(
//                    value = commentText,
//                    onValueChange = onCommentTextChange,
//                    label = { Text("Thêm bình luận") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp)
//                )
//                Spacer(Modifier.height(8.dp))
//                Button(
//                    onClick = onSubmitComment,
//                    modifier = Modifier
//                        .align(Alignment.End)
//                        .padding(end = 16.dp)
//                ) {
//                    Text("Gửi")
//                }
            }
        }
    }

    if (showDialog && selectedPostId != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    if (isEditable) stringResource(R.string.delete_title) else stringResource(R.string.hide_title),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Text(
                    if (isEditable) stringResource(R.string.delete_content) else stringResource(R.string.hide_content),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        selectedPostId = null
                        expandedMenu = false
                        onDeletePost()
                    }
                ) {
                    Text(if (isEditable) stringResource(R.string.delete) else stringResource(R.string.hide))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        selectedPostId = null
                    }
                ) {
                    Text("Huỷ")
                }
            }
        )
    }
}
