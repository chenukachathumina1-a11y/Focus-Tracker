package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.ScheduledFocusEntity
import com.example.localization.Strings
import com.example.ui.theme.AmberFlame
import com.example.ui.theme.FocusObsidianDark
import com.example.ui.theme.FocusOutline
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.RadiantRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppViewModel
import java.util.Locale

@Composable
fun AlarmSettingsScreen(
    viewModel: AppViewModel,
    onChangeCurriculumRequested: () -> Unit
) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val scheduledFocusList by viewModel.scheduledFocusList.collectAsStateWithLifecycle()

    var showTimePickerDialog by remember { mutableStateOf(false) }
    var showRingtoneDialog by remember { mutableStateOf(false) }
    var showNotificationSoundDialog by remember { mutableStateOf(false) }
    var showAddScheduleDialog by remember { mutableStateOf(false) }

    var alarmHour by remember { mutableIntStateOf(profile?.morningAlarmHour ?: 5) }
    var alarmMinute by remember { mutableIntStateOf(profile?.morningAlarmMinute ?: 30) }
    var isAlarmEnabled by remember { mutableStateOf(profile?.morningAlarmEnabled ?: false) }
    var selectedRingtone by remember { mutableStateOf(profile?.morningAlarmRingtoneName ?: "Gentle Sunrise Bells") }
    var isPreviewPlaying by remember { mutableStateOf(false) }

    val availableRingtones = listOf(
        "Gentle Sunrise Bells",
        "Calm Temple Chime",
        "Morning Forest Birds",
        "Energetic Digital Pulse",
        "Default System Alarm"
    )

    val notificationSounds = listOf(
        "Bell Chime",
        "Gentle Sunrise Bells",
        "Calm Temple Chime",
        "Energetic Digital Pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("alarm_settings_screen")
    ) {
        // Top Header
        Text(
            text = Strings.get("alarm_settings_title", lang),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
        Text(
            text = Strings.get("notification_sound_desc", lang),
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 1: MORNING GETTING UP ALARM
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("morning_alarm_card"),
            colors = CardDefaults.cardColors(containerColor = FocusSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AmberFlame.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = AmberFlame,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = Strings.get("morning_alarm", lang),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            val timeStr = String.format(Locale.getDefault(), "%02d:%02d %s",
                                if (alarmHour % 12 == 0) 12 else alarmHour % 12,
                                alarmMinute,
                                if (alarmHour < 12) "AM" else "PM"
                            )
                            Text(
                                text = timeStr,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAlarmEnabled) NeonCyan else TextMuted
                                ),
                                modifier = Modifier.clickable { showTimePickerDialog = true }
                            )
                        }
                    }

                    Switch(
                        checked = isAlarmEnabled,
                        onCheckedChange = {
                            isAlarmEnabled = it
                            viewModel.updateMorningAlarm(alarmHour, alarmMinute, it, selectedRingtone)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = FocusObsidianDark,
                            checkedTrackColor = NeonCyan,
                            uncheckedTrackColor = FocusSurfaceElevated
                        ),
                        modifier = Modifier.testTag("morning_alarm_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Ringtone Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(FocusSurfaceElevated)
                        .clickable { showRingtoneDialog = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Strings.get("alarm_sound", lang),
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                            Text(
                                text = selectedRingtone,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            )
                        }
                    }

                    // Preview Button
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isPreviewPlaying) {
                            IconButton(
                                onClick = {
                                    viewModel.stopTone()
                                    isPreviewPlaying = false
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = null, tint = RadiantRose)
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    viewModel.previewTone(selectedRingtone)
                                    isPreviewPlaying = true
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = RadiantEmerald)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 2: FOCUS FINISHED NOTIFICATION SOUND
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("focus_alert_card"),
            colors = CardDefaults.cardColors(containerColor = FocusSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("focus_alert_sound", lang),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(FocusSurfaceElevated)
                        .clickable { showNotificationSoundDialog = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = profile?.focusAlertSound ?: "Bell Chime",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = Strings.get("edit", lang),
                        style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 3: SCHEDULE FOCUS HOURS
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("scheduled_focus_card"),
            colors = CardDefaults.cardColors(containerColor = FocusSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.get("schedule_focus_hours", lang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    IconButton(
                        onClick = { showAddScheduleDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = FocusObsidianDark)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (scheduledFocusList.isEmpty()) {
                    Text(
                        text = "No study routines scheduled. Tap + to set a regular daily study block.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    scheduledFocusList.forEach { schedule ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(FocusSurfaceElevated)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val sTimeStr = String.format(Locale.getDefault(), "%02d:%02d", schedule.hour, schedule.minute)
                                Text(
                                    text = "$sTimeStr • ${schedule.subjectName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "${schedule.durationMinutes} mins planned",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = schedule.isEnabled,
                                    onCheckedChange = { viewModel.toggleScheduledFocus(schedule) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = FocusObsidianDark,
                                        checkedTrackColor = NeonCyan
                                    )
                                )
                                IconButton(
                                    onClick = { viewModel.deleteScheduledFocus(schedule.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 4: LANGUAGE PREFERENCE
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = FocusSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("language_setting", lang),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.setLanguage("en") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lang == "en") NeonCyan else FocusSurfaceElevated,
                            contentColor = if (lang == "en") FocusObsidianDark else TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("English", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.setLanguage("si") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lang == "si") NeonCyan else FocusSurfaceElevated,
                            contentColor = if (lang == "si") FocusObsidianDark else TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("සිංහල", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 5: CHANGE EXAM & SUBJECTS
        Button(
            onClick = onChangeCurriculumRequested,
            colors = ButtonDefaults.buttonColors(
                containerColor = FocusSurfaceElevated,
                contentColor = NeonCyan
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("change_exam_subject_button")
        ) {
            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Strings.get("change_exam_subject", lang),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Time Picker Dialog for Morning Alarm
    if (showTimePickerDialog) {
        var tempHour by remember { mutableIntStateOf(alarmHour) }
        var tempMin by remember { mutableIntStateOf(alarmMinute) }

        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            containerColor = FocusSurfaceVariant,
            title = {
                Text(
                    text = Strings.get("morning_alarm", lang),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Hour
                        OutlinedTextField(
                            value = String.format(Locale.getDefault(), "%02d", tempHour),
                            onValueChange = {
                                val v = it.toIntOrNull() ?: 0
                                if (v in 0..23) tempHour = v
                            },
                            singleLine = true,
                            modifier = Modifier.width(70.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan)
                        )
                        Text(" : ", style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary))
                        // Minute
                        OutlinedTextField(
                            value = String.format(Locale.getDefault(), "%02d", tempMin),
                            onValueChange = {
                                val v = it.toIntOrNull() ?: 0
                                if (v in 0..59) tempMin = v
                            },
                            singleLine = true,
                            modifier = Modifier.width(70.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("(24-hour format: 05:30 = 5:30 AM)", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        alarmHour = tempHour
                        alarmMinute = tempMin
                        viewModel.updateMorningAlarm(alarmHour, alarmMinute, isAlarmEnabled, selectedRingtone)
                        showTimePickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = FocusObsidianDark)
                ) {
                    Text(Strings.get("save", lang), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text(Strings.get("cancel", lang), color = TextSecondary)
                }
            }
        )
    }

    // Ringtone Selection Dialog
    if (showRingtoneDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.stopTone()
                isPreviewPlaying = false
                showRingtoneDialog = false
            },
            containerColor = FocusSurfaceVariant,
            title = {
                Text(
                    text = Strings.get("alarm_sound", lang),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    availableRingtones.forEach { tone ->
                        val isSelected = selectedRingtone == tone
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRingtone = tone
                                    viewModel.updateMorningAlarm(alarmHour, alarmMinute, isAlarmEnabled, tone)
                                    viewModel.previewTone(tone)
                                    isPreviewPlaying = true
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tone,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NeonCyan else TextPrimary
                                )
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.stopTone()
                        isPreviewPlaying = false
                        showRingtoneDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = FocusObsidianDark)
                ) {
                    Text(Strings.get("done", lang), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Notification Sound Dialog
    if (showNotificationSoundDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.stopTone()
                showNotificationSoundDialog = false
            },
            containerColor = FocusSurfaceVariant,
            title = {
                Text(
                    text = Strings.get("focus_alert_sound", lang),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    notificationSounds.forEach { sound ->
                        val isSelected = (profile?.focusAlertSound ?: "Bell Chime") == sound
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateFocusAlertSound(sound)
                                    viewModel.previewTone(sound)
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sound,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NeonCyan else TextPrimary
                                )
                            )
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.stopTone()
                        showNotificationSoundDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = FocusObsidianDark)
                ) {
                    Text(Strings.get("done", lang), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Add Scheduled Focus Dialog
    if (showAddScheduleDialog) {
        var sHour by remember { mutableIntStateOf(16) }
        var sMinute by remember { mutableIntStateOf(0) }
        var sDuration by remember { mutableIntStateOf(60) }
        var sSubjectName by remember { mutableStateOf("Self Study") }

        AlertDialog(
            onDismissRequest = { showAddScheduleDialog = false },
            containerColor = FocusSurfaceVariant,
            title = {
                Text(
                    text = Strings.get("add_schedule", lang),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = sSubjectName,
                        onValueChange = { sSubjectName = it },
                        label = { Text("Subject / Activity", color = TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonCyan)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = sHour.toString(),
                            onValueChange = { sHour = it.toIntOrNull() ?: 16 },
                            label = { Text("Hour (0-23)", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = sMinute.toString(),
                            onValueChange = { sMinute = it.toIntOrNull() ?: 0 },
                            label = { Text("Minute (0-59)", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sDuration.toString(),
                        onValueChange = { sDuration = it.toIntOrNull() ?: 60 },
                        label = { Text("Duration (mins)", color = TextSecondary) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addScheduledFocus(null, sSubjectName, sHour, sMinute, sDuration)
                        showAddScheduleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = FocusObsidianDark)
                ) {
                    Text(Strings.get("add", lang), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddScheduleDialog = false }) {
                    Text(Strings.get("cancel", lang), color = TextSecondary)
                }
            }
        )
    }
}
