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
    override val route = "customer_details/{customerId}"

    fun createRoute(customerId: String): String = "customer_details/$customerId"
}

data object AppointmentDetails : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "appointment_details/{appointmentId}"

    fun createRoute(appointmentId: Int): String = "appointment_details/$appointmentId"
}

data object Booking : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "doctorProfile/{doctorId}/booking"

    fun createRoute(doctorId: String) = "doctorProfile/$doctorId/booking"
}



val BeacefulBottomNavPatient = listOf(AppointmentRoute, CustomerRoute, Doctor, Forum, DiaryRoute)
