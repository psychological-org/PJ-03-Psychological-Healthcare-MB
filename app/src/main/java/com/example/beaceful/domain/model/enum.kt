package com.example.beaceful.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.beaceful.R
import com.example.beaceful.ui.theme.Blue
import com.example.beaceful.ui.theme.Blue250
import com.example.beaceful.ui.theme.Orange
import com.example.beaceful.ui.theme.Orange250
import com.example.beaceful.ui.theme.Pink
import com.example.beaceful.ui.theme.Pink250
import com.example.beaceful.ui.theme.Red
import com.example.beaceful.ui.theme.Salmon
import com.example.beaceful.ui.theme.Teal
import com.example.beaceful.ui.theme.Teal250

enum class RoleType { ADMIN, DOCTOR, PATIENT }

enum class FriendStatus { ACCEPTED, DECLINED, BLOCKED, PENDING }

enum class AppointmentStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED }

enum class NotificationType { SYSTEM, FRIEND_REQUEST, APPOINTMENT, OTHER }

enum class CollectionType { MUSIC, PODCAST, OTHER }

enum class PostVisibility { PRIVATE, FRIEND, PUBLIC }

enum class Emotions(
    val backgroundColor: Color,
    val textColor: Color,
    @DrawableRes val iconRes: Int,
    @StringRes val descriptionRes: Int,
) {
    INLOVE(Pink250, Pink, R.drawable.diary_mood_inlove, R.string.in_love),
    HAPPY(Orange250, Orange, R.drawable.diary_mood_grateful, R.string.grateful),
    CONFUSE(Teal250, Teal, R.drawable.diary_mood_confused, R.string.confused),
    SAD(Blue250, Blue, R.drawable.diary_mood_worried, R.string.worried),
    ANGRY(Salmon, Red, R.drawable.diary_mood_angry, R.string.angry)
}
