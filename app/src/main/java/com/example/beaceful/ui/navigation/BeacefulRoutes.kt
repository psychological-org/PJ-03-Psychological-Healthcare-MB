package com.example.beaceful.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument

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
data object Doctor : BeacefulRoutes {
    override val icon = Icons.Filled.Healing
    override val route = "doctor"
}
data object Forum : BeacefulRoutes {
    override val icon = Icons.Filled.Forum
    override val route = "forum"
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


val BeacefulBottomNavPatient = listOf(Home, Diary, Doctor, Forum, Profile)
