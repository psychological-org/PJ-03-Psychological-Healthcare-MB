package com.example.beaceful.core.network.recommended

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TextInput(
    @SerializedName("text") val text: String
) : Serializable

data class TranslatedResponse(
    @SerializedName("translation") val translation: String
) : Serializable

// Response cho /emotion
data class EmotionOutput(
    @SerializedName("emotions") val emotions: List<EmotionScore>,
    @SerializedName("negativity_score") val negativityScore: Float
) : Serializable

data class EmotionScore(
    @SerializedName("label") val label: String,
    @SerializedName("score") val score: Float
) : Serializable

// Request và Response cho /qna
data class QuestionRequest(
    @SerializedName("negative_level") val negativeLevel: Float
) : Serializable

data class AnswerResponse(
    val answer: String,
    val emotions: List<Emotion>,
    val negativityScore: Float
): Serializable

data class Emotion(
    val label: String,
    val score: Float
)