package com.example.service

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ActiveTimerData(
    val isRunning: Boolean = false,
    val endTimeMs: Long = 0L,
    val remainingSeconds: Long = 25L * 60L,
    val totalSeconds: Long = 25L * 60L,
    val subjectId: Long? = null,
    val subjectName: String = "",
    val lessonTitle: String? = null,
    val isOtherFocus: Boolean = false
)

object TimerStateManager {

    private const val PREFS_NAME = "focus_timer_prefs"
    private const val KEY_IS_RUNNING = "is_running"
    private const val KEY_END_TIME_MS = "end_time_ms"
    private const val KEY_REMAINING_SECS = "remaining_secs"
    private const val KEY_TOTAL_SECS = "total_secs"
    private const val KEY_SUBJECT_ID = "subject_id"
    private const val KEY_SUBJECT_NAME = "subject_name"
    private const val KEY_LESSON_TITLE = "lesson_title"
    private const val KEY_IS_OTHER_FOCUS = "is_other_focus"

    private lateinit var prefs: SharedPreferences
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var tickerJob: Job? = null

    private val _timerState = MutableStateFlow(ActiveTimerData())
    val timerState: StateFlow<ActiveTimerData> = _timerState.asStateFlow()

    var onTimerFinishedListener: ((ActiveTimerData) -> Unit)? = null

    fun initialize(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFromPrefs()
        startTickerIfNeeded(context.applicationContext)
    }

    private fun loadFromPrefs() {
        if (!::prefs.isInitialized) return

        val isRunning = prefs.getBoolean(KEY_IS_RUNNING, false)
        val endTimeMs = prefs.getLong(KEY_END_TIME_MS, 0L)
        val totalSecs = prefs.getLong(KEY_TOTAL_SECS, 25L * 60L)
        val savedRemainingSecs = prefs.getLong(KEY_REMAINING_SECS, 25L * 60L)
        val subIdLong = prefs.getLong(KEY_SUBJECT_ID, -1L)
        val subId = if (subIdLong == -1L) null else subIdLong
        val subName = prefs.getString(KEY_SUBJECT_NAME, "") ?: ""
        val lessonTitle = prefs.getString(KEY_LESSON_TITLE, null)
        val isOther = prefs.getBoolean(KEY_IS_OTHER_FOCUS, false)

        val currentRemaining = if (isRunning) {
            val now = System.currentTimeMillis()
            ((endTimeMs - now) / 1000L).coerceAtLeast(0L)
        } else {
            savedRemainingSecs
        }

        _timerState.value = ActiveTimerData(
            isRunning = isRunning && currentRemaining > 0,
            endTimeMs = endTimeMs,
            remainingSeconds = currentRemaining,
            totalSeconds = totalSecs,
            subjectId = subId,
            subjectName = subName,
            lessonTitle = lessonTitle,
            isOtherFocus = isOther
        )
    }

    private fun saveToPrefs(data: ActiveTimerData) {
        if (!::prefs.isInitialized) return
        prefs.edit()
            .putBoolean(KEY_IS_RUNNING, data.isRunning)
            .putLong(KEY_END_TIME_MS, data.endTimeMs)
            .putLong(KEY_REMAINING_SECS, data.remainingSeconds)
            .putLong(KEY_TOTAL_SECS, data.totalSeconds)
            .putLong(KEY_SUBJECT_ID, data.subjectId ?: -1L)
            .putString(KEY_SUBJECT_NAME, data.subjectName)
            .putString(KEY_LESSON_TITLE, data.lessonTitle)
            .putBoolean(KEY_IS_OTHER_FOCUS, data.isOtherFocus)
            .apply()
    }

    fun startTimer(
        context: Context,
        subjectId: Long?,
        subjectName: String,
        lessonTitle: String?,
        isOtherFocus: Boolean
    ) {
        val current = _timerState.value
        val duration = if (current.remainingSeconds > 0) current.remainingSeconds else current.totalSeconds
        val endTime = System.currentTimeMillis() + (duration * 1000L)

        val newState = current.copy(
            isRunning = true,
            endTimeMs = endTime,
            remainingSeconds = duration,
            subjectId = subjectId,
            subjectName = subjectName,
            lessonTitle = lessonTitle,
            isOtherFocus = isOtherFocus
        )
        _timerState.value = newState
        saveToPrefs(newState)

        FocusTimerService.startService(context, subjectName, endTime, duration)
        startTickerIfNeeded(context)
    }

    fun pauseTimer(context: Context) {
        val current = _timerState.value
        val now = System.currentTimeMillis()
        val remaining = ((current.endTimeMs - now) / 1000L).coerceAtLeast(0L)

        val newState = current.copy(
            isRunning = false,
            remainingSeconds = remaining
        )
        _timerState.value = newState
        saveToPrefs(newState)

        FocusTimerService.stopService(context)
        tickerJob?.cancel()
    }

    fun resetTimer(context: Context) {
        val current = _timerState.value
        val newState = current.copy(
            isRunning = false,
            endTimeMs = 0L,
            remainingSeconds = current.totalSeconds
        )
        _timerState.value = newState
        saveToPrefs(newState)

        FocusTimerService.stopService(context)
        tickerJob?.cancel()
    }

    fun setDuration(totalSeconds: Long) {
        val current = _timerState.value
        if (!current.isRunning) {
            val newState = current.copy(
                totalSeconds = totalSeconds,
                remainingSeconds = totalSeconds
            )
            _timerState.value = newState
            saveToPrefs(newState)
        }
    }

    fun onFinished(context: Context) {
        val finishedState = _timerState.value
        val newState = finishedState.copy(
            isRunning = false,
            endTimeMs = 0L,
            remainingSeconds = finishedState.totalSeconds
        )
        _timerState.value = newState
        saveToPrefs(newState)

        FocusTimerService.stopService(context)
        tickerJob?.cancel()

        onTimerFinishedListener?.invoke(finishedState)
    }

    private fun startTickerIfNeeded(context: Context) {
        if (tickerJob?.isActive == true) return

        tickerJob = scope.launch {
            while (isActive) {
                delay(1000L)
                val state = _timerState.value
                if (state.isRunning) {
                    val now = System.currentTimeMillis()
                    val remaining = ((state.endTimeMs - now) / 1000L).coerceAtLeast(0L)
                    _timerState.value = state.copy(remainingSeconds = remaining)

                    if (remaining <= 0L) {
                        onFinished(context)
                        break
                    }
                }
            }
        }
    }
}
