package com.example.beaceful.domain.repository

import android.content.Context
import android.util.Log
import com.example.beaceful.core.network.recommended.Emotion
import com.example.beaceful.core.network.recommended.SerializableEmotion
import com.example.beaceful.domain.model.Diary
import com.example.beaceful.domain.model.Emotions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import com.example.beaceful.domain.model.DumpDataProvider
import com.example.beaceful.domain.model.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import java.time.ZoneId

@Serializable
data class DiarySerializable(
    val id: Int,
    val emotion: String,
    val title: String,
    val content: String?,
    val imageUrl: String?,
    val voiceUrl: String?,
    val posterId: String,
    val createdAt: String,
    val emotionsJson: String? = null,
    val negativityScore: Float? = null
)

@Singleton
class DiaryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val DIARY_DIR = "diaries"

    private fun Diary.toSerializable(): DiarySerializable {
        val emotionsJson = emotions?.let {
            try {
                json.encodeToString(it).also { json ->
                    Log.d("DiaryRepository", "Serialized emotions: $json")
                }
            } catch (e: Exception) {
                Log.e("DiaryRepository", "Serialization error: ${e.message}")
                null
            }
        }
        return DiarySerializable(
            id = id,
            emotion = emotion.name,
            title = title,
            content = content,
            imageUrl = imageUrl,
            voiceUrl = voiceUrl,
            posterId = posterId,
            createdAt = createdAt.atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            emotionsJson = emotionsJson,
            negativityScore = negativityScore
        )
    }

    private fun DiarySerializable.toDiary(): Diary {
        val emotions = emotionsJson?.let {
            try {
                json.decodeFromString<List<SerializableEmotion>>(it).also { emotions ->
                    Log.d("DiaryRepository", "Deserialized emotions: $emotions")
                }
            } catch (e: Exception) {
                Log.e("DiaryRepository", "Error deserializing emotionsJson: ${e.message}")
                null
            }
        }
        return Diary(
            id = id,
            emotion = Emotions.valueOf(emotion),
            title = title,
            content = content,
            imageUrl = imageUrl,
            voiceUrl = voiceUrl,
            posterId = posterId,
            createdAt = try {
                LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            } catch (e1: DateTimeParseException) {
                try {
                    LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                        .toLocalDateTime()
                } catch (e2: DateTimeParseException) {
                    Log.e("DiaryRepository", "Error parsing createdAt: ${createdAt}, error: ${e2.message}")
                    LocalDateTime.of(2000, 1, 1, 0, 0) // Giá trị mặc định tạm thời
                }
            },
            emotions = emotions,
            negativityScore = negativityScore
        )
    }

    fun saveDiary(diary: Diary) {
        val diaryDir = File(context.filesDir, DIARY_DIR)
        if (!diaryDir.exists()) {
            diaryDir.mkdirs()
        }

        val file = File(diaryDir, "diary_${diary.id}.json")
        val jsonString = json.encodeToString(diary.toSerializable())
        file.writeText(jsonString)
        println("Saved diary to ${file.absolutePath}")
    }

    fun getAllDiaries(): List<Diary> {
        val diaryDir = File(context.filesDir, DIARY_DIR)
        if (!diaryDir.exists()) {
            return emptyList()
        }

        return diaryDir.listFiles()
            ?.filter { it.name.endsWith(".json") }
            ?.mapNotNull { file ->
                try {
                    val jsonString = file.readText()
                    val diarySerializable = json.decodeFromString<DiarySerializable>(jsonString)
                    diarySerializable.toDiary()
                } catch (e: Exception) {
                    println("Error reading diary from ${file.name}: ${e.message}")
                    null
                }
            }
            ?.sortedByDescending { it.createdAt }
            ?: emptyList()
    }

    fun getDiaryById(id: Int): Diary? {
        val diaryDir = File(context.filesDir, DIARY_DIR)
        val file = File(diaryDir, "diary_${id}.json")
        return if (file.exists()) {
            try {
                val jsonString = file.readText()
                val diarySerializable = json.decodeFromString<DiarySerializable>(jsonString)
                diarySerializable.toDiary()
            } catch (e: Exception) {
                println("Error reading diary $id: ${e.message}")
                null
            }
        } else {
            null
        }
    }

    fun getDiariesInMonth(date: LocalDateTime): List<Diary> {
        val year = date.year
        val month = date.month
        return getAllDiaries().filter {
            it.createdAt.year == year && it.createdAt.month == month
        }.sortedByDescending { it.createdAt }
    }

    fun getDiariesInWeek(date: LocalDate): List<Diary> {
        val monday = date.with(java.time.DayOfWeek.MONDAY)
        val sunday = monday.plusDays(6)
        return getAllDiaries().filter {
            val day = it.createdAt.toLocalDate()
            day in monday..sunday
        }
    }

    fun getDiariesOnDate(date: LocalDate): List<Diary> =
        getAllDiaries().filter { it.createdAt.toLocalDate() == date }

    fun deleteDiary(diaryId: Int): Boolean {
        val diaryDir = File(context.filesDir, DIARY_DIR)
        val file = File(diaryDir, "diary_${diaryId}.json")
        return if (file.exists()) {
            file.delete()
            println("Deleted diary $diaryId")
            true
        } else {
            println("Diary $diaryId not found")
            false
        }
    }

    fun updateDiary(diary: Diary) {
        val diaryDir = File(context.filesDir, DIARY_DIR)
        if (!diaryDir.exists()) {
            diaryDir.mkdirs()
        }

        val file = File(diaryDir, "diary_${diary.id}.json")
        val jsonString = json.encodeToString(diary.toSerializable())
        file.writeText(jsonString)
        println("Updated diary to ${file.absolutePath}")
    }

    fun getDiariesOnDate(date: LocalDateTime): List<Diary> =
        getAllDiaries().filter { it.createdAt.toLocalDate() == date.toLocalDate() }

    fun getUserById(userId: String): User? =
        DumpDataProvider.listUser.find { it.id == userId }
}