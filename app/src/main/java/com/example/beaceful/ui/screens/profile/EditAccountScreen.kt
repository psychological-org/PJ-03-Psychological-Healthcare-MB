package com.example.beaceful.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.ui.viewmodel.AuthViewModel
import com.example.beaceful.ui.viewmodel.ProfileViewModel

const val MAX_TEXT_FIELD_SIZE = 200

@Composable
fun EditAccountScreen(
    navController: NavController,
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val context = LocalContext.current
    val user by profileViewModel.user.collectAsState()
    val error by profileViewModel.error.collectAsState()
    val success by profileViewModel.success.collectAsState()
    val nameField = remember { mutableStateOf("") }
    val dobField = remember { mutableStateOf("") }
    val headlineField = remember { mutableStateOf("") }
    val biographyField = remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var backgroundUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        avatarUri = uri
    }
    val backgroundPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        backgroundUri = uri
    }

    LaunchedEffect(success) {
        success?.let { successMessage ->
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            profileViewModel.clearMessages()
        }
    }

    LaunchedEffect(error) {
        error?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            profileViewModel.clearMessages()
        }
    }

    LaunchedEffect(user) {
        user?.let {
            nameField.value = it.fullName ?: ""
            dobField.value = it.yearOfBirth?.toString() ?: ""
            headlineField.value = it.headline ?: ""
            biographyField.value = it.biography ?: ""
        }
        if (UserSession.getCurrentUserId() == null) {
            errorMessage = "Lỗi: Người dùng chưa đăng nhập"
        }
    }


    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                OutlinedButton(onClick = {
                    profileViewModel.updateUserProfile(
                        context = context,
                        fullName = nameField.value,
                        yearOfBirth = dobField.value,
                        headline = headlineField.value,
                        biography = biographyField.value,
                        avatarUri = avatarUri,
                        backgroundUri = backgroundUri
                    )
                }) {
                    Text("Lưu")
                }
            }
        }
        item {
            Text("Ảnh")
            Button(
                onClick = {avatarPicker.launch("image/*")},
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Ảnh đại diện", color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(4.dp),
                        textAlign = TextAlign.Left,
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

            }
            Button(
                onClick = {backgroundPicker.launch("image/*")},
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "Ảnh bìa", color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(4.dp),
                        textAlign = TextAlign.Left,
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        item {
            Text("Về tôi")
            Card(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(24.dp)
                    )
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Column() {
                    CustomTextField(nameField, "Họ và tên")
                    CustomTextField(dobField, "Ngày sinh")
                    CustomTextField(headlineField, "Headline")
                    CustomTextField(biographyField, "Tiểu sử", false)
                }
            }
        }

        item {
            Text("Tài khoản của tôi")
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "Đổi email", color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(4.dp),
                        textAlign = TextAlign.Left,
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Button(
                onClick = {
                    showDeleteDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "Xoá tài khoản", color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(4.dp),
                        textAlign = TextAlign.Left,
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
    // Hộp thoại xác nhận xóa tài khoản
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa tài khoản") },
            text = { Text("Bạn có chắc chắn muốn xóa tài khoản? Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileViewModel.deleteUser()
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun CustomTextField(
    itemField: MutableState<String>,
    label: String,
    isSingleLine: Boolean = true
) {
    Column(Modifier.background(MaterialTheme.colorScheme.primary)) {
        TextField(
            value = itemField.value,
            onValueChange = { if (it.length <= MAX_TEXT_FIELD_SIZE) itemField.value = it },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RectangleShape,
            singleLine = isSingleLine,
            label = { Text(label, color = MaterialTheme.colorScheme.secondary) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                cursorColor = MaterialTheme.colorScheme.secondary,

                ),
        )
        if (!isSingleLine) {
            Text(
                text = "${itemField.value.count()} / $MAX_TEXT_FIELD_SIZE",
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}