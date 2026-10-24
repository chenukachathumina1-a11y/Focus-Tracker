package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.ALStream
import com.example.data.model.AppLanguage
import com.example.data.model.ExamType
import com.example.data.model.PresetCurriculum
import com.example.localization.Strings
import com.example.ui.theme.AmberFlame
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.FocusObsidianDark
import com.example.ui.theme.FocusOutline
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    onComplete: (
        language: String,
        examType: ExamType,
        alStream: ALStream,
        selectedSubjectIds: Set<String>,
        customSubjects: List<String>
    ) -> Unit
) {
    var step by remember { mutableIntStateOf(0) } // 0: Lang, 1: Exam, 2: Stream (if AL), 3: Subjects

    var selectedLang by remember { mutableStateOf("en") }
    var selectedExam by remember { mutableStateOf(ExamType.AL) }
    var selectedStream by remember { mutableStateOf(ALStream.MATHS) }

    val selectedSubjectIds = remember { mutableStateListOf<String>() }
    val customSubjects = remember { mutableStateListOf<String>() }
    var customInputText by remember { mutableStateOf("") }

    // Helper to initialize subject selections when stream or exam changes
    fun syncInitialSubjects() {
        selectedSubjectIds.clear()
        customSubjects.clear()
        if (selectedExam == ExamType.AL) {
            when (selectedStream) {
                ALStream.MATHS -> {
                    PresetCurriculum.mathsPermanent.forEach { selectedSubjectIds.add(it.id) }
                    selectedSubjectIds.add(PresetCurriculum.mathsElectives.first().id) // Chemistry default
                }
                ALStream.BIO -> {
                    PresetCurriculum.bioPermanent.forEach { selectedSubjectIds.add(it.id) }
                    selectedSubjectIds.add(PresetCurriculum.bioElectives.first().id) // Physics default
                }
                ALStream.COMMERCE -> {
                    PresetCurriculum.commerceSubjects.take(3).forEach { selectedSubjectIds.add(it.id) }
                }
                ALStream.ARTS -> {
                    PresetCurriculum.artsSubjects.take(3).forEach { selectedSubjectIds.add(it.id) }
                }
                ALStream.ETECH -> {
                    PresetCurriculum.etechSubjects.take(3).forEach { selectedSubjectIds.add(it.id) }
                }
                ALStream.BTECH -> {
                    PresetCurriculum.btechSubjects.take(3).forEach { selectedSubjectIds.add(it.id) }
                }
            }
        } else {
            PresetCurriculum.olSubjects.forEach { selectedSubjectIds.add(it.id) }
        }
    }

    Scaffold(
        containerColor = FocusObsidianDark,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    OutlinedButton(
                        onClick = {
                            if (step == 3 && selectedExam == ExamType.OL) {
                                step = 1 // Skip stream step for OL
                            } else {
                                step -= 1
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        modifier = Modifier.testTag("onboarding_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(Strings.get("back", selectedLang))
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = {
                        when (step) {
                            0 -> step = 1
                            1 -> {
                                if (selectedExam == ExamType.AL) {
                                    step = 2
                                } else {
                                    syncInitialSubjects()
                                    step = 3
                                }
                            }
                            2 -> {
                                syncInitialSubjects()
                                step = 3
                            }
                            3 -> {
                                onComplete(
                                    selectedLang,
                                    selectedExam,
                                    selectedStream,
                                    selectedSubjectIds.toSet(),
                                    customSubjects.toList()
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = FocusObsidianDark
                    ),
                    modifier = Modifier.testTag("onboarding_next_button")
                ) {
                    Text(
                        text = if (step == 3) Strings.get("get_started", selectedLang) else Strings.get("next", selectedLang),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Step Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val totalSteps = if (selectedExam == ExamType.AL) 4 else 3
                for (s in 0 until totalSteps) {
                    val active = s <= step
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(if (active) NeonCyan else FocusSurfaceElevated)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            when (step) {
                // STEP 0: LANGUAGE SELECTION
                0 -> {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Strings.get("step_language", selectedLang),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Choose your preferred language for the application / යෙදුම සඳහා ඔබේ භාෂාව තෝරන්න",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    AppLanguage.values().forEach { lang ->
                        val isSelected = selectedLang == lang.code
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { selectedLang = lang.code }
                                .testTag("lang_option_${lang.code}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) FocusSurfaceElevated else FocusSurfaceVariant
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, NeonCyan) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = lang.labelEn,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = lang.labelSi,
                                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan)
                                }
                            }
                        }
                    }
                }

                // STEP 1: EXAM SELECTION
                1 -> {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Strings.get("step_exam", selectedLang),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = Strings.get("onboarding_desc", selectedLang),
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    ExamType.values().forEach { exam ->
                        val isSelected = selectedExam == exam
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .clickable { selectedExam = exam }
                                .testTag("exam_option_${exam.code}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) FocusSurfaceElevated else FocusSurfaceVariant
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, NeonCyan) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (selectedLang == "si") exam.nameSi else exam.nameEn,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = if (exam == ExamType.AL) "Physical Science, Bio, Commerce, Arts, Tech" else "Standard National O/L Curriculum (9 Modules)",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan)
                                }
                            }
                        }
                    }
                }

                // STEP 2: A/L STREAM SELECTION
                2 -> {
                    Text(
                        text = Strings.get("step_stream", selectedLang),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = Strings.get("select_stream_subtitle", selectedLang),
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(ALStream.values()) { stream ->
                            val isSelected = selectedStream == stream
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedStream = stream }
                                    .testTag("stream_option_${stream.code}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) FocusSurfaceElevated else FocusSurfaceVariant
                                ),
                                shape = RoundedCornerShape(14.dp),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, NeonCyan) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (selectedLang == "si") stream.nameSi else stream.nameEn,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        )
                                        Text(
                                            text = if (selectedLang == "si") stream.descriptionSi else stream.descriptionEn,
                                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = NeonCyan)
                                    }
                                }
                            }
                        }
                    }
                }

                // STEP 3: SUBJECT CONFIGURATION & VERIFICATION
                3 -> {
                    Text(
                        text = Strings.get("step_subjects", selectedLang),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    if (selectedExam == ExamType.AL) {
                        Text(
                            text = Strings.get("must_select_3_al", selectedLang),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (selectedSubjectIds.size + customSubjects.size == 3) RadiantEmerald else AmberFlame,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    } else {
                        Text(
                            text = Strings.get("ol_subjects_hint", selectedLang),
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // MATHS STREAM LOGIC
                        if (selectedExam == ExamType.AL && selectedStream == ALStream.MATHS) {
                            item {
                                Text(
                                    text = Strings.get("permanent_subject_hint", selectedLang),
                                    style = MaterialTheme.typography.labelMedium.copy(color = NeonCyan, fontWeight = FontWeight.Bold)
                                )
                            }
                            items(PresetCurriculum.mathsPermanent) { bp ->
                                SubjectSelectCard(
                                    title = if (selectedLang == "si") bp.nameSi else bp.nameEn,
                                    colorHex = bp.colorHex,
                                    isPermanent = true,
                                    isSelected = true,
                                    onToggle = {}
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = Strings.get("elective_subject_hint", selectedLang),
                                    style = MaterialTheme.typography.labelMedium.copy(color = AmberFlame, fontWeight = FontWeight.Bold)
                                )
                            }
                            items(PresetCurriculum.mathsElectives) { bp ->
                                val isSelected = selectedSubjectIds.contains(bp.id)
                                SubjectSelectCard(
                                    title = if (selectedLang == "si") bp.nameSi else bp.nameEn,
                                    colorHex = bp.colorHex,
                                    isPermanent = false,
                                    isSelected = isSelected,
                                    onToggle = {
                                        // Pick only 1 elective
                                        PresetCurriculum.mathsElectives.forEach { selectedSubjectIds.remove(it.id) }
                                        selectedSubjectIds.add(bp.id)
                                    }
                                )
                            }
                        }

                        // BIO STREAM LOGIC
                        else if (selectedExam == ExamType.AL && selectedStream == ALStream.BIO) {
                            item {
                                Text(
                                    text = Strings.get("permanent_subject_hint", selectedLang),
                                    style = MaterialTheme.typography.labelMedium.copy(color = NeonCyan, fontWeight = FontWeight.Bold)
                                )
                            }
                            items(PresetCurriculum.bioPermanent) { bp ->
                                SubjectSelectCard(
                                    title = if (selectedLang == "si") bp.nameSi else bp.nameEn,
                                    colorHex = bp.colorHex,
                                    isPermanent = true,
                                    isSelected = true,
                                    onToggle = {}
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = Strings.get("elective_subject_hint", selectedLang),
                                    style = MaterialTheme.typography.labelMedium.copy(color = AmberFlame, fontWeight = FontWeight.Bold)
                                )
                            }
                            items(PresetCurriculum.bioElectives) { bp ->
                                val isSelected = selectedSubjectIds.contains(bp.id)
                                SubjectSelectCard(
                                    title = if (selectedLang == "si") bp.nameSi else bp.nameEn,
                                    colorHex = bp.colorHex,
                                    isPermanent = false,
                                    isSelected = isSelected,
                                    onToggle = {
                                        // Pick only 1 elective
                                        PresetCurriculum.bioElectives.forEach { selectedSubjectIds.remove(it.id) }
                                        selectedSubjectIds.add(bp.id)
                                    }
                                )
                            }
                        }

                        // COMMERCE STREAM
                        else if (selectedExam == ExamType.AL && selectedStream == ALStream.COMMERCE) {
                            items(PresetCurriculum.commerceSubjects) { bp ->
                                val isSelected = selectedSubjectIds.contains(bp.id)
                                SubjectSelectCard(
                                    title = if (selectedLang == "si") bp.nameSi else bp.nameEn,
                                    colorHex = bp.colorHex,
                                    isPermanent = false,
                                    isSelected = isSelected,
                                    onToggle = {
                                        if (isSelected) {
                                            selectedSubjectIds.remove(bp.id)
                                        } else {
                                            if (selectedSubjectIds.size + customSubjects.size < 3) {
                                                selectedSubjectIds.add(bp.id)
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        // ARTS STREAM
                        else if (selectedExam == ExamType.AL && selectedStream == ALStream.ARTS) {
                            items(PresetCurriculum.artsSubjects) { bp ->
                                val isSelected = selectedSubjectIds.contains(bp.id)
                                SubjectSelectCard(
                                    title = if (selectedLang == "si") bp.nameSi else bp.nameEn,
                                    colorHex = bp.colorHex,
                                    isPermanent = false,
                                    isSelected = isSelected,
                                    onToggle = {
                                        if (isSelected) {
                                            selectedSubjectIds.remove(bp.id)
                                        } else {
                                            if (selectedSubjectIds.size + customSubjects.size < 3) {
                                                selectedSubjectIds.add(bp.id)
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        // E-TECH & B-TECH
                        else if (selectedExam == ExamType.AL && (selectedStream == ALStream.ETECH || selectedStream == ALStream.BTECH)) {
                            val techList = if (selectedStream == ALStream.ETECH) PresetCurriculum.etechSubjects else PresetCurriculum.btechSubjects
                            items(techList) { bp ->
                                val isSelected = bp.isPermanent || selectedSubjectIds.contains(bp.id)
                                SubjectSelectCard(
                                    title = if (selectedLang == "si") bp.nameSi else bp.nameEn,
                                    colorHex = bp.colorHex,
                                    isPermanent = bp.isPermanent,
                                    isSelected = isSelected,
                                    onToggle = {
                                        if (!bp.isPermanent) {
                                            if (selectedSubjectIds.contains(bp.id)) {
                                                selectedSubjectIds.remove(bp.id)
                                            } else {
                                                if (selectedSubjectIds.size + customSubjects.size < 3) {
                                                    selectedSubjectIds.add(bp.id)
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        // O/L CURRICULUM
                        else {
                            items(PresetCurriculum.olSubjects) { bp ->
                                SubjectSelectCard(
                                    title = if (selectedLang == "si") bp.nameSi else bp.nameEn,
                                    colorHex = bp.colorHex,
                                    isPermanent = bp.isPermanent,
                                    isSelected = selectedSubjectIds.contains(bp.id),
                                    onToggle = {
                                        if (!bp.isPermanent) {
                                            if (selectedSubjectIds.contains(bp.id)) selectedSubjectIds.remove(bp.id)
                                            else selectedSubjectIds.add(bp.id)
                                        }
                                    }
                                )
                            }
                        }

                        // Custom Subject Input for Arts/Commerce/Tech if < 3 subjects
                        if (selectedExam == ExamType.AL && selectedSubjectIds.size + customSubjects.size < 3) {
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = Strings.get("add_custom_subject", selectedLang),
                                    style = MaterialTheme.typography.titleSmall.copy(color = NeonCyan)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = customInputText,
                                        onValueChange = { customInputText = it },
                                        placeholder = { Text(Strings.get("subject_name_placeholder", selectedLang), color = TextMuted) },
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NeonCyan,
                                            unfocusedBorderColor = FocusOutline,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            if (customInputText.isNotBlank()) {
                                                customSubjects.add(customInputText.trim())
                                                customInputText = ""
                                            }
                                        },
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(NeonCyan)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = FocusObsidianDark)
                                    }
                                }
                            }
                        }

                        // Other Focus Note
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = FocusSurfaceElevated),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "💡 " + Strings.get("other_focus_notice", selectedLang),
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectSelectCard(
    title: String,
    colorHex: String,
    isPermanent: Boolean,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val chipColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (_: Exception) {
        NeonCyan
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isPermanent) { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) FocusSurfaceElevated else FocusSurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, chipColor) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(chipColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                if (isPermanent) {
                    Text(
                        text = "Permanent Compulsory Subject",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            }
            if (isPermanent) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            } else if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = chipColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
