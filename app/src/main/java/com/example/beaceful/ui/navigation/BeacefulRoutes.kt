package com.example.beaceful.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.beaceful.domain.model.Emotions
import java.time.LocalDateTime

sealed interface BeacefulRoutes {
    val icon: ImageVector
    val route: String
}

data object Home : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "home"
}
data object DiaryRoute : BeacefulRoutes {
    override val icon = Icons.Filled.CollectionsBookmark
    override val route = "diary"
}
data object NotificationRoute : BeacefulRoutes {
    override val icon = Icons.Filled.CollectionsBookmark
    override val route = "notifications"
}
data object SelectEmotionDiary : BeacefulRoutes {
    override val icon = Icons.Filled.CollectionsBookmark
    override val route = "diary_write"
}
data object WriteDiary : BeacefulRoutes {
    override val icon = Icons.Filled.CollectionsBookmark
    override val route = "diary_write/{emotion}"

    fun createRoute(emotion: Emotions, datetime: LocalDateTime): String {
        return "diary_write/${emotion.name}/$datetime"
    }
}
data object WriteDiaryExpand : BeacefulRoutes {
    override val icon = Icons.Filled.CollectionsBookmark
    override val route = "diary_write_fullscreen"
}

data object DiaryCalendar : BeacefulRoutes {
    override val icon = Icons.Filled.CollectionsBookmark
    override val route = "diary_calendar"
}

data object Doctor : BeacefulRoutes {
    override val icon = Icons.Filled.Healing
    override val route = "doctor"
}
data object Forum : BeacefulRoutes {
    override val icon = Icons.Filled.Forum
    override val route = "forum"
}
data object CommunityRoute : BeacefulRoutes {
    override val icon = Icons.Filled.Forum
    override val route = "forum_community/{communityId}/{userId}"

    fun createRoute(communityId: Int, userId: String) = "forum_community/$communityId/$userId"
}
data object Profile : BeacefulRoutes {
    override val icon = Icons.Filled.Person
    override val route = "profile"
}

data object SingleDoctorProfile : BeacefulRoutes {
    override val icon = Icons.Filled.Person
    override val route = "doctorProfile/{doctorId}"

    fun createRoute(doctorId: String) = "doctorProfile/$doctorId"
}

data object PostDetails : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "postDetails/{postId}"

    fun createRoute(postId: Int): String = "postDetails/$postId"
}

data object DiaryDetails : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "diary_details/{diaryId}"

    fun createRoute(diaryId: Int): String = "diary_details/$diaryId"
}

data object EditRoute : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "profile_edit"
}

data object EditAccountRoute : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "profile_edit_account"
}

data object ChatDetailRoute : BeacefulRoutes {
    override val icon = Icons.Filled.Chat
    override val route = "chatDetail/{userId}/{userName}"

    fun createRoute(userId: Int, userName: String): String = "chatDetail/$userId/$userName"
}
data object AppointmentRoute : BeacefulRoutes {
    override val icon = Icons.Default.CalendarToday
    override val route = "appointment"
}

data object CustomerRoute : BeacefulRoutes {
    override val icon = Icons.Default.TagFaces
    override val route = "customer"
}

data object CustomerDetails : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "customer_details/{customerId}/{isDoctorView}"

    fun createRoute(customerId: String, isDoctorView: Boolean): String {
        return "customer_details/$customerId/$isDoctorView"
    }
}

data object AppointmentDetails : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "appointment_details/{appointmentId}/{isDoctorView}"

    fun createRoute(appointmentId: Int, isDoctorView: Boolean): String {
        return "appointment_details/$appointmentId/$isDoctorView"
    }
}

data object Booking : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "doctorProfile/{doctorId}/booking"

    fun createRoute(doctorId: String) = "doctorProfile/$doctorId/booking"
}

data object ForumTab : BeacefulRoutes {
    override val icon = Icons.Default.TagFaces
    override val route = "forum?selectedTab={selectedTab}"
}

data object LoginRoute : BeacefulRoutes {
    override val icon = Icons.Default.TagFaces
    override val route = "login"
}

data object SignUpRoute : BeacefulRoutes {
    override val icon = Icons.Default.TagFaces
    override val route = "signUp"
}
data object ForgotRoute : BeacefulRoutes {
    override val icon = Icons.Default.TagFaces
    override val route = "login_forgot"
}

data object VerifyRoute : BeacefulRoutes {
    override val icon = Icons.Default.TagFaces
    override val route = "login_forgot_verify"
}


val BeacefulBottomNavPatient = listOf(DiaryRoute, Doctor, Forum, Profile, NotificationRoute)
val BeacefulBottomNavDoctor = listOf(DiaryRoute, CustomerRoute, AppointmentRoute, Forum, Profile, NotificationRoute)

