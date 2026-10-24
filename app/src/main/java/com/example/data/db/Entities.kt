package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val isConfigured: Boolean = false,
    val language: String = "en", // "en" or "si"
    val examType: String = "AL", // "AL" or "OL"
    val alStream: String = "MATHS", // "MATHS", "BIO", "COMMERCE", "ARTS", "ETECH", "BTECH"
    val morningAlarmHour: Int = 5,
    val morningAlarmMinute: Int = 30,
    val morningAlarmEnabled: Boolean = false,
    val morningAlarmRingtoneUri: String = "",
    val morningAlarmRingtoneName: String = "Gentle Sunrise Chime",
    val focusAlertSound: String = "Bell Chime",
    val keepScreenOn: Boolean = true,
    val dailyTargetHours: Float = 6.0f
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val blueprintId: String,
    val nameEn: String,
    val nameSi: String,
    val colorHex: String,
    val isPermanent: Boolean = false,
    val isCustom: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long?, // null for Other Focus
    val subjectName: String,
    val lessonTitle: String?,
    val durationSeconds: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String, // YYYY-MM-DD
    val isOtherFocus: Boolean = false
)

@Entity(tableName = "scheduled_focus")
data class ScheduledFocusEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long?,
    val subjectName: String,
    val hour: Int,
    val minute: Int,
    val durationMinutes: Int = 60,
    val isEnabled: Boolean = true
)
