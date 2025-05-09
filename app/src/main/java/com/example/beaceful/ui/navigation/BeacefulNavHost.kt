package com.example.beaceful.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.beaceful.ui.components.PostDetailsScreen
import com.example.beaceful.ui.screens.forum.ForumScreen
import com.example.beaceful.ui.screens.diary.DiaryScreen
import com.example.beaceful.ui.screens.doctor.DoctorScreen
import com.example.beaceful.ui.screens.doctor.SingleDoctorProfileScreen
import com.example.beaceful.ui.screens.home.HomeScreen
import com.example.beaceful.ui.screens.profile.ProfileScreen

@Composable
fun BeacefulNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ) {
        composable(route = Home.route) {
            HomeScreen()
        }
        composable(route = Diary.route) {
            DiaryScreen()
        }
        composable(route = Doctor.route) {
            DoctorScreen(navController = navController)
        }
        composable(route = Forum.route) {
            ForumScreen()
        }
        composable(route = Profile.route) {
            ProfileScreen()
        }
        composable(
            route = SingleDoctorProfile.route,
            arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getInt("doctorId") ?: return@composable
            SingleDoctorProfileScreen(navController = navController, doctorId = doctorId)
        }

        composable(
            route = PostDetails.route,
            arguments = listOf(navArgument("postId") { type = NavType.IntType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: return@composable
            PostDetailsScreen(postId = postId)
        }

    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
