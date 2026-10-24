package com.example.data.repository

import com.example.data.db.FocusDao
import com.example.data.db.LessonEntity
import com.example.data.db.ScheduledFocusEntity
import com.example.data.db.StudySessionEntity
import com.example.data.db.SubjectEntity
import com.example.data.db.UserProfileEntity
import com.example.data.model.ALStream
import com.example.data.model.ExamType
import com.example.data.model.PresetCurriculum
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FocusRepository(private val dao: FocusDao) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val allSubjects: Flow<List<SubjectEntity>> = dao.getAllSubjects()
    val allLessons: Flow<List<LessonEntity>> = dao.getAllLessons()
    val allSessions: Flow<List<StudySessionEntity>> = dao.getAllSessions()
    val allScheduledFocus: Flow<List<ScheduledFocusEntity>> = dao.getAllScheduledFocus()

    fun getLessonsForSubject(subjectId: Long): Flow<List<LessonEntity>> = dao.getLessonsForSubject(subjectId)

    suspend fun saveProfile(profile: UserProfileEntity) {
        dao.saveUserProfile(profile)
    }

    suspend fun setupCurriculum(
        examType: ExamType,
        alStream: ALStream,
        selectedSubjectIds: Set<String>,
        customSubjectNames: List<String> = emptyList()
    ) {
        dao.clearSubjects()
        val list = mutableListOf<SubjectEntity>()
        var order = 0

        if (examType == ExamType.AL) {
            when (alStream) {
                ALStream.MATHS -> {
                    // Permanent 2 subjects
                    PresetCurriculum.mathsPermanent.forEach {
                        list.add(
                            SubjectEntity(
                                blueprintId = it.id,
                                nameEn = it.nameEn,
                                nameSi = it.nameSi,
                                colorHex = it.colorHex,
                                isPermanent = true,
                                orderIndex = order++
                            )
                        )
                    }
                    // 1 Elective
                    val elective = PresetCurriculum.mathsElectives.find { selectedSubjectIds.contains(it.id) }
                        ?: PresetCurriculum.mathsElectives.first()
                    list.add(
                        SubjectEntity(
                            blueprintId = elective.id,
                            nameEn = elective.nameEn,
                            nameSi = elective.nameSi,
                            colorHex = elective.colorHex,
                            isPermanent = false,
                            orderIndex = order++
                        )
                    )
                }
                ALStream.BIO -> {
                    // Permanent 2 subjects
                    PresetCurriculum.bioPermanent.forEach {
                        list.add(
                            SubjectEntity(
                                blueprintId = it.id,
                                nameEn = it.nameEn,
                                nameSi = it.nameSi,
                                colorHex = it.colorHex,
                                isPermanent = true,
                                orderIndex = order++
                            )
                        )
                    }
                    // 1 Elective
                    val elective = PresetCurriculum.bioElectives.find { selectedSubjectIds.contains(it.id) }
                        ?: PresetCurriculum.bioElectives.first()
                    list.add(
                        SubjectEntity(
                            blueprintId = elective.id,
                            nameEn = elective.nameEn,
                            nameSi = elective.nameSi,
                            colorHex = elective.colorHex,
                            isPermanent = false,
                            orderIndex = order++
                        )
                    )
                }
                ALStream.COMMERCE -> {
                    PresetCurriculum.commerceSubjects.filter { selectedSubjectIds.contains(it.id) }.take(3).forEach {
                        list.add(
                            SubjectEntity(
                                blueprintId = it.id,
                                nameEn = it.nameEn,
                                nameSi = it.nameSi,
                                colorHex = it.colorHex,
                                isPermanent = false,
                                orderIndex = order++
                            )
                        )
                    }
                }
                ALStream.ARTS -> {
                    PresetCurriculum.artsSubjects.filter { selectedSubjectIds.contains(it.id) }.take(3).forEach {
                        list.add(
                            SubjectEntity(
                                blueprintId = it.id,
                                nameEn = it.nameEn,
                                nameSi = it.nameSi,
                                colorHex = it.colorHex,
                                isPermanent = false,
                                orderIndex = order++
                            )
                        )
                    }
                }
                ALStream.ETECH -> {
                    PresetCurriculum.etechSubjects.filter { it.isPermanent || selectedSubjectIds.contains(it.id) }.take(3).forEach {
                        list.add(
                            SubjectEntity(
                                blueprintId = it.id,
                                nameEn = it.nameEn,
                                nameSi = it.nameSi,
                                colorHex = it.colorHex,
                                isPermanent = it.isPermanent,
                                orderIndex = order++
                            )
                        )
                    }
                }
                ALStream.BTECH -> {
                    PresetCurriculum.btechSubjects.filter { it.isPermanent || selectedSubjectIds.contains(it.id) }.take(3).forEach {
                        list.add(
                            SubjectEntity(
                                blueprintId = it.id,
                                nameEn = it.nameEn,
                                nameSi = it.nameSi,
                                colorHex = it.colorHex,
                                isPermanent = it.isPermanent,
                                orderIndex = order++
                            )
                        )
                    }
                }
            }

            // If user added custom subjects for Arts/Commerce/Tech, add them up to 3 total
            for (custom in customSubjectNames) {
                if (list.size < 3 && custom.isNotBlank()) {
                    list.add(
                        SubjectEntity(
                            blueprintId = "custom_${System.currentTimeMillis()}_$order",
                            nameEn = custom.trim(),
                            nameSi = custom.trim(),
                            colorHex = "#F43F5E",
                            isPermanent = false,
                            isCustom = true,
                            orderIndex = order++
                        )
                    )
                }
            }
        } else {
            // O/L Curriculum
            PresetCurriculum.olSubjects.forEach {
                list.add(
                    SubjectEntity(
                        blueprintId = it.id,
                        nameEn = it.nameEn,
                        nameSi = it.nameSi,
                        colorHex = it.colorHex,
                        isPermanent = it.isPermanent,
                        orderIndex = order++
                    )
                )
            }
        }

        dao.insertSubjects(list)
    }

    suspend fun addCustomSubject(name: String, colorHex: String = "#EC4899") {
        dao.insertSubject(
            SubjectEntity(
                blueprintId = "custom_${System.currentTimeMillis()}",
                nameEn = name,
                nameSi = name,
                colorHex = colorHex,
                isPermanent = false,
                isCustom = true,
                orderIndex = 99
            )
        )
    }

    suspend fun deleteSubject(id: Long) {
        dao.deleteSubject(id)
    }

    suspend fun addLesson(subjectId: Long, title: String) {
        dao.insertLesson(
            LessonEntity(
                subjectId = subjectId,
                title = title.trim(),
                isCompleted = false
            )
        )
    }

    suspend fun toggleLessonCompleted(lesson: LessonEntity) {
        dao.updateLesson(lesson.copy(isCompleted = !lesson.isCompleted))
    }

    suspend fun deleteLesson(id: Long) {
        dao.deleteLesson(id)
    }

    suspend fun logSession(
        subjectId: Long?,
        subjectName: String,
        lessonTitle: String?,
        durationSeconds: Long,
        isOtherFocus: Boolean
    ) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        dao.insertSession(
            StudySessionEntity(
                subjectId = subjectId,
                subjectName = subjectName,
                lessonTitle = lessonTitle,
                durationSeconds = durationSeconds,
                timestamp = System.currentTimeMillis(),
                dateString = today,
                isOtherFocus = isOtherFocus
            )
        )
    }

    suspend fun addScheduledFocus(item: ScheduledFocusEntity) {
        dao.insertScheduledFocus(item)
    }

    suspend fun updateScheduledFocus(item: ScheduledFocusEntity) {
        dao.updateScheduledFocus(item)
    }

    suspend fun deleteScheduledFocus(id: Long) {
        dao.deleteScheduledFocus(id)
    }
}
