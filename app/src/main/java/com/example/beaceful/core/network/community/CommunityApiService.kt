package com.example.beaceful.core.network.community

import com.example.beaceful.domain.model.PagedResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityApiService {
    @GET("communities")
    suspend fun getAllCommunities(
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 10
    ): PagedResponse<CommunityResponse>

    @GET("communities/{community-id}")
    suspend fun getCommunityById(@Path("community-id") communityId: Int): CommunityResponse

    @GET("communities/exists/{community-id}")
    suspend fun existsById(@Path("community-id") communityId: Int): Boolean
}