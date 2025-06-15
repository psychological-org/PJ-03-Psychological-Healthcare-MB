package com.example.beaceful.domain.repository

import com.example.beaceful.core.network.recommended.AnswerResponse
import com.example.beaceful.core.network.recommended.Emotion
import com.example.beaceful.core.network.recommended.QuestionRequest
import com.example.beaceful.core.network.recommended.RecommendationApiService
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
                val emotionResponse = apiService.analyzeEmotion(translateResponse)
                val questionRequest = QuestionRequest(negativeLevel = emotionResponse.negativityScore)
                val qnaResponse = apiService.askQuestion(questionRequest)
                Result.success(
                    AnswerResponse(
                        answer = qnaResponse.answer,
                        emotions = emotionResponse.emotions.map { Emotion(it.label, it.score) },
                        negativityScore = emotionResponse.negativityScore
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}