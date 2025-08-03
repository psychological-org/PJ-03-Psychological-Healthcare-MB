package com.example.beaceful.core.network.recommended

import com.google.gson.annotations.SerializedName

data class TextInput(
    @SerializedName("text") val text: String
) : java.io.Serializable

data class TranslatedResponse(
    @SerializedName("translation") val translation: String
) : java.io.Serializable

data class EmotionOutput(
    @SerializedName("emotions") val emotions: List<EmotionScore>,
    @SerializedName("negativity_score") val negativityScore: Float
) : java.io.Serializable

data class EmotionScore(
    @SerializedName("label") val label: String,
    @SerializedName("score") val score: Float
) : java.io.Serializable

data class QuestionRequest(
    @SerializedName("negative_level") val negativeLevel: Float
) : java.io.Serializable

data class AnswerResponse(
    val answer: String,
    val emotions: List<SerializableEmotion>,
    val negativityScore: Float
) : java.io.Serializable

data class Emotion(
    @SerializedName("label") val label: String,
    @SerializedName("score") val score: Float
) : java.io.Serializable

@kotlinx.serialization.Serializable
data class SerializableEmotion(
    val label: String,
    val score: Float
)