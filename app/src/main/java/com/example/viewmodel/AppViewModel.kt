package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.LessonEntity
import com.example.data.db.ScheduledFocusEntity
import com.example.data.db.StudySessionEntity
import com.example.data.db.SubjectEntity
import com.example.data.db.UserProfileEntity
import com.example.data.model.ALStream
import com.example.data.model.AppLanguage
import com.example.data.model.ExamType
import com.example.data.model.PresetCurriculum
import com.example.data.repository.FocusRepository
import com.example.localization.Strings
import com.example.service.AlarmHelper
import com.example.service.TimerStateManager
import com.example.ui.components.DayStudyStat
import com.example.ui.components.SubjectStudyStat
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FocusRepository
    init {
        val db = AppDatabase.getDatabase(application)
        repository = FocusRepository(db.focusDao())
        AlarmHelper.createNotificationChannels(application)
    }

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val subjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lessons: StateFlow<List<LessonEntity>> = repository.allLessons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<StudySessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scheduledFocusList: StateFlow<List<ScheduledFocusEntity>> = repository.allScheduledFocus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Language State
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.saveProfile(current.copy(language = lang))
        }
    }

    // Active Focus Timer State synced with TimerStateManager
    private val _timerRemainingSeconds = MutableStateFlow(25L * 60L)
    val timerRemainingSeconds: StateFlow<Long> = _timerRemainingSeconds.asStateFlow()

    private val _timerTotalSeconds = MutableStateFlow(25L * 60L)
    val timerTotalSeconds: StateFlow<Long> = _timerTotalSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _selectedSubject = MutableStateFlow<SubjectEntity?>(null)
    val selectedSubject: StateFlow<SubjectEntity?> = _selectedSubject.asStateFlow()

    private val _isOtherFocusMode = MutableStateFlow(false)
    val isOtherFocusMode: StateFlow<Boolean> = _isOtherFocusMode.asStateFlow()

    private val _selectedLesson = MutableStateFlow<LessonEntity?>(null)
    val selectedLesson: StateFlow<LessonEntity?> = _selectedLesson.asStateFlow()

    private val _keepScreenOn = MutableStateFlow(true)
    val keepScreenOn: StateFlow<Boolean> = _keepScreenOn.asStateFlow()

    init {
        TimerStateManager.initialize(application)

        TimerStateManager.onTimerFinishedListener = { data ->
            viewModelScope.launch {
                val actualDuration = if (data.totalSeconds > 0) data.totalSeconds else 25L * 60L
                val subjectName = if (data.isOtherFocus) {
                    Strings.get("other_focus_mode", _currentLanguage.value)
                } else {
                    data.subjectName.ifBlank { "General Study" }
                }

                repository.logSession(
                    subjectId = data.subjectId,
                    subjectName = subjectName,
                    lessonTitle = data.lessonTitle,
                    durationSeconds = actualDuration,
                    isOtherFocus = data.isOtherFocus
                )

                AlarmHelper.triggerFocusFinishedNotification(
                    getApplication(),
                    subjectName,
                    actualDuration / 60L
                )
                val profile = userProfile.value
                val alertTone = profile?.focusAlertSound ?: "Bell Chime"
                AlarmHelper.playPreviewTone(getApplication(), alertTone)
                delay(1500)
                AlarmHelper.stopPreview()
            }
        }

        viewModelScope.launch {
            TimerStateManager.timerState.collect { state ->
                _timerRemainingSeconds.value = state.remainingSeconds
                _timerTotalSeconds.value = state.totalSeconds
                _isTimerRunning.value = state.isRunning
                if (state.isOtherFocus) {
                    _isOtherFocusMode.value = true
                    _selectedSubject.value = null
                }
            }
        }

        viewModelScope.launch {
            userProfile.collect { profile ->
                if (profile != null) {
                    _currentLanguage.value = profile.language
                    _keepScreenOn.value = profile.keepScreenOn
                }
            }
        }

        viewModelScope.launch {
            subjects.collect { subList ->
                if (_selectedSubject.value == null && subList.isNotEmpty()) {
                    val savedState = TimerStateManager.timerState.value
                    val match = if (savedState.subjectId != null) subList.find { it.id == savedState.subjectId } else null
                    _selectedSubject.value = match ?: subList.first()
                }
            }
        }
    }

    fun selectSubject(subject: SubjectEntity?) {
        _selectedSubject.value = subject
        _isOtherFocusMode.value = false
        _selectedLesson.value = null
    }

    fun enableOtherFocusMode() {
        _isOtherFocusMode.value = true
        _selectedSubject.value = null
        _selectedLesson.value = null
    }

    fun selectLesson(lesson: LessonEntity?) {
        _selectedLesson.value = lesson
    }

    fun setTimerPreset(minutes: Int) {
        if (!_isTimerRunning.value) {
            val secs = minutes * 60L
            TimerStateManager.setDuration(secs)
        }
    }

    fun setCustomTimerMinutes(minutes: Int) {
        if (!_isTimerRunning.value && minutes > 0) {
            val secs = minutes * 60L
            TimerStateManager.setDuration(secs)
        }
    }

    fun startTimer() {
        val subjectName = if (_isOtherFocusMode.value) {
            Strings.get("other_focus_mode", _currentLanguage.value)
        } else {
            val s = _selectedSubject.value
            if (_currentLanguage.value == "si") s?.nameSi ?: s?.nameEn ?: "General Study" else s?.nameEn ?: "General Study"
        }
        val subId = if (_isOtherFocusMode.value) null else _selectedSubject.value?.id
        val lesson = _selectedLesson.value?.title

        TimerStateManager.startTimer(
            context = getApplication(),
            subjectId = subId,
            subjectName = subjectName,
            lessonTitle = lesson,
            isOtherFocus = _isOtherFocusMode.value
        )
    }

    fun pauseTimer() {
        TimerStateManager.pauseTimer(getApplication())
    }

    fun resetTimer() {
        TimerStateManager.resetTimer(getApplication())
    }

    fun finishSessionAndLog() {
        TimerStateManager.onFinished(getApplication())
    }

    fun toggleKeepScreenOn(enabled: Boolean) {
        _keepScreenOn.value = enabled
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.saveProfile(current.copy(keepScreenOn = enabled))
        }
    }

    // Onboarding / Curriculum Setup
    fun completeOnboarding(
        language: String,
        examType: ExamType,
        alStream: ALStream,
        selectedSubjectIds: Set<String>,
        customSubjects: List<String>
    ) {
        viewModelScope.launch {
            repository.setupCurriculum(
                examType = examType,
                alStream = alStream,
                selectedSubjectIds = selectedSubjectIds,
                customSubjectNames = customSubjects
            )

            val profile = UserProfileEntity(
                id = 1,
                isConfigured = true,
                language = language,
                examType = examType.code,
                alStream = alStream.code,
                morningAlarmHour = 5,
                morningAlarmMinute = 30,
                morningAlarmEnabled = false,
                keepScreenOn = true,
                dailyTargetHours = 6.0f
            )
            repository.saveProfile(profile)
            _currentLanguage.value = language
        }
    }

    // Subject & Lesson Management
    fun addLesson(subjectId: Long, title: String) {
        viewModelScope.launch {
            repository.addLesson(subjectId, title)
        }
    }

    fun toggleLessonCompleted(lesson: LessonEntity) {
        viewModelScope.launch {
            repository.toggleLessonCompleted(lesson)
        }
    }

    fun deleteLesson(id: Long) {
        viewModelScope.launch {
            repository.deleteLesson(id)
        }
    }

    fun addCustomSubject(name: String) {
        viewModelScope.launch {
            repository.addCustomSubject(name)
        }
    }

    fun deleteSubject(id: Long) {
        viewModelScope.launch {
            repository.deleteSubject(id)
        }
    }

    // Morning Alarm & Sound Settings
    fun updateMorningAlarm(hour: Int, minute: Int, enabled: Boolean, soundName: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val updated = current.copy(
                morningAlarmHour = hour,
                morningAlarmMinute = minute,
                morningAlarmEnabled = enabled,
                morningAlarmRingtoneName = soundName
            )
            repository.saveProfile(updated)
            AlarmHelper.setMorningAlarm(getApplication(), hour, minute, enabled)
        }
    }

    fun updateFocusAlertSound(soundName: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.saveProfile(current.copy(focusAlertSound = soundName))
        }
    }

    fun previewTone(toneName: String) {
        AlarmHelper.playPreviewTone(getApplication(), toneName)
    }

    fun stopTone() {
        AlarmHelper.stopPreview()
    }

    // Scheduled Focus Sessions
    fun addScheduledFocus(subjectId: Long?, subjectName: String, hour: Int, minute: Int, durationMinutes: Int) {
        viewModelScope.launch {
            repository.addScheduledFocus(
                ScheduledFocusEntity(
                    subjectId = subjectId,
                    subjectName = subjectName,
                    hour = hour,
                    minute = minute,
                    durationMinutes = durationMinutes,
                    isEnabled = true
                )
            )
        }
    }

    fun toggleScheduledFocus(item: ScheduledFocusEntity) {
        viewModelScope.launch {
            repository.updateScheduledFocus(item.copy(isEnabled = !item.isEnabled))
        }
    }

    fun deleteScheduledFocus(id: Long) {
        viewModelScope.launch {
            repository.deleteScheduledFocus(id)
        }
    }

    // Analytics Aggregation Helpers
    fun getSubjectStats(sessionList: List<StudySessionEntity>, subList: List<SubjectEntity>, lang: String): List<SubjectStudyStat> {
        val result = mutableListOf<SubjectStudyStat>()

        // Core subjects
        subList.forEach { subject ->
            val duration = sessionList.filter { !it.isOtherFocus && it.subjectId == subject.id }
                .sumOf { it.durationSeconds }

            val color = try {
                Color(android.graphics.Color.parseColor(subject.colorHex))
            } catch (_: Exception) {
                NeonCyan
            }

            val name = if (lang == "si") subject.nameSi else subject.nameEn
            result.add(
                SubjectStudyStat(
                    subjectId = subject.id,
                    name = name,
                    color = color,
                    durationSeconds = duration,
                    isOtherFocus = false
                )
            )
        }

        // Other Focus Time
        val otherDuration = sessionList.filter { it.isOtherFocus }.sumOf { it.durationSeconds }
        if (otherDuration > 0 || result.isEmpty()) {
            result.add(
                SubjectStudyStat(
                    subjectId = null,
                    name = Strings.get("other_focus_time_label", lang),
                    color = ElectricPurple,
                    durationSeconds = otherDuration,
                    isOtherFocus = true
                )
            )
        }

        return result
    }

    fun getWeeklyDayStats(sessionList: List<StudySessionEntity>, lang: String): List<DayStudyStat> {
        val result = mutableListOf<DayStudyStat>()
        val cal = Calendar.getInstance()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val dayNamesEn = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val dayNamesSi = listOf("ඉරිදා", "සඳුදා", "අඟහ", "බදාදා", "බ්‍රහස්", "සිකු", "සෙන")

        for (i in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -i)
            val dStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
            val dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sun
            val label = if (lang == "si") dayNamesSi[dayOfWeek] else dayNamesEn[dayOfWeek]

            val daySeconds = sessionList.filter { it.dateString == dStr }.sumOf { it.durationSeconds }
            val hours = daySeconds.toFloat() / 3600f

            result.add(
                DayStudyStat(
                    dayLabel = label,
                    dateString = dStr,
                    hours = hours,
                    isToday = dStr == todayStr
                )
            )
        }

        return result
    }

    fun generateRecommendations(
        sessionList: List<StudySessionEntity>,
        subList: List<SubjectEntity>,
        lang: String,
        streamCode: String
    ): List<String> {
        val tips = mutableListOf<String>()
        val totalSecs = sessionList.sumOf { it.durationSeconds }

        if (totalSecs == 0L) {
            tips.add(Strings.get("rec_morning_tip", lang))
            if (streamCode == "MATHS") tips.add(Strings.get("rec_maths_tip", lang))
            if (streamCode == "BIO") tips.add(Strings.get("rec_bio_tip", lang))
            return tips
        }

        // Check for neglected subjects (< 18% of core study time)
        val coreSessions = sessionList.filter { !it.isOtherFocus }
        val coreTotalSecs = coreSessions.sumOf { it.durationSeconds }

        if (coreTotalSecs > 0 && subList.size >= 2) {
            var foundNeglected = false
            subList.forEach { s ->
                val sDuration = coreSessions.filter { it.subjectId == s.id }.sumOf { it.durationSeconds }
                val pct = (sDuration.toFloat() / coreTotalSecs.toFloat()) * 100f
                if (pct < 18f) {
                    val sName = if (lang == "si") s.nameSi else s.nameEn
                    tips.add(Strings.get("rec_neglected_prefix", lang) + sName + Strings.get("rec_neglected_suffix", lang))
                    foundNeglected = true
                }
            }
            if (!foundNeglected) {
                tips.add(Strings.get("rec_balanced", lang))
            }
        }

        // Other focus check
        val otherSecs = sessionList.filter { it.isOtherFocus }.sumOf { it.durationSeconds }
        if (otherSecs > 0) {
            tips.add(Strings.get("rec_other_focus_balanced", lang))
        }

        // Stream specific tip
        when (streamCode) {
            "MATHS" -> tips.add(Strings.get("rec_maths_tip", lang))
            "BIO" -> tips.add(Strings.get("rec_bio_tip", lang))
            else -> tips.add(Strings.get("rec_morning_tip", lang))
        }

        return tips
    }
}
