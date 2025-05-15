package com.example.beaceful.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.ui.components.PostDetailsScreen
import com.example.beaceful.ui.screens.diary.DiaryFullScreen
import com.example.beaceful.ui.screens.diary.DiaryScreen
import com.example.beaceful.ui.screens.diary.FullscreenDiaryScreen
import com.example.beaceful.ui.screens.diary.SelectEmotionScreen
import com.example.beaceful.ui.screens.diary.WriteDiaryScreen
import com.example.beaceful.ui.screens.doctor.DoctorScreen
import com.example.beaceful.ui.screens.doctor.SingleDoctorProfileScreen
import com.example.beaceful.ui.screens.forum.CommunityScreen
import com.example.beaceful.ui.screens.forum.ForumScreen
import com.example.beaceful.ui.screens.home.HomeScreen
import com.example.beaceful.ui.screens.profile.EditProfileScreen
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
            HomeScreen(navController = navController)
        }
        composable(route = DiaryRoute.route) {
            DiaryScreen(navController = navController)
        }
        composable(route = Doctor.route) {
            DoctorScreen(navController = navController)
        }
        composable(route = Forum.route) {
            ForumScreen(navController = navController)
        }

        composable(
            route = CommunityRoute.route,
            arguments = listOf(navArgument("communityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val communityId = backStackEntry.arguments?.getInt("communityId") ?: return@composable
            CommunityScreen(navController = navController, communityId = communityId)

        }
        composable(route = Profile.route) {
            ProfileScreen(
                userId = 2,
                navController = navController
            )
        }
        composable(route = EditRoute.route) {
            EditProfileScreen()
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

        composable(
            route = DiaryDetails.route,
            arguments = listOf(navArgument("diaryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val diaryId = backStackEntry.arguments?.getInt("diaryId") ?: return@composable
            DiaryFullScreen(diaryId = diaryId)
        }


        composable(
            route = WriteDiary.route,
            arguments = listOf(navArgument("emotion") { type = NavType.StringType })
        ) { backStackEntry ->
            val emotionArg = backStackEntry.arguments?.getString("emotion")
            val emotion = runCatching { Emotions.valueOf(emotionArg ?: "") }.getOrNull()
            if (emotion != null) {
                WriteDiaryScreen(navController, selectedEmotion = emotion)
            } else {
                Text("Emotion không hợp lệ")
            }
        }
        composable(
            route = WriteDiaryExpand.route
        ) {
            FullscreenDiaryScreen(
                navController = navController
            )
        }
        composable(
            route = SelectEmotionDiary.route
        ) {
            SelectEmotionScreen(
                navController = navController
            )
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
