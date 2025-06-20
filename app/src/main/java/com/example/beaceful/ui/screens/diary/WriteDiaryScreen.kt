package com.example.beaceful.ui.screens.diary

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.beaceful.R
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.ui.components.calendar.TimePickerDialog
import com.example.beaceful.ui.navigation.DiaryRoute
import com.example.beaceful.ui.navigation.WriteDiary
import com.example.beaceful.ui.navigation.WriteDiaryExpand
import com.example.beaceful.ui.screens.home.EmotionItem
import com.example.beaceful.ui.viewmodel.DiaryViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

const val SAVED_DIARY_KEY = "diary_content"

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WriteDiaryScreen(
    navController: NavHostController,
    selectedEmotion: Emotions,
    selectedDate: LocalDateTime,
    modifier: Modifier = Modifier,
    viewModel: DiaryViewModel = hiltViewModel(),
) {
    val userId = UserSession.getCurrentUserId()
    val diaryContentFromFullScreen = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow(SAVED_DIARY_KEY, "")
    var diaryContent by remember { mutableStateOf("") }
    var diaryTitle by remember { mutableStateOf("No title") }

    val latestDiaryText by diaryContentFromFullScreen?.collectAsState()
        ?: remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var recordedVoiceUri by remember { mutableStateOf<Uri?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val mediaPlayer = remember { mutableStateOf<MediaPlayer?>(null) }
    val isPlaying = remember { mutableStateOf(false) }
    val currentPosition = remember { mutableStateOf(0) }
    val duration = remember { mutableStateOf(1) }

    val handler = remember { Handler(Looper.getMainLooper()) }
    val updatePosition = remember {
        object : Runnable {
            override fun run() {
                mediaPlayer.value?.let {
                    currentPosition.value = it.currentPosition
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    // Quyền đọc ảnh
    val readImagePermissionState = rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    // Quyền máy ảnh
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    // Quyền ghi âm
    val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    // Launcher để chọn ảnh từ thư viện
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri = uri
        }

    // Launcher để chụp ảnh từ máy ảnh
    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedImageUri?.let { uri ->
                    selectedImageUri = uri
                }
            }
        }

    // MediaRecorder để ghi âm
    val mediaRecorder = remember { mutableStateOf<MediaRecorder?>(null) }
    val tempVoiceFile = remember { mutableStateOf<File?>(null) }

    fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file =
                File(context.filesDir, "images/diary_image_${System.currentTimeMillis()}.jpg")
            file.parentFile?.mkdirs()
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            println("Error saving image: ${e.message}")
            null
        }
    }

    fun saveVoiceToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "voices/diary_voice_${System.currentTimeMillis()}.m4a")
            file.parentFile?.mkdirs() // Tạo folder voices nếu chưa có

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            println("Error saving voice: ${e.message}")
            null
        }
    }


    // Tạo URI tạm thời cho ảnh chụp từ máy ảnh
    fun createImageUri(context: Context): Uri {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    // Bắt đầu ghi âm
    fun startRecording() {
        if (recordAudioPermissionState.status.isGranted) {
            try {
                tempVoiceFile.value = File(context.cacheDir, "voice_${UUID.randomUUID()}.m4a")
                mediaRecorder.value = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(tempVoiceFile.value!!.absolutePath)
                    prepare()
                    start()
                }
                isRecording = true
                println("Started recording: ${tempVoiceFile.value!!.absolutePath}")
            } catch (e: Exception) {
                println("Error starting recording: ${e.message}")
                isRecording = false
            }
        } else {
            recordAudioPermissionState.launchPermissionRequest()
        }
    }

    // Dừng ghi âm
    fun stopRecording() {
        try {
            mediaRecorder.value?.apply {
                stop()
                release()
            }
            mediaRecorder.value = null
            isRecording = false
            tempVoiceFile.value?.let { file ->
                recordedVoiceUri = Uri.fromFile(file)
                println("Stopped recording: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            println("Error stopping recording: ${e.message}")
            isRecording = false
        }
    }

    fun playRecordedVoice(uri: Uri) {
        try {
            if (mediaPlayer.value == null) {
                mediaPlayer.value = MediaPlayer().apply {
                    setDataSource(context, uri)
                    prepare()
                    start()
                    isPlaying.value = true
                    duration.value = this.duration
                    handler.post(updatePosition)

                    setOnCompletionListener {
                        // Phát lại từ đầu
                        seekTo(0)
                        start()
                    }
                }
            } else {
                mediaPlayer.value?.start()
                isPlaying.value = true
                handler.post(updatePosition)
            }
        } catch (e: Exception) {
            println("Error playing voice: ${e.message}")
        }
    }

    fun pausePlayback() {
        mediaPlayer.value?.pause()
        isPlaying.value = false
        handler.removeCallbacks(updatePosition)
    }

    fun stopPlayback() {
        mediaPlayer.value?.release()
        mediaPlayer.value = null
        isPlaying.value = false
        handler.removeCallbacks(updatePosition)
        currentPosition.value = 0
    }


    if (readImagePermissionState.status.shouldShowRationale ||
        cameraPermissionState.status.shouldShowRationale ||
        recordAudioPermissionState.status.shouldShowRationale
    ) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Yêu cầu quyền") },
            text = { Text("Ứng dụng cần quyền truy cập ảnh, máy ảnh hoặc ghi âm để thêm hình ảnh và tin nhắn thoại vào nhật ký.") },
            confirmButton = {
                TextButton(onClick = {
                    readImagePermissionState.launchPermissionRequest()
                    cameraPermissionState.launchPermissionRequest()
                    recordAudioPermissionState.launchPermissionRequest()
                }) { Text("Cấp quyền") }
            },
            dismissButton = {
                TextButton(onClick = { /* Không làm gì */ }) { Text("Từ chối") }
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
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
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
                            selectedImageUri = newUri
                            takePictureLauncher.launch(newUri)
                            showImageSourceDialog = false
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }) { Text("Máy ảnh") }
                }
            },
            confirmButton = { },
            dismissButton = {
                TextButton(onClick = { showImageSourceDialog = false }) { Text("Hủy") }
            }
        )
    }

    LaunchedEffect(latestDiaryText) {
        diaryContent = latestDiaryText
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row {
            IconButton(onClick = { navController.popBackStack()
                stopPlayback()
            }) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Image(
                painter = painterResource(selectedEmotion.iconRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(18.dp))
            )
        }
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = diaryTitle,
                    onValueChange = { diaryTitle = it },
                    placeholder = { Text("No title", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    trailingIcon = {
                        Icon(
                            Icons.Default.Clear, contentDescription = null,
                            modifier = Modifier.clickable(onClick = { diaryTitle = "" })
                        )
                    }
                )
            }
            item {
                Text(
                    text = stringResource(R.string.di6_record_your_thought),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // --- Diary Text Area ---
            item {
                Text(
                    "Nhật ký",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Spacer(Modifier.height(4.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                    TextField(
                        value = diaryContent,
                        onValueChange = { diaryContent = it },
                        placeholder = { Text("Nhập ghi chép...", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(24.dp)
                            ),
                    )
                    IconButton(onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            SAVED_DIARY_KEY,
                            diaryContent
                        )
                        navController.navigate(WriteDiaryExpand.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Mở rộng",
                            tint = Color(0xFF1A0033)
                        )
                    }
                }
            }

            // --- Image/Video Upload ---
            item {
                Text(
                    "Hình ảnh",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Spacer(Modifier.height(4.dp))

                if (selectedImageUri == null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        UploadButton(label = "Chọn ảnh") {
                            showImageSourceDialog = true
                        }
                    }
                }

                // Hiển thị ảnh đã chọn
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
                        IconButton(onClick = { selectedImageUri = null }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear Image",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

            // --- Voice Record ---
            item {
                Text(
                    "Ghi âm",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Spacer(Modifier.height(4.dp))

                if (recordedVoiceUri == null) {
                    UploadButton(label = if (isRecording) "Dừng ghi âm" else "Nhấn để ghi âm") {
                        if (isRecording) stopRecording() else startRecording()
                    }
                }

                // Hiển thị voice đã ghi
                recordedVoiceUri?.let { uri ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Box(){
                            // Nút phát / tạm dừng
                            IconButton(
                                onClick = {
                                    if (isPlaying.value) {
                                        pausePlayback()
                                    } else {
                                        playRecordedVoice(uri)
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Icon(
                                    imageVector = if (isPlaying.value) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            // Thanh tiến độ
                            Slider(
                                value = currentPosition.value.toFloat(),
                                onValueChange = { value ->
                                    currentPosition.value = value.toInt()
                                    mediaPlayer.value?.seekTo(value.toInt())
                                },
                                valueRange = 0f..duration.value.toFloat(),
                                modifier = Modifier
                                    .padding(horizontal = 40.dp)
                                    .align(Alignment.Center)
                            )

                            IconButton(onClick = {
                                recordedVoiceUri = null
                                tempVoiceFile.value?.delete()
                                stopPlayback()
                            },
                                modifier = Modifier.align(Alignment.CenterEnd)
                                ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear Voice",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }

            }

            // --- Confirm Button ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {

                           stopPlayback()

                            val savedImagePath =
                                selectedImageUri?.let { saveImageToInternalStorage(it) }
                            val savedVoicePath =
                                recordedVoiceUri?.let { saveVoiceToInternalStorage(it) }

                            viewModel.saveDiary(
                                emotion = selectedEmotion,
                                title = diaryTitle,
                                content = diaryContent.takeIf { it.isNotBlank() },
                                imageUrl = savedImagePath,
                                voiceUrl = savedVoicePath,
                                posterId = userId,
                                createdAt = selectedDate
                            )
//                            navController.popBackStack("diary", inclusive = false)
                            navController.navigate(DiaryRoute.route)

                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Xác nhận", color = Color.White)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaRecorder.value?.release()
            if (!isRecording) {
                tempVoiceFile.value?.delete()
            }
        }
    }
}

@Composable
fun UploadButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
    ) {
        Text(label, color = MaterialTheme.colorScheme.onTertiary)
    }
}

@Composable
fun FullscreenDiaryScreen(navController: NavHostController) {
    val previousBackStackEntry = navController.previousBackStackEntry
    val savedStateHandle = previousBackStackEntry?.savedStateHandle
    val existingContent = savedStateHandle?.get<String>(SAVED_DIARY_KEY) ?: ""

    var diaryText by remember { mutableStateOf(existingContent) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Ghi chép chi tiết", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = diaryText,
            onValueChange = { diaryText = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = { Text("Viết mọi điều bạn muốn...") },
            maxLines = Int.MAX_VALUE,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                savedStateHandle?.set(SAVED_DIARY_KEY, diaryText)
                navController.popBackStack()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Lưu")
        }
    }
}