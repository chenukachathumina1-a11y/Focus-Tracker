package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.LessonEntity
import com.example.data.db.SubjectEntity
import com.example.localization.Strings
import com.example.ui.components.GlowingTimerRing
import com.example.ui.theme.AmberFlame
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.FocusObsidianDark
import com.example.ui.theme.FocusOutline
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FocusTimerScreen(viewModel: AppViewModel) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val allLessons by viewModel.lessons.collectAsStateWithLifecycle()
    val allSessions by viewModel.sessions.collectAsStateWithLifecycle()

    val remainingSeconds by viewModel.timerRemainingSeconds.collectAsStateWithLifecycle()
    val totalSeconds by viewModel.timerTotalSeconds.collectAsStateWithLifecycle()
    val isRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubject.collectAsStateWithLifecycle()
    val isOtherFocusMode by viewModel.isOtherFocusMode.collectAsStateWithLifecycle()
    val selectedLesson by viewModel.selectedLesson.collectAsStateWithLifecycle()
    val keepScreenOn by viewModel.keepScreenOn.collectAsStateWithLifecycle()

    var showLessonPicker by remember { mutableStateOf(false) }
    var showCustomDurationDialog by remember { mutableStateOf(false) }
    var customMinutesInput by remember { mutableStateOf("45") }

    // Today's total calculation
    val todayDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val todaySessions = allSessions.filter { it.dateString == todayDateStr }
    val todayTotalSeconds = todaySessions.sumOf { it.durationSeconds }
    val todayTotalHours = todayTotalSeconds.toFloat() / 3600f

    // Accent color based on selection
    val accentColor = if (isOtherFocusMode) {
        ElectricPurple
    } else {
        try {
            Color(android.graphics.Color.parseColor(selectedSubject?.colorHex ?: "#00E5FF"))
        } catch (_: Exception) {
            NeonCyan
        }
    }

    val subjectTitle = if (isOtherFocusMode) {
        Strings.get("other_focus_mode", lang)
    } else {
        if (lang == "si") selectedSubject?.nameSi ?: selectedSubject?.nameEn ?: "" else selectedSubject?.nameEn ?: ""
    }

    // Filter lessons for currently selected subject
    val subjectLessons = allLessons.filter { it.subjectId == selectedSubject?.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Strings.get("focus_mode", lang),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "${Strings.get("today_total", lang)}: ${String.format(Locale.getDefault(), "%.1f", todayTotalHours)} ${Strings.get("hours_abbr", lang)}",
                    style = MaterialTheme.typography.bodySmall.copy(color = NeonCyan)
                )
            }

            // Keep screen on switch
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = if (keepScreenOn) NeonCyan else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                    checked = keepScreenOn,
                    onCheckedChange = { viewModel.toggleKeepScreenOn(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = FocusObsidianDark,
                        checkedTrackColor = NeonCyan,
                        uncheckedTrackColor = FocusSurfaceElevated
                    ),
                    modifier = Modifier.testTag("keep_screen_on_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subject Selection Horizontal Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            subjects.forEach { sub ->
                val isSelected = !isOtherFocusMode && selectedSubject?.id == sub.id
                val chipColor = try {
                    Color(android.graphics.Color.parseColor(sub.colorHex))
                } catch (_: Exception) {
                    NeonCyan
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) chipColor.copy(alpha = 0.2f) else FocusSurfaceVariant)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) chipColor else FocusOutline,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { viewModel.selectSubject(sub) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("subject_chip_${sub.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(chipColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == "si") sub.nameSi else sub.nameEn,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) chipColor else TextPrimary
                            )
                        )
                    }
                }
            }

            // Other Focus Time Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isOtherFocusMode) ElectricPurple.copy(alpha = 0.25f) else FocusSurfaceVariant)
                    .border(
                        width = if (isOtherFocusMode) 1.5.dp else 1.dp,
                        color = if (isOtherFocusMode) ElectricPurple else FocusOutline,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { viewModel.enableOtherFocusMode() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("other_focus_chip"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ElectricPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.get("other_focus_mode", lang),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isOtherFocusMode) FontWeight.Bold else FontWeight.Normal,
                            color = if (isOtherFocusMode) ElectricPurple else TextPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Lesson Tag Selector (if regular subject selected)
        if (!isOtherFocusMode && selectedSubject != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLessonPicker = true }
                    .testTag("lesson_selector_card"),
                colors = CardDefaults.cardColors(containerColor = FocusSurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedLesson?.title ?: Strings.get("no_lesson_selected", lang),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (selectedLesson != null) TextPrimary else TextMuted,
                                fontWeight = if (selectedLesson != null) FontWeight.SemiBold else FontWeight.Normal
                            ),
                            maxLines = 1
                        )
                    }

                    Text(
                        text = Strings.get("edit", lang),
                        style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Glowing Timer Ring
        GlowingTimerRing(
            remainingSeconds = remainingSeconds,
            totalSeconds = totalSeconds,
            isActive = isRunning,
            accentColor = accentColor,
            subjectTitle = subjectTitle,
            lessonTitle = selectedLesson?.title,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Preset Duration Chips (25m, 45m, 60m, 90m, Custom)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val presets = listOf(15, 25, 45, 60, 90)
            presets.forEach { min ->
                val isCurrent = (totalSeconds == min * 60L)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCurrent) FocusSurfaceElevated else FocusSurfaceVariant)
                        .border(
                            width = 1.dp,
                            color = if (isCurrent) NeonCyan else FocusOutline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = !isRunning) { viewModel.setTimerPreset(min) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("preset_${min}m"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${min}m",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) NeonCyan else TextSecondary
                        )
                    )
                }
            }

            // Custom minutes button
            IconButton(
                onClick = { showCustomDurationDialog = true },
                enabled = !isRunning,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(FocusSurfaceVariant)
                    .border(1.dp, FocusOutline, RoundedCornerShape(12.dp))
                    .size(36.dp)
                    .testTag("custom_duration_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreTime,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Controls (Start / Pause / Finish / Reset)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset button
            IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(FocusSurfaceElevated)
                    .testTag("reset_timer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = Strings.get("reset", lang),
                    tint = TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Main Play/Pause Button
            Button(
                onClick = {
                    if (isRunning) viewModel.pauseTimer() else viewModel.startTimer()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = FocusObsidianDark
                ),
                shape = CircleShape,
                modifier = Modifier
                    .size(72.dp)
                    .testTag("play_pause_timer_button")
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) Strings.get("pause_focus", lang) else Strings.get("start_focus", lang),
                    tint = FocusObsidianDark,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Finish & Log Session
            IconButton(
                onClick = { viewModel.finishSessionAndLog() },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(FocusSurfaceElevated)
                    .testTag("finish_session_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = Strings.get("stop_finish", lang),
                    tint = RadiantEmerald
                )
            }
        }
    }

    // Lesson Picker Dialog
    if (showLessonPicker) {
        AlertDialog(
            onDismissRequest = { showLessonPicker = false },
            containerColor = FocusSurfaceVariant,
            title = {
                Text(
                    text = Strings.get("select_lesson", lang),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Option for No Lesson
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectLesson(null)
                                showLessonPicker = false
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings.get("no_lesson_selected", lang),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (selectedLesson == null) NeonCyan else TextSecondary,
                                fontWeight = if (selectedLesson == null) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (subjectLessons.isEmpty()) {
                        Text(
                            text = Strings.get("no_lessons_yet", lang),
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    } else {
                        subjectLessons.forEach { lesson ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectLesson(lesson)
                                        showLessonPicker = false
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = lesson.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (selectedLesson?.id == lesson.id) NeonCyan else TextPrimary,
                                        fontWeight = if (selectedLesson?.id == lesson.id) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                                if (lesson.isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = RadiantEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLessonPicker = false }) {
                    Text(Strings.get("close", lang), color = NeonCyan)
                }
            }
        )
    }

    // Custom Duration Dialog
    if (showCustomDurationDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDurationDialog = false },
            containerColor = FocusSurfaceVariant,
            title = {
                Text(
                    text = Strings.get("custom_minutes", lang),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = customMinutesInput,
                    onValueChange = { customMinutesInput = it.filter { ch -> ch.isDigit() } },
                    placeholder = { Text("e.g. 50", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = FocusOutline,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = customMinutesInput.toIntOrNull() ?: 25
                        viewModel.setCustomTimerMinutes(mins)
                        showCustomDurationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = FocusObsidianDark)
                ) {
                    Text(Strings.get("save", lang), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDurationDialog = false }) {
                    Text(Strings.get("cancel", lang), color = TextSecondary)
                }
            }
        )
    }
}
