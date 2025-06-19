package com.example.beaceful.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.beaceful.R
import com.example.beaceful.domain.model.ProfileSelection
import com.example.beaceful.ui.navigation.CustomerDetails
import com.example.beaceful.ui.navigation.EditAccountRoute
import com.example.beaceful.ui.navigation.LoginRoute
import com.example.beaceful.ui.viewmodel.AuthViewModel
import com.example.beaceful.ui.viewmodel.ChatViewModel
import com.example.beaceful.ui.viewmodel.ProfileViewModel
import com.example.beaceful.viewmodel.DoctorViewModel

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
) {
    val user by profileViewModel.user.collectAsState()
    val error by profileViewModel.error.collectAsState()
    val listSelection = listOf(
        ProfileSelection("Tài khoản", Icons.Default.AccountCircle) {
            navController.navigate(EditAccountRoute.route)
        },
        ProfileSelection("Riêng tư & an toàn", Icons.Default.Lock) {
            // TODO: Implement privacy & security settings
        },
        ProfileSelection("Thông báo", Icons.Default.Notifications) {
            // TODO: Implement notification settings
        },
        ProfileSelection("Hỗ trợ", Icons.AutoMirrored.Filled.Help) {
            // TODO: Implement help/support
        },
        ProfileSelection("Về chúng tôi", Icons.Default.Info) {
            // TODO: Implement about us
        },
    )

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    if (user == null && error == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                Box {
                    AsyncImage(
                        model = user!!.backgroundUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                    AsyncImage(
                        model = user!!.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = 24.dp, y = 50.dp)
                            .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            item {
                Spacer(Modifier.height(60.dp))

                // Tên
                Text(
                    text = user!!.fullName,
                    style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(16.dp))
            }
            items(listSelection) { selection ->
                Row(Modifier.padding(horizontal = 16.dp)) {
                    Button(
                        onClick = selection.onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Icon(
                            selection.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            selection.title, color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.fillMaxWidth().padding(4.dp),
                            textAlign = TextAlign.Left,
                        )
                    }
                }
            }
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            chatViewModel.clearDataOnLogout()
                            authViewModel.logout()
                            navController.navigate(LoginRoute.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Đăng xuất", color = MaterialTheme.colorScheme.onSecondary)
                    }
                }
            }
        }
    }
}

