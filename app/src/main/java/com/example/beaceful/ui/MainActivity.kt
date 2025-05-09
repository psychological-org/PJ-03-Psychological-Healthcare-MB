package com.example.beaceful.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.beaceful.R
import com.example.beaceful.ui.components.BottomNavRow
import com.example.beaceful.ui.navigation.BeacefulBottomNavPatient
import com.example.beaceful.ui.navigation.BeacefulNavHost
import com.example.beaceful.ui.navigation.BeacefulRoutes
import com.example.beaceful.ui.navigation.Diary
import com.example.beaceful.ui.navigation.Doctor
import com.example.beaceful.ui.navigation.Forum
import com.example.beaceful.ui.navigation.Home
import com.example.beaceful.ui.navigation.SingleDoctorProfile
import com.example.beaceful.ui.navigation.navigateSingleTopTo
import com.example.beaceful.ui.theme.BeacefulTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                route.startsWith("diary") -> Diary
                else -> Home
            }
        }

        val showBottomBar = currentDestination?.route in listOf(
            Home.route,
            Doctor.route,
            Forum.route,
            Diary.route,
            SingleDoctorProfile.route
            )

        val currentScreen = resolveTab(currentDestination?.route)
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavRow(
                        allScreens = BeacefulBottomNavPatient,
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