package com.example.beaceful.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.beaceful.domain.model.User
import com.example.beaceful.ui.viewmodel.ChatPreview
import com.example.beaceful.ui.viewmodel.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beaceful.domain.model.SearchItem
import com.example.beaceful.ui.components.CustomSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onUserClick: (String, String) -> Unit,
) {
    val users by viewModel.users
    val chatPreviews by viewModel.chatPreviews
    val error by viewModel.error.collectAsState()
    val nameSuggestions = remember(users) {
        users.map { SearchItem<String>(id = it.id, name = it.fullName) }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    println("ChatScreen: users.size=${users.size}, chatPreviews.size=${chatPreviews.size}, error=$error")

    LazyColumn (contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            CustomSearchBar(
                suggestions = nameSuggestions,
                placeholder = "Tìm đoạn chat...",
                onSearch = { selected ->
                    if (selected != null) {
                        val user = users.find { it.id == selected.id }
                        if (user != null) {
                            println("Selected user: ${user.fullName} (${user.id})")
                            onUserClick(user.id, user.fullName)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (chatPreviews.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Chưa có đoạn chat nào", color = Color.Gray)
                }
            }
        } else {
            println("ChatPreviews size: ${chatPreviews.size}")
            items(chatPreviews.entries.toList()) { (userId, preview) ->
                val user = users.find { it.id == userId }
                if (user != null) {
                    println("Displaying chat preview for userId: $userId, name: ${user.fullName}")
                    UserItem(
                        user = user,
                        preview = preview,
                        onClick = { onUserClick(user.id, user.fullName) }
                    )
                } else {
                    println("User not found for userId: $userId")
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
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl ?: "https://via.placeholder.com/50",
            contentDescription = "Avatar",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.fullName,
                fontWeight = if (preview!!.isNewMessage) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = preview.lastMessage,
                fontSize = 14.sp,
                fontWeight = if (preview.isNewMessage) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = preview!!.createdAt,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}