package com.example.beaceful.domain.repository

import android.util.Log
import com.example.beaceful.core.network.recommended.AnswerResponse
import com.example.beaceful.core.network.recommended.Emotion
import com.example.beaceful.core.network.recommended.QuestionRequest
import com.example.beaceful.core.network.recommended.RecommendationApiService
import com.example.beaceful.core.network.recommended.SerializableEmotion
import com.example.beaceful.core.network.recommended.TextInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecommendationRepository @Inject constructor(
    private val apiService: RecommendationApiService
) {
    suspend fun getRecommendation(content: String?): Result<AnswerResponse> {
        return withContext(Dispatchers.IO) {
            try {
                if (content.isNullOrBlank()) {
                    return@withContext Result.failure(Exception("Diary content is empty"))
                }
                val textInput = TextInput(text = content)
                val translateResponse = apiService.translate(textInput)
                Log.d("RecommendationRepo", "Translation: ${translateResponse.translation}")
                val emotionResponse = apiService.analyzeEmotion(translateResponse)
                Log.d("RecommendationRepo", "Emotions: ${emotionResponse.emotions}")
                Result.success(
                    AnswerResponse(
                        answer = "",
                        emotions = emotionResponse.emotions.map {
                            SerializableEmotion(it.label, it.score)
                        },
                        negativityScore = emotionResponse.negativityScore
                    )
                )
            } catch (e: Exception) {
                Log.e("RecommendationRepo", "Error: ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun getHomeRecommendation(negativityScore: Float): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val questionRequest = QuestionRequest(negativeLevel = negativityScore)
                val qnaResponse = apiService.askQuestion(questionRequest)
                Log.d("RecommendationRepo", "QNA Response: ${qnaResponse.answer}")
                Result.success(qnaResponse.answer)
            } catch (e: Exception) {
                Log.e("RecommendationRepo", "QNA Error: ${e.message}")
                Result.failure(e)
            }
        }
    }
}