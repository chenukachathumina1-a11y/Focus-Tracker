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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localization.Strings
import com.example.ui.components.SmartRecommendationsCard
import com.example.ui.components.StudyBarGraph
import com.example.ui.components.StudyPieChart
import com.example.ui.theme.FocusOutline
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(viewModel: AppViewModel) {
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val allSessions by viewModel.sessions.collectAsStateWithLifecycle()

    var isWeeklyView by remember { mutableStateOf(false) }

    val todayDateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Filter sessions depending on Daily vs Weekly
    val activeSessions = if (!isWeeklyView) {
        allSessions.filter { it.dateString == todayDateStr }
    } else {
        // Last 7 days
        allSessions
    }

    val totalSeconds = activeSessions.sumOf { it.durationSeconds }
    val coreSeconds = activeSessions.filter { !it.isOtherFocus }.sumOf { it.durationSeconds }
    val otherSeconds = activeSessions.filter { it.isOtherFocus }.sumOf { it.durationSeconds }

    val subjectStats = viewModel.getSubjectStats(activeSessions, subjects, lang)
    val dayStats = viewModel.getWeeklyDayStats(allSessions, lang)
    val recommendations = viewModel.generateRecommendations(
        sessionList = activeSessions,
        subList = subjects,
        lang = lang,
        streamCode = profile?.alStream ?: "MATHS"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("analytics_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Strings.get("analytics_title", lang),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = if (isWeeklyView) Strings.get("weekly_view", lang) else Strings.get("daily_view", lang),
                    style = MaterialTheme.typography.bodySmall.copy(color = NeonCyan)
                )
            }

            // Daily / Weekly Toggle
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(FocusSurfaceVariant)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (!isWeeklyView) NeonCyan else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable { isWeeklyView = false }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("analytics_daily_toggle")
                ) {
                    Text(
                        text = Strings.get("daily_view", lang),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (!isWeeklyView) com.example.ui.theme.FocusObsidianDark else TextSecondary
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isWeeklyView) NeonCyan else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable { isWeeklyView = true }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("analytics_weekly_toggle")
                ) {
                    Text(
                        text = Strings.get("weekly_view", lang),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isWeeklyView) com.example.ui.theme.FocusObsidianDark else TextSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stat Summary Cards (Total, Core, Other Focus)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = Strings.get("total_study_time", lang),
                value = String.format(Locale.getDefault(), "%.1fh", totalSeconds.toFloat() / 3600f),
                color = NeonCyan,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = Strings.get("core_subjects_time", lang),
                value = String.format(Locale.getDefault(), "%.1fh", coreSeconds.toFloat() / 3600f),
                color = RadiantEmerald,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = Strings.get("other_focus_time_label", lang),
                value = String.format(Locale.getDefault(), "%.1fh", otherSeconds.toFloat() / 3600f),
                color = com.example.ui.theme.ElectricPurple,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pie Chart: Subject Distribution
        StudyPieChart(
            stats = subjectStats,
            lang = lang
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Bar Graph: Weekly Focus Hours
        StudyBarGraph(
            days = dayStats,
            targetHours = profile?.dailyTargetHours ?: 6f,
            lang = lang
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Smart Rule-Based Recommendations Card
        SmartRecommendationsCard(
            recommendations = recommendations,
            lang = lang
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = FocusSurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
    }
}
