package com.example.beaceful.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.beaceful.domain.model.Emotions

sealed interface BeacefulRoutes {
    val icon: ImageVector
    val route: String
}

data object Home : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "home"
}
data object Diary : BeacefulRoutes {
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

    fun createRoute(emotion: Emotions) : String = "diary_write/${emotion.name}"
}
data object WriteDiaryExpand : BeacefulRoutes {
    override val icon = Icons.Filled.CollectionsBookmark
    override val route = "diary_write_fullscreen"
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
    override val route = "forum_community/{communityId}"

    fun createRoute(communityId: Int) = "forum_community/$communityId"
}
data object Profile : BeacefulRoutes {
    override val icon = Icons.Filled.Person
    override val route = "profile"
}

data object SingleDoctorProfile : BeacefulRoutes {
    override val icon = Icons.Filled.Person
    override val route = "doctorProfile/{doctorId}"

    fun createRoute(doctorId: Int) = "doctorProfile/$doctorId"
}

data object PostDetails : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "postDetails/{postId}"

    fun createRoute(postId: Int): String = "postDetails/$postId"
}

data object EditRoute : BeacefulRoutes {
    override val icon = Icons.Filled.Home
    override val route = "profile_edit"
}


val BeacefulBottomNavPatient = listOf(Home, Diary, Doctor, Forum, Profile)
