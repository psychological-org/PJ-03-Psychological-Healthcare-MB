package com.example.beaceful.core.network.participant_community

import com.example.beaceful.domain.model.User

data class ParticipantCommunityResponse(
    val id: Int,
    val userId: String,
    val communityId: Int,
    val user: User?
)