package com.example.beaceful.core.network.participant_community

data class ParticipantCommunityRequest(
    val id: Int?,
    val userId: String,
    val communityId: Int
)