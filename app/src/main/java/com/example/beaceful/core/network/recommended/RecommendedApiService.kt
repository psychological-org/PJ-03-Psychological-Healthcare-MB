package com.example.beaceful.core.network.recommended

import retrofit2.http.Body
import retrofit2.http.POST

interface RecommendationApiService {

    @POST("recommended/translate")
    suspend fun translate(@Body payload: TextInput): TranslatedResponse

    @POST("recommended/emotion")
    suspend fun analyzeEmotion(@Body payload: TranslatedResponse): EmotionOutput

    @POST("recommended/qna")
    suspend fun askQuestion(@Body payload: QuestionRequest): AnswerResponse
}