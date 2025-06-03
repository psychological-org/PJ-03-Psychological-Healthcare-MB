package com.example.beaceful.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.beaceful.domain.model.Emotions
import com.example.beaceful.ui.components.PostDetailsScreen
import com.example.beaceful.ui.screens.chat.ChatDetailScreen
import com.example.beaceful.ui.screens.appointment.AppointmentDetailsScreen
import com.example.beaceful.ui.screens.appointment.AppointmentScreen
import com.example.beaceful.ui.screens.customer.CustomerDetailsScreen
import com.example.beaceful.ui.screens.customer.CustomerScreen
import com.example.beaceful.ui.screens.diary.DiaryFullScreen
import com.example.beaceful.ui.screens.diary.DiaryScreen
import com.example.beaceful.ui.screens.diary.FullscreenDiaryScreen
import com.example.beaceful.ui.screens.diary.SelectEmotionScreen
import com.example.beaceful.ui.screens.diary.WriteDiaryScreen
import com.example.beaceful.ui.screens.doctor.BookingScreen
import com.example.beaceful.ui.screens.doctor.DoctorScreen
import com.example.beaceful.ui.screens.doctor.SingleDoctorProfileScreen
import com.example.beaceful.ui.screens.forum.CommunityScreen
import com.example.beaceful.ui.screens.forum.ForumScreen
import com.example.beaceful.ui.screens.home.HomeScreen
import com.example.beaceful.ui.screens.profile.EditAccountScreen
import com.example.beaceful.ui.screens.profile.EditProfileScreen
import com.example.beaceful.ui.screens.profile.ProfileScreen
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
            ForumScreen(navController = navController, userId = 4)
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
            EditProfileScreen(navController = navController)
        }
        composable(route = EditAccountRoute.route) {
            EditAccountScreen()
        }
        composable(
            route = SingleDoctorProfile.route,
            arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getInt("doctorId") ?: return@composable
            SingleDoctorProfileScreen(navController = navController, doctorId = doctorId)
        }
        composable(
            route = Booking.route,
            arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getInt("doctorId") ?: return@composable
            BookingScreen(navController = navController, doctorId = doctorId)
        }
        composable(
            route = Booking.route,
            arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getInt("doctorId") ?: return@composable
            BookingScreen(navController = navController, doctorId = doctorId)
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
            DiaryFullScreen(
                diaryId = diaryId,
                navController = navController
            )
        }


        composable(
            route = "diary_write/{emotion}/{datetime}",
            arguments = listOf(
                navArgument("emotion") { type = NavType.StringType },
                navArgument("datetime") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val emotionArg = backStackEntry.arguments?.getString("emotion")
            val datetimeArg = backStackEntry.arguments?.getString("datetime")

            val emotion = runCatching { Emotions.valueOf(emotionArg ?: "") }.getOrNull()
            val datetime = runCatching { LocalDateTime.parse(datetimeArg) }.getOrNull()

            if (emotion != null && datetime != null) {
                WriteDiaryScreen(navController, selectedEmotion = emotion, selectedDate = datetime)
            } else {
                Text("Emotion hoặc thời gian không hợp lệ")
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
//        composable(
//            route = DiaryCalendar.route
//        ) {
//            CalendarDiaryScreen(
//                navController = navController
//            )
//        }
        composable(
            route = ChatDetailRoute.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("userName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val userName = backStackEntry.arguments?.getString("userName") ?: ""
            ChatDetailScreen(
                userId = userId,
                userName = userName,
                onBack = { navController.popBackStack() },
                viewModel = viewModel()
            )
        }
        composable(
            route = AppointmentRoute.route
        ) {
            AppointmentScreen(
                navController = navController
            )
        }
        composable(
            route = CustomerRoute.route
        ) {
            CustomerScreen(
                navController = navController
            )
        }
//        composable(
//            route = CustomerDetails.route,
//            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val customerId = backStackEntry.arguments?.getInt("customerId") ?: return@composable
//            CustomerDetailsScreen(customerId = customerId, navController = navController)
//        }
//        composable(
//            route = AppointmentDetails.route,
//            arguments = listOf(navArgument("appointmentId") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val appointmentId =
//                backStackEntry.arguments?.getInt("appointmentId") ?: return@composable
//            AppointmentDetailsScreen(appointmentId = appointmentId)
//        }
        composable(
            route = AppointmentRoute.route
        ) {
            AppointmentScreen(
                navController = navController
            )
        }
        composable(
            route = CustomerRoute.route
        ) {
            CustomerScreen(
                navController = navController
            )
        }
        composable(
            route = CustomerDetails.route,
            arguments = listOf(navArgument("customerId") { type = NavType.IntType },
                navArgument("isDoctorView") { type = NavType.BoolType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getInt("customerId") ?: return@composable
            val isDoctorView =
                backStackEntry.arguments?.getBoolean("isDoctorView") ?: return@composable
            CustomerDetailsScreen(
                customerId = customerId,
                navController = navController,
                isDoctorView = isDoctorView
            )
        }
        composable(
            route = AppointmentDetails.route,
            arguments = listOf(navArgument("appointmentId") { type = NavType.IntType },
                navArgument("isDoctorView") { type = NavType.BoolType })
        ) { backStackEntry ->
            val appointmentId =
                backStackEntry.arguments?.getInt("appointmentId") ?: return@composable
            val isDoctorView =
                backStackEntry.arguments?.getBoolean("isDoctorView") ?: return@composable
            AppointmentDetailsScreen(appointmentId = appointmentId,  isDoctorView = isDoctorView)
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
