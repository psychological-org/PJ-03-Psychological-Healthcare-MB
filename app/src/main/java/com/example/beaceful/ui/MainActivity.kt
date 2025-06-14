package com.example.beaceful.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.beaceful.domain.firebase.FirebaseTest
import com.example.beaceful.R
import com.example.beaceful.core.util.UserSession
import com.example.beaceful.domain.amazon.S3Manager
import com.example.beaceful.ui.components.BottomNavRow
import com.example.beaceful.ui.navigation.AppointmentDetails
import com.example.beaceful.ui.navigation.AppointmentRoute
import com.example.beaceful.ui.navigation.BeacefulBottomNavDoctor
import com.example.beaceful.ui.navigation.BeacefulNavHost
import com.example.beaceful.ui.navigation.BeacefulRoutes
import com.example.beaceful.ui.navigation.ChatDetailRoute
import com.example.beaceful.ui.navigation.BeacefulBottomNavPatient
import com.example.beaceful.ui.navigation.Booking
import com.example.beaceful.ui.navigation.CustomerRoute
import com.example.beaceful.ui.navigation.DiaryCalendar
import com.example.beaceful.ui.navigation.DiaryDetails
import com.example.beaceful.ui.navigation.DiaryRoute
import com.example.beaceful.ui.navigation.Doctor
import com.example.beaceful.ui.navigation.EditAccountRoute
import com.example.beaceful.ui.navigation.EditRoute
import com.example.beaceful.ui.navigation.Forum
import com.example.beaceful.ui.navigation.ForumTab
import com.example.beaceful.ui.navigation.Home
import com.example.beaceful.ui.navigation.LoginRoute
import com.example.beaceful.ui.navigation.Profile
import com.example.beaceful.ui.navigation.SelectEmotionDiary
import com.example.beaceful.ui.navigation.SingleDoctorProfile
import com.example.beaceful.ui.navigation.navigateSingleTopTo
import com.example.beaceful.ui.theme.BeacefulTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        S3Manager.initialize(this)
        setContent {
            BeacefulApp()
        }
    }
}

@Composable
fun BeacefulApp() {
    BeacefulTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        fun resolveTab(route: String?): BeacefulRoutes {
            return when {
                route == null -> Home
                route.startsWith("doctor") -> Doctor
                route.startsWith("forum") -> Forum
                route.startsWith("diary") -> DiaryRoute
                route.startsWith("profile") -> Profile
                else -> Home
            }
        }

        LaunchedEffect(Unit) {
            try {
                UserSession.getCurrentUserId()
            } catch (e: IllegalStateException) {
                Log.d("BeacefulApp", "User not logged in, navigating to login")
                navController.navigateSingleTopTo(LoginRoute.route)
            }
        }

        val showBottomBar = currentDestination?.route in listOf(
            Home.route,
            Doctor.route,
            Forum.route,
            DiaryRoute.route,
            DiaryCalendar.route,
            DiaryDetails.route,
            SelectEmotionDiary.route,
            SingleDoctorProfile.route,
            Profile.route,
            Booking.route,
            AppointmentRoute.route,
            CustomerRoute.route,
            EditAccountRoute.route,
            EditRoute.route,
            AppointmentDetails.route,
            ForumTab.route
            )

        val currentScreen = resolveTab(currentDestination?.route)

        val role by UserSession.currentUserRole.collectAsState()
        Log.d("BeacefulApp", "Current user role (state): $role")

        // Chọn danh sách mục dựa trên role
        val bottomNavItems = when (role) {
            "patient" -> BeacefulBottomNavPatient
            "doctor" -> BeacefulBottomNavDoctor
            "admin" -> BeacefulBottomNavDoctor
            else -> BeacefulBottomNavPatient // Mặc định trước khi đăng nhập
        }

        // Log danh sách bottomNavItems
        Log.d("BeacefulApp", "Selected bottom nav items: ${bottomNavItems.map { it.route }}")

        Scaffold(
            bottomBar = {
                if (showBottomBar && role != null) {
                    BottomNavRow(
                        allScreens = bottomNavItems,
                        onTabSelected = { newScreen ->
                            navController.navigateSingleTopTo(newScreen.route)
                        },
                        currentScreen = currentScreen,
                    )
                }
            },
            containerColor = colorResource(R.color.purple_700),
            contentColor = colorResource(R.color.white)
        ) { innerPadding ->
            BeacefulNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview
@Composable
fun BeacefulAppPreview() {
    BeacefulApp()
}