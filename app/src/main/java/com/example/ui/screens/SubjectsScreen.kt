package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.LessonEntity
import com.example.data.db.SubjectEntity
import com.example.localization.Strings
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

@Composable
fun SubjectsScreen(viewModel: AppViewModel) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val allLessons by viewModel.lessons.collectAsStateWithLifecycle()

    var showAddLessonDialogForSubject by remember { mutableStateOf<SubjectEntity?>(null) }
    var newLessonTitle by remember { mutableStateOf("") }
    var expandedSubjectId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Strings.get("profile_dashboard", lang),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "${profile?.examType ?: "A/L"} • ${profile?.alStream ?: ""}",
                    style = MaterialTheme.typography.bodySmall.copy(color = NeonCyan)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("subjects_lazy_list"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(subjects, key = { it.id }) { subject ->
                val isExpanded = expandedSubjectId == subject.id
                val subjectLessons = allLessons.filter { it.subjectId == subject.id }
                val completedCount = subjectLessons.count { it.isCompleted }

                val subjectColor = try {
                    Color(android.graphics.Color.parseColor(subject.colorHex))
                } catch (_: Exception) {
                    NeonCyan
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("subject_card_${subject.id}"),
                    colors = CardDefaults.cardColors(containerColor = FocusSurfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    border = if (isExpanded) androidx.compose.foundation.BorderStroke(1.5.dp, subjectColor) else null
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedSubjectId = if (isExpanded) null else subject.id
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(subjectColor)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (lang == "si") subject.nameSi else subject.nameEn,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    if (subject.isPermanent) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${subjectLessons.size} ${Strings.get("lessons_for_subject", lang)} • $completedCount ${Strings.get("completed", lang)}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }

                            IconButton(
                                onClick = {
                                    showAddLessonDialogForSubject = subject
                                    newLessonTitle = ""
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(FocusSurfaceElevated)
                                    .testTag("add_lesson_button_${subject.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = Strings.get("add_lesson", lang),
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }

                        // Expanded Lesson List
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            ) {
                                if (subjectLessons.isEmpty()) {
                                    Text(
                                        text = Strings.get("no_lessons_yet", lang),
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    subjectLessons.forEach { lesson ->
                                        LessonRowItem(
                                            lesson = lesson,
                                            onToggle = { viewModel.toggleLessonCompleted(lesson) },
                                            onDelete = { viewModel.deleteLesson(lesson.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Lesson Dialog
    if (showAddLessonDialogForSubject != null) {
        val targetSub = showAddLessonDialogForSubject!!
        AlertDialog(
            onDismissRequest = { showAddLessonDialogForSubject = null },
            containerColor = FocusSurfaceVariant,
            title = {
                Text(
                    text = "${Strings.get("add_lesson", lang)} - ${if (lang == "si") targetSub.nameSi else targetSub.nameEn}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            },
            text = {
                OutlinedTextField(
                    value = newLessonTitle,
                    onValueChange = { newLessonTitle = it },
                    placeholder = { Text(Strings.get("lesson_name_placeholder", lang), color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = FocusOutline,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newLessonTitle.isNotBlank()) {
                            viewModel.addLesson(targetSub.id, newLessonTitle)
                            showAddLessonDialogForSubject = null
                            newLessonTitle = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = FocusObsidianDark)
                ) {
                    Text(Strings.get("add", lang), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLessonDialogForSubject = null }) {
                    Text(Strings.get("cancel", lang), color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun LessonRowItem(
    lesson: LessonEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(FocusSurfaceElevated)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clickable { onToggle() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (lesson.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (lesson.isCompleted) RadiantEmerald else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (lesson.isCompleted) TextMuted else TextPrimary,
                    textDecoration = if (lesson.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
}
