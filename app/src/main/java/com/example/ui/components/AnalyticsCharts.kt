package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Strings
import com.example.ui.theme.AmberFlame
import com.example.ui.theme.FocusOutline
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.FocusSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RadiantEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

data class SubjectStudyStat(
    val subjectId: Long?,
    val name: String,
    val color: Color,
    val durationSeconds: Long,
    val isOtherFocus: Boolean = false
)

data class DayStudyStat(
    val dayLabel: String,
    val dateString: String,
    val hours: Float,
    val isToday: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudyPieChart(
    stats: List<SubjectStudyStat>,
    lang: String,
    modifier: Modifier = Modifier
) {
    val totalSeconds = stats.sumOf { it.durationSeconds }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("study_pie_chart_card"),
        colors = CardDefaults.cardColors(containerColor = FocusSurfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = Strings.get("study_distribution", lang),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (totalSeconds <= 0L) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = Strings.get("no_sessions_yet", lang),
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(170.dp)) {
                        var startAngle = -90f
                        val strokeWidth = 36.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                        val arcSize = Size(diameter, diameter)

                        stats.forEach { stat ->
                            val sweep = if (totalSeconds > 0) {
                                (stat.durationSeconds.toFloat() / totalSeconds.toFloat()) * 360f
                            } else 0f

                            if (sweep > 0f) {
                                drawArc(
                                    color = stat.color,
                                    startAngle = startAngle,
                                    sweepAngle = sweep - 1.5f,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = strokeWidth)
                                )
                                startAngle += sweep
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val totalHours = totalSeconds.toFloat() / 3600f
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", totalHours),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = Strings.get("hours_abbr", lang),
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Legend
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    stats.forEach { stat ->
                        val pct = if (totalSeconds > 0) {
                            (stat.durationSeconds.toFloat() / totalSeconds.toFloat()) * 100f
                        } else 0f

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FocusSurfaceElevated)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(stat.color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${stat.name} (${String.format(Locale.getDefault(), "%.0f%%", pct)})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudyBarGraph(
    days: List<DayStudyStat>,
    targetHours: Float = 6f,
    lang: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("study_bar_graph_card"),
        colors = CardDefaults.cardColors(containerColor = FocusSurfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.get("weekly_bar_chart", lang),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                Text(
                    text = "Goal: ${targetHours.toInt()}h/day",
                    style = MaterialTheme.typography.bodySmall.copy(color = NeonCyan)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val maxHours = maxOf(targetHours, (days.maxOfOrNull { it.hours } ?: 0f) + 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                    val count = days.size
                    if (count > 0) {
                        val barWidth = 22.dp.toPx()
                        val spacing = (size.width - (barWidth * count)) / (count + 1)
                        val chartHeight = size.height

                        // Target reference line
                        val targetY = chartHeight - (targetHours / maxHours) * chartHeight
                        drawLine(
                            color = NeonCyan.copy(alpha = 0.35f),
                            start = Offset(0f, targetY),
                            end = Offset(size.width, targetY),
                            strokeWidth = 1.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Bars
                        days.forEachIndexed { i, day ->
                            val barHeight = (day.hours / maxHours) * chartHeight
                            val x = spacing + i * (barWidth + spacing)
                            val y = chartHeight - barHeight

                            val barColor = if (day.isToday) {
                                NeonCyan
                            } else if (day.hours >= targetHours) {
                                RadiantEmerald
                            } else if (day.hours > 0f) {
                                AmberFlame
                            } else {
                                FocusOutline
                            }

                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, maxOf(barHeight, 4.dp.toPx())),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )
                        }
                    }
                }

                // Day labels underneath
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    days.forEach { day ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day.dayLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (day.isToday) NeonCyan else TextSecondary
                                )
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%.1fh", day.hours),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = if (day.hours > 0f) TextPrimary else TextMuted
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartRecommendationsCard(
    recommendations: List<String>,
    lang: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("smart_recommendations_card"),
        colors = CardDefaults.cardColors(containerColor = FocusSurfaceElevated),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = AmberFlame,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Strings.get("smart_recommendations", lang),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            recommendations.forEach { tip ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}
