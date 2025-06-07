package com.example.beaceful.core.network.participant_community

import com.example.beaceful.domain.model.PagedResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ParticipantCommunityApiService {
    @GET("communities/participant_community/user/{userId}")
    suspend fun getCommunitiesByUserId(
        @Path("userId") userId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): PagedResponse<ParticipantCommunityResponse>

    @GET("communities/participant_community/community/{communityId}")
    suspend fun getParticipantsByCommunityId(
        @Path("communityId") communityId: Int,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): PagedResponse<ParticipantCommunityResponse>

    @POST("communities/participant_community")
    suspend fun joinCommunity(@Body request: ParticipantCommunityRequest): Int
}