package com.example.beaceful.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.beaceful.domain.model.Message
import com.example.beaceful.ui.viewmodel.ChatDetailViewModel
import androidx.compose.ui.graphics.Brush
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    userId: Int,
    userName: String,
    onBack: () -> Unit,
    viewModel: ChatDetailViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        println("LaunchedEffect in ChatDetailScreen triggered") // Debug
        viewModel.setChatPartner(userId, userName)
        viewModel.markMessagesAsRead()
    }

    val messages by viewModel.messages
    var messageText by remember { mutableStateOf("") }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF4A148C), Color(0xFFAB47BC)),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = "https://via.placeholder.com/30",
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = userName,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF6A1B9A))
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: Chọn hình ảnh */ }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = Color.Gray)
                }
                IconButton(onClick = { /* TODO: Ghi âm */ }) {
                    Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Color.Gray)
                }
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    placeholder = { Text("Nhập tin nhắn...") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(messageText)
                            messageText = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF6A1B9A))
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBackground)
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages.reversed()) { message ->
                    MessageItem(message, viewModel.currentUserId ?: 0)
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, currentUserId: Int) {
    val isSentByCurrentUser = message.senderId == currentUserId
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isSentByCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isSentByCurrentUser) {
            AsyncImage(
                model = "https://via.placeholder.com/24",
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .align(Alignment.Top)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSentByCurrentUser) Color(0xFF9575CD) else Color.White,
            modifier = Modifier
                .widthIn(max = 250.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                if (!message.content.isNullOrBlank()) {
                    Text(
                        text = message.content,
                        color = if (isSentByCurrentUser) Color.White else Color.Black,
                        fontSize = 16.sp
                    )
                }
                Text(
                    text = message.createdAt.format(DateTimeFormatter.ofPattern("HH:mm")),
                    color = if (isSentByCurrentUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
        if (isSentByCurrentUser) {
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = "https://via.placeholder.com/24",
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .align(Alignment.Top)
            )
        }
    }
}