package com.example.beaceful.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.example.beaceful.domain.model.User
import com.example.beaceful.ui.viewmodel.ChatPreview
import com.example.beaceful.ui.viewmodel.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    onUserClick: (Int, String) -> Unit,
    onLogout: () -> Unit
) {
    val users by viewModel.users
    val chatPreviews by viewModel.chatPreviews

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { padding ->
        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Không có người dùng nào", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(users) { user ->
                    UserItem(
                        user = user,
                        preview = chatPreviews[user.id],
                        onClick = { onUserClick(user.id, user.fullName) }
                    )
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    preview: ChatPreview?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl ?: "https://via.placeholder.com/50",
            contentDescription = "Avatar",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.fullName,
                fontWeight = if (preview?.isNewMessage == true) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp
            )
            Text(
                text = preview?.lastMessage ?: "Chưa có tin nhắn",
                fontSize = 14.sp,
                fontWeight = if (preview?.isNewMessage == true) FontWeight.Bold else FontWeight.Normal,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = preview?.createdAt ?: "",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}