package com.example.beaceful.ui.screen

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.beaceful.domain.model.Message
import com.example.beaceful.ui.viewmodel.ChatDetailViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ChatDetailScreen(
    userId: Int,
    userName: String,
    onBack: () -> Unit,
    viewModel: ChatDetailViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.setChatPartner(userId, userName)
        viewModel.markMessagesAsRead()
    }

    val messages by viewModel.messages
    val selectedImageUri by viewModel.selectedImageUri
    val isRecording by viewModel.isRecording
    val recordedVoiceUri by viewModel.recordedVoiceUri
    var messageText by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Quyền đọc ảnh
    val readImagePermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    // Quyền máy ảnh
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    // Quyền ghi âm
    val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    // Launcher để chọn ảnh từ thư viện
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.setSelectedImage(uri)
    }

    // Launcher để chụp ảnh từ máy ảnh
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            viewModel.selectedImageUri.value?.let { uri ->
                viewModel.setSelectedImage(uri)
            }
        }
    }

    // Tạo URI tạm thời cho ảnh chụp từ máy ảnh
    fun createImageUri(context: Context): Uri {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // Dialog hiển thị khi cần giải thích quyền
    if (readImagePermissionState.status.shouldShowRationale ||
        cameraPermissionState.status.shouldShowRationale ||
        recordAudioPermissionState.status.shouldShowRationale) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Yêu cầu quyền") },
            text = {
                Text(
                    "Ứng dụng cần quyền truy cập ảnh, máy ảnh hoặc ghi âm để gửi hình ảnh và tin nhắn thoại."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    readImagePermissionState.launchPermissionRequest()
                    cameraPermissionState.launchPermissionRequest()
                    recordAudioPermissionState.launchPermissionRequest()
                }) {
                    Text("Cấp quyền")
                }
            },
            dismissButton = {
                TextButton(onClick = { /* Không làm gì */ }) {
                    Text("Từ chối")
                }
            }
        )
    }

    // Dialog chọn nguồn ảnh
    var showImageSourceDialog by remember { mutableStateOf(false) }
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Chọn nguồn ảnh", color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button (onClick = {
                        if (readImagePermissionState.status.isGranted) {
                            pickImageLauncher.launch("image/*")
                            showImageSourceDialog = false
                        } else {
                            readImagePermissionState.launchPermissionRequest()
                        }
                    }) { Text("Thư viện") }
                    Button(onClick = {
                        if (cameraPermissionState.status.isGranted) {
                            val newUri = createImageUri(context)
                            viewModel.setSelectedImage(newUri)
                            takePictureLauncher.launch(newUri)
                            showImageSourceDialog = false
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }) {
                        Text("Máy ảnh")
                    }
                }
            },
            confirmButton = { },
            dismissButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp)
            ) {
                // Hiển thị ảnh xem trước nếu có
                selectedImageUri?.let { uri ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Image Preview",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { viewModel.clearSelectedImage() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Image", tint = Color.Gray)
                        }
                    }
                }
                // Hiển thị voice xem trước nếu có
                recordedVoiceUri?.let { uri ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tin nhắn thoại",
                            modifier = Modifier.weight(1f),
                            color = Color.Black
                        )
                        IconButton(onClick = { viewModel.clearRecordedVoice() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Voice", tint = Color.Gray)
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showImageSourceDialog = true }) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = Color.Gray)
                    }
                    IconButton(onClick = {
                        if (recordAudioPermissionState.status.isGranted) {
                            if (isRecording) {
                                viewModel.stopRecording(context)
                            } else {
                                viewModel.startRecording(context)
                            }
                        } else {
                            recordAudioPermissionState.launchPermissionRequest()
                        }
                    }) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop Recording" else "Mic",
                            tint = if (isRecording) Color.Red else Color.Gray
                        )
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
                            if (messageText.isNotBlank() || selectedImageUri != null || recordedVoiceUri != null) {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendMessage(messageText)
                                    messageText = ""
                                }
                                if (selectedImageUri != null) {
                                    viewModel.sendImageMessage(context)
                                }
                                if (recordedVoiceUri != null) {
                                    viewModel.sendVoiceMessage(context)
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF6A1B9A))
                    }
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
    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }

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
                var hasContent = false
                if (!message.content.isNullOrBlank()) {
                    Text(
                        text = message.content,
                        color = if (isSentByCurrentUser) Color.White else Color.Black,
                        fontSize = 16.sp
                    )
                    hasContent = true
                }
                if (!message.imageUrl.isNullOrBlank()) {
                    println("Loading image URL: ${message.imageUrl}")
                    AsyncImage(
                        model = message.imageUrl,
                        contentDescription = "Sent Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        onError = {
                            println("Error loading image: ${message.imageUrl}")
//                            Text(
//                                text = "Không tải được ảnh",
//                                color = Color.Red,
//                                fontSize = 12.sp
//                            )
                        }
                    )
                    hasContent = true
                }
                if (!message.voiceUrl.isNullOrBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            if (isPlaying) {
                                mediaPlayer.stop()
                                mediaPlayer.reset()
                                isPlaying = false
                            } else {
                                try {
                                    mediaPlayer.setDataSource(message.voiceUrl)
                                    mediaPlayer.prepare()
                                    mediaPlayer.start()
                                    isPlaying = true
                                    mediaPlayer.setOnCompletionListener {
                                        isPlaying = false
                                        mediaPlayer.reset()
                                    }
                                } catch (e: Exception) {
                                    println("Error playing voice: ${e.message}")
                                    isPlaying = false
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Stop Voice" else "Play Voice",
                                tint = if (isSentByCurrentUser) Color.White else Color.Black
                            )
                        }
                        Text(
                            text = "Tin nhắn thoại",
                            color = if (isSentByCurrentUser) Color.White else Color.Black,
                            fontSize = 16.sp
                        )
                    }
                    hasContent = true
                }
                if (hasContent) {
                    Text(
                        text = message.createdAt.format(DateTimeFormatter.ofPattern("HH:mm")),
                        color = if (isSentByCurrentUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
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

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}