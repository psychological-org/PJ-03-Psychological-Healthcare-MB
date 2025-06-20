package com.example.beaceful.core.network

import android.content.Context
import android.util.Log
import com.example.beaceful.BuildConfig
import com.example.beaceful.core.network.appointment.AppointmentApiService
import com.example.beaceful.core.network.appointment.AppointmentDeserializer
import com.example.beaceful.core.network.auth.AuthApiService
import com.example.beaceful.core.network.auth.AuthDataStore
import com.example.beaceful.core.network.collection.CollectionApiService
import com.example.beaceful.core.network.collection.CollectionSeenApiService
import com.example.beaceful.core.network.collection.CollectionTypeDeserializer
import com.example.beaceful.core.network.comment.CommentApiService
import com.example.beaceful.core.network.community.CommunityApiService
import com.example.beaceful.core.network.fcm_token.FcmTokenApiService
import com.example.beaceful.core.network.notification.UserNotificationApiService
import com.example.beaceful.core.network.participant_community.ParticipantCommunityApiService
import com.example.beaceful.core.network.post.PostApiService
import com.example.beaceful.core.network.recommended.RecommendationApiService
import com.example.beaceful.core.network.topic.TopicApiService
import com.example.beaceful.core.network.user.UserApiService
import com.example.beaceful.domain.model.Appointment
import com.example.beaceful.domain.model.CollectionType
import com.example.beaceful.domain.repository.AuthRepository
import com.example.beaceful.domain.repository.CollectionRepository
import com.example.beaceful.domain.repository.CollectionSeenRepository
import com.example.beaceful.domain.repository.CommunityRepository
import com.example.beaceful.domain.repository.PostRepository
import com.example.beaceful.domain.repository.TopicRepository
import com.example.beaceful.domain.repository.UserNotificationRepository
import com.example.beaceful.domain.repository.UserNotificationRepositoryImpl
import com.example.beaceful.domain.repository.UserRepository
import com.example.beaceful.ui.viewmodel.AuthViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val GATEWAY_BASE_URL = "http://127.0.0.1:8222/api/v1/"
    private const val KEYCLOAK_BASE_URL = "http://127.0.0.1:9098/"
//    private const val BEARER_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ4YWlxa01lN0E3MDJVRENJcldrV3ZxV21ZZndudGo5RF9xMzFYTUZEdmhJIn0.eyJleHAiOjE3NDk0NDQ5MTksImlhdCI6MTc0OTQzNDExOSwianRpIjoiOTU3YWM2YmYtMTJjZC00YzkwLTg1YzgtNDdmZjdmZThjNWVlIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODAvcmVhbG1zL21pY3JvLXNlcnZpY2VzIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImYzOTdjNDgwLTg1ZmYtNDc0Ni1iZjliLTViMGEwODU1OTA3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImF1dGhzZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjNkNDMwYjRhLTVmNWQtNGM2NS1iMDczLTdhODgyZDYwYTYzNyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInBhdGllbnQiLCJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtbWljcm8tc2VydmljZXMiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6IjNkNDMwYjRhLTVmNWQtNGM2NS1iMDczLTdhODgyZDYwYTYzNyIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiTmd1eeG7hW4gWHXDom4gUXVhbmciLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJwYXRpZW50MSIsImdpdmVuX25hbWUiOiJOZ3V54buFbiIsImZhbWlseV9uYW1lIjoiWHXDom4gUXVhbmciLCJlbWFpbCI6InBhdGllbnQxQGV4YW1wbGUuY29tIn0.mx_l7sKqePek6U-P_5Upyn1IMdDyX9PA7Y8AZBX2ImZVqRK6x5M0Y5MsJkyRchEPfh-KEhJ3N6k9rdH9bc0Zrlk99FCIyjG0dGMkrFmfV4T0s9e3N_CPx3TX9qZJUtlCbX3xjTz2OHCghmV1rbx2-wZ8IksNT98zzY1KyEp0nX1sSX772yfwN7k6k5IpbQCU2IV_q1lb0VCS_fb8vtlG63tovACK44nog6aZ60sFsl_6yOYyIhrfOHM0ktPZzQN5c11td97JJn-mH3aS09bpOe5lR99Uc_rA0Ipd3jVhx1KkZmO4vbx79zt6YvZ7o_RCQNpOI553VC1vIMUQ0c16Mw"

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
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        authRepository: AuthRepository // Sử dụng AuthRepository thay vì AuthApiService
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val token = runBlocking { AuthDataStore.getToken(context) }
                Log.d("NetworkModule", "Using access token: $token")
                val requestBuilder = chain.request().newBuilder()
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                val response = chain.proceed(requestBuilder.build())
                if (response.code == 401) {
                    val refreshToken = runBlocking { AuthDataStore.getRefreshToken(context) }
                    if (refreshToken != null) {
                        val newResponse = runBlocking {
                            try {
                                val refreshResponse = authRepository.refreshToken(
                                    clientId = "authservice",
                                    refreshToken = refreshToken,
                                    clientSecret = BuildConfig.CLIENT_SECRET
                                )
                                AuthDataStore.saveTokens(context, refreshResponse.token, refreshResponse.refreshToken)
                                refreshResponse
                            } catch (e: Exception) {
                                Log.e("NetworkModule", "Refresh token failed: ${e.message}", e)
                                // Xóa token nếu làm mới thất bại
                                runBlocking { AuthDataStore.clearTokens(context) }
                                null
                            }
                        }
                        if (newResponse != null) {
                            val newRequest = chain.request().newBuilder()
                                .header("Authorization", "Bearer ${newResponse.token}")
                                .build()
                            return@addInterceptor chain.proceed(newRequest)
                        }
                    }
                    // Nếu không thể làm mới, trả về response 401
                    response
                } else {
                    response
                }
            }
            .connectTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("KeycloakRetrofit")
    fun provideKeycloakRetrofit(gson: Gson): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(KEYCLOAK_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GATEWAY_BASE_URL)
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
    fun provideUserRepository(userApiService: UserApiService, authApiService: AuthApiService): UserRepository {
        return UserRepository(userApiService, authApiService)
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

    @Provides
    @Singleton
    fun provideAuthApiService(@Named("KeycloakRetrofit") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authApiService: AuthApiService): AuthRepository {
        return AuthRepository(authApiService)
    }

    @Provides
    @Singleton
    fun provideRecommendationApiService(retrofit: Retrofit): RecommendationApiService {
        return retrofit.create(RecommendationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFcmTokenApiService(retrofit: Retrofit): FcmTokenApiService {
        return retrofit.create(FcmTokenApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserNotificationApiService(retrofit: Retrofit): UserNotificationApiService {
        return retrofit.create(UserNotificationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserNotificationRepository(userNotificationApiService: UserNotificationApiService): UserNotificationRepository {
        return UserNotificationRepositoryImpl(userNotificationApiService)
    }
}