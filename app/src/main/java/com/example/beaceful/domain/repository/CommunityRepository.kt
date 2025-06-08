package com.example.beaceful.domain.repository

import com.example.beaceful.core.network.community.CommunityApiService
import com.example.beaceful.core.network.community.CommunityResponse
import com.example.beaceful.core.network.participant_community.ParticipantCommunityApiService
import com.example.beaceful.core.network.participant_community.ParticipantCommunityRequest
import com.example.beaceful.domain.model.Community
import javax.inject.Inject

class CommunityRepository @Inject constructor(
    private val communityApiService: CommunityApiService,
    private val participantCommunityApiService: ParticipantCommunityApiService
) {
    suspend fun getAllCommunities(page: Int = 0, limit: Int = 10): List<Community> {
        return communityApiService.getAllCommunities(page, limit).content.map { it.toCommunity() }
    }

    suspend fun getCommunityById(communityId: Int): Community? {
        return try {
            communityApiService.getCommunityById(communityId).toCommunity()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserCommunityIds(userId: String): List<Int> {
        return try {
            participantCommunityApiService.getCommunitiesByUserId(userId)
                .content
                .map { it.communityId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCommunityMembers(communityId: Int): List<String> {
        return try {
            participantCommunityApiService.getParticipantsByCommunityId(communityId)
                .content
                .map { it.userId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun joinCommunity(userId: String, communityId: Int): Int {
        val request = ParticipantCommunityRequest(
            id = null,
            userId = userId,
            communityId = communityId
        )
        return participantCommunityApiService.joinCommunity(request)
    }
}

fun CommunityResponse.toCommunity(): Community {
    return Community(
        id = id,
        name = name,
        content = content,
        imageUrl = avatarUrl,
        adminId = adminId
    )
}