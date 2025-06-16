package com.example.beaceful.ui.screens.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.ui.components.cards.NotificationList
import com.example.beaceful.ui.viewmodel.NotificationViewModel

@Composable
fun NotificationScreen(
    navController: NavHostController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val userRole = UserSession.getCurrentUserRole()
    val notifications by viewModel.getNotifications(userRole).collectAsState(initial = emptyList())

    // Sử dụng LaunchedEffect để tránh recomposition lặp khi userRole không đổi
    LaunchedEffect(userRole) {
        // Log để debug
        println("NotificationScreen recomposed with userRole: $userRole")
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Thông báo",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        NotificationList(
            notifications = notifications,
            navController = navController,
            modifier = Modifier.fillMaxHeight()
        )
    }
}