package com.example.beaceful.core.network

import com.example.beaceful.core.network.appointment.AppointmentApiService
import com.example.beaceful.core.network.appointment.AppointmentDeserializer
import com.example.beaceful.core.network.collection.CollectionApiService
import com.example.beaceful.core.network.collection.CollectionSeenApiService
import com.example.beaceful.core.network.collection.CollectionTypeDeserializer
import com.example.beaceful.core.network.comment.CommentApiService
import com.example.beaceful.core.network.community.CommunityApiService
import com.example.beaceful.core.network.participant_community.ParticipantCommunityApiService
import com.example.beaceful.core.network.post.PostApiService
import com.example.beaceful.core.network.topic.TopicApiService
import com.example.beaceful.core.network.user.UserApiService
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.CollectionType
import com.example.beaceful.domain.repository.CollectionRepository
import com.example.beaceful.domain.repository.CollectionSeenRepository
import com.example.beaceful.domain.repository.CommunityRepository
import com.example.beaceful.domain.repository.PostRepository
import com.example.beaceful.domain.repository.TopicRepository
import com.example.beaceful.domain.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:8222/api/v1/"
    private const val BEARER_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ4YWlxa01lN0E3MDJVRENJcldrV3ZxV21ZZndudGo5RF9xMzFYTUZEdmhJIn0.eyJleHAiOjE3NDkyNzc3MDksImlhdCI6MTc0OTI2NjkwOSwianRpIjoiMWYwM2NkYmItMWMyYS00YTRjLTg4ZmQtYmVkMDNiNzEzODliIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL21pY3JvLXNlcnZpY2VzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImYzOTdjNDgwLTg1ZmYtNDc0Ni1iZjliLTViMGEwODU1OTA3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImF1dGhzZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjVkOGExMzhkLThkOWEtNDNlMS1iMThmLWU4NTlkYTcwYzE2ZCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInBhdGllbnQiLCJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtbWljcm8tc2VydmljZXMiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6IjVkOGExMzhkLThkOWEtNDNlMS1iMThmLWU4NTlkYTcwYzE2ZCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiTmd1eeG7hW4gWHXDom4gUXVhbmciLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJwYXRpZW50MSIsImdpdmVuX25hbWUiOiJOZ3V54buFbiIsImZhbWlseV9uYW1lIjoiWHXDom4gUXVhbmciLCJlbWFpbCI6InBhdGllbnQxQGV4YW1wbGUuY29tIn0.jQHwW9zVEg5mF2VCiwDS3UNxM_h_HDTOi0Gxs2UWwVkcqEmiA8l-k452eVgqYxGypSSQazHnZFDslITNKTceMm5x_n5olgtUFS4_EWvngvuazchaFiam4EP4ecc8Wx5dbTbuNmQKOkCr89sLd6V6_L1TKT4DofsTnLBjvQokHHTECxfom-yl_r7xp025rYo3Hgb6q972DLgCdUV_nWFa4BdQDzZERIJBrapcpuwEP5fjYGPbIbFBcF1r0SlVZnV8wTR1thSvf4N4J84HCe_NnWPRdxKZs7vtXj42hKgmeLOW9GClUTN1uSv8jBNC8jXJS2UYE5_Z-ySGvTEDYRfQEg"

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Appointment::class.java, AppointmentDeserializer())
            .registerTypeAdapter(CollectionType::class.java, CollectionTypeDeserializer())
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $BEARER_TOKEN")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(provideGson()))
            .build()
    }

    @Provides
    @Singleton
    fun provideAppointmentApiService(retrofit: Retrofit): AppointmentApiService {
        return retrofit.create(AppointmentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userApiService: UserApiService): UserRepository {
        return UserRepository(userApiService)
    }

    @Provides
    @Singleton
    fun providePostApiService(retrofit: Retrofit): PostApiService {
        return retrofit.create(PostApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCommentApiService(retrofit: Retrofit): CommentApiService {
        return retrofit.create(CommentApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePostRepository(
        userRepository: UserRepository,
        postApiService: PostApiService,
        commentApiService: CommentApiService
    ): PostRepository {
        return PostRepository(userRepository, postApiService, commentApiService)
    }

    @Provides
    @Singleton
    fun provideCommunityApiService(retrofit: Retrofit): CommunityApiService {
        return retrofit.create(CommunityApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideParticipantCommunityApiService(retrofit: Retrofit): ParticipantCommunityApiService {
        return retrofit.create(ParticipantCommunityApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCommunityRepository(
        communityApiService: CommunityApiService,
        participantCommunityApiService: ParticipantCommunityApiService
    ): CommunityRepository {
        return CommunityRepository(communityApiService, participantCommunityApiService)
    }

    @Provides
    @Singleton
    fun provideTopicApiService(retrofit: Retrofit): TopicApiService {
        return retrofit.create(TopicApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTopicRepository(topicApiService: TopicApiService): TopicRepository {
        return TopicRepository(topicApiService)
    }

    @Provides
    @Singleton
    fun provideCollectionApiService(retrofit: Retrofit): CollectionApiService {
        return retrofit.create(CollectionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCollectionSeenApiService(retrofit: Retrofit): CollectionSeenApiService {
        return retrofit.create(CollectionSeenApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCollectionRepository(collectionApiService: CollectionApiService): CollectionRepository {
        return CollectionRepository(collectionApiService)
    }

    @Provides
    @Singleton
    fun provideCollectionSeenRepository(collectionSeenApiService: CollectionSeenApiService): CollectionSeenRepository {
        return CollectionSeenRepository(collectionSeenApiService)
    }
}