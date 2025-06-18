package com.example.beaceful.ui.screens.diary

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.ui.components.AnimatedCircle
import com.example.beaceful.ui.components.preprocessProportions
import com.example.beaceful.ui.navigation.WriteDiaryExpand
import com.example.beaceful.ui.viewmodel.DiaryViewModel
import com.example.beaceful.ui.viewmodel.RecommendationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File
import java.util.UUID

data class PieChartData(
    val label: String,
    val score: Float,
    val color: Color,
)

//val emotions: List<PieChartData> = listOf(
//    PieChartData(
//        label = "anger",
//        score = (0.5293946266174316).toFloat(),
//        color = Emotions.ANGRY.textColor
//    ),
//    PieChartData(
//        label = "sadness",
//        score = (0.3829881548881531).toFloat(),
//        color = Emotions.SAD.textColor
//    ),
//    PieChartData(
//        label = "joy",
//        score = (0.07129291445016861).toFloat(),
//        color = Emotions.HAPPY.textColor
//    ),
//    PieChartData(
//        label = "fear",
//        score = (0.011929457075893879).toFloat(),
//        color = Emotions.CONFUSE.textColor
//    ),
//    PieChartData(
//        label = "love",
//        score = (0.0034298289101570845).toFloat(),
//        color = Emotions.INLOVE.textColor
//    )
//)
//val negativity_score = 0.9252

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DiaryFullScreen(
    diaryId: Int,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: DiaryViewModel = hiltViewModel(),
    recommendationViewModel: RecommendationViewModel = hiltViewModel()
) {
    val diary = viewModel.getDiary(diaryId)
    if (diary == null) {
        Text("Không tìm thấy nhật ký", modifier = Modifier.fillMaxSize())
        return
    }

    val diaryContentFromFullScreen = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow(SAVED_DIARY_KEY, diary.content ?: "")
    var diaryText by remember { mutableStateOf(diary.content ?: "") }
    var diaryTitle by remember { mutableStateOf(diary.title) }
    val latestDiaryText by diaryContentFromFullScreen?.collectAsState()
        ?: remember { mutableStateOf(diary.content ?: "") }
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(diary.imageUrl?.let {
            Uri.fromFile(
                File(
                    it
                )
            )
        })
    }
    var recordedVoiceUri by remember { mutableStateOf<Uri?>(diary.voiceUrl?.let { Uri.parse(it) }) }
    var isRecording by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

//    LaunchedEffect(latestDiaryText) {
//        diaryText = latestDiaryText
//    }

    // Log và gọi API khi diaryText thay đổi
    LaunchedEffect(diaryText) {
        Log.d("DiaryFullScreen", "Diary content: $diaryText")
        if (diaryText.isNotBlank()) {
            recommendationViewModel.getRecommendation(diaryText)
        }
    }

    var emotionsState by remember {
        mutableStateOf<List<PieChartData>>(emptyList())
    }
    var recommendationText by remember { mutableStateOf("") }

    // Quan sát LiveData từ RecommendationViewModel
    val recommendation by recommendationViewModel.recommendation.observeAsState()
    LaunchedEffect(recommendation) {
        recommendation?.let { result ->
            if (result.isSuccess) {
                val answerResponse = result.getOrNull()
                val answer = answerResponse?.answer
                Log.d("DiaryFullScreen", "Recommendation success: $answer")
                // Cập nhật recommendation text
                recommendationText = answer ?: "Không có khuyến nghị"

                // Lấy dữ liệu emotions từ AnswerResponse (giả định AnswerResponse chứa emotions)
                // Cần cập nhật model AnswerResponse để chứa emotions và negativity_score
                emotionsState = (answerResponse?.emotions ?: emptyList()).map { emotion ->
                    PieChartData(
                        label = emotion.label,
                        score = emotion.score.toFloat(),
                        color = when (emotion.label) {
                            "joy" -> Emotions.HAPPY.textColor
                            "sadness" -> Emotions.SAD.textColor
                            "anger" -> Emotions.ANGRY.textColor
                            "love" -> Emotions.INLOVE.textColor
                            "fear" -> Emotions.CONFUSE.textColor
                            "surprise" -> Emotions.CONFUSE.textColor // Gán tạm surprise
                            else -> Color.Gray
                        }
                    )
                }
            } else if (result.isFailure) {
                Log.e("DiaryFullScreen", "Recommendation error: ${result.exceptionOrNull()?.message}")
                recommendationText = "Lỗi khi lấy khuyến nghị: ${result.exceptionOrNull()?.message}"
                emotionsState = emptyList()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Image(
                painter = painterResource(diary.emotion.iconRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(18.dp))
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                        unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                        focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                    ),
                    trailingIcon = {
                        Icon(
                            Icons.Default.Clear, contentDescription = null,
                            modifier = Modifier.clickable(onClick = {diaryTitle = ""})
                        )
                    }
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
                        value = diaryText,
                        onValueChange = { diaryText = it },
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
                            diaryText
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
                        .padding(horizontal = 8.dp, vertical = 4.dp)
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
                                .size(60.dp)
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
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(Modifier.height(4.dp))

                UploadButton(label = if (isRecording) "Dừng ghi âm" else "Nhấn để ghi âm") {
                    if (isRecording) stopRecording() else startRecording()
                }

                // Hiển thị voice đã ghi
                recordedVoiceUri?.let { uri ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth().background(color = MaterialTheme.colorScheme.tertiary)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tin nhắn thoại",
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                        IconButton(onClick = {
                            recordedVoiceUri = null; tempVoiceFile.value?.delete()
                        }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear Voice",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    "Phân tích từ AI",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(Modifier.height(4.dp))
                AnimatedCircle(
                    proportions = preprocessProportions(emotionsState),
                )
            }
//            item {
//                // Hiển thị recommendation text
//                Text(
//                    text = recommendationText,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.White,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 8.dp)
//                        .background(
//                            MaterialTheme.colorScheme.secondary,
//                            RoundedCornerShape(8.dp)
//                        )
//                        .padding(12.dp)
//                )
//            }
            item {
                // --- Confirm Button ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            val savedImagePath =
                                selectedImageUri?.let { saveImageToInternalStorage(it) }
                            viewModel.updateDiary(
                                id = diaryId,
                                content = diaryText.takeIf { it.isNotBlank() },
                                imageUrl = savedImagePath,
                                voiceUrl = recordedVoiceUri?.toString()
                            )
                            navController.popBackStack("diary", inclusive = false)
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    ) {
                        Text("Cập nhật", color = Color.White)
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