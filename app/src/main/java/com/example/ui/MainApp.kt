package com.example.ui

import android.app.Activity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.localization.Strings
import com.example.ui.screens.AlarmSettingsScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.FocusTimerScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SubjectsScreen
import com.example.ui.theme.FocusObsidianDark
import com.example.ui.theme.FocusSurface
import com.example.ui.theme.FocusSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.AppViewModel

@Composable
fun MainApp(viewModel: AppViewModel = viewModel()) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val keepScreenOn by viewModel.keepScreenOn.collectAsStateWithLifecycle()

    var isReconfiguringCurriculum by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Keep screen on logic
    val context = LocalContext.current
    DisposableEffect(keepScreenOn) {
        val window = (context as? Activity)?.window
        if (keepScreenOn) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val isProfileConfigured = profile?.isConfigured == true

    if (!isProfileConfigured || isReconfiguringCurriculum) {
        OnboardingScreen(
            onComplete = { selectedLang, examType, alStream, subjectIds, customSubjects ->
                viewModel.completeOnboarding(
                    language = selectedLang,
                    examType = examType,
                    alStream = alStream,
                    selectedSubjectIds = subjectIds,
                    customSubjects = customSubjects
                )
                isReconfiguringCurriculum = false
                selectedTab = 0
            }
        )
    } else {
        Scaffold(
            containerColor = FocusObsidianDark,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = FocusSurface,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_navigation_bar"),
                    tonalElevation = 8.dp
                ) {
                    val tabs = listOf(
                        Triple(
                            Strings.get("nav_focus", lang),
                            Icons.Filled.HourglassBottom to Icons.Outlined.HourglassBottom,
                            0
                        ),
                        Triple(
                            Strings.get("nav_subjects", lang),
                            Icons.Filled.School to Icons.Outlined.School,
                            1
                        ),
                        Triple(
                            Strings.get("nav_analytics", lang),
                            Icons.Filled.Analytics to Icons.Outlined.Analytics,
                            2
                        ),
                        Triple(
                            Strings.get("nav_alarm_settings", lang),
                            Icons.Filled.Alarm to Icons.Outlined.Alarm,
                            3
                        )
                    )

                    tabs.forEach { (title, icons, index) ->
                        val isSelected = selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) icons.first else icons.second,
                                    contentDescription = title
                                )
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FocusObsidianDark,
                                indicatorColor = NeonCyan,
                                unselectedIconColor = TextMuted,
                                selectedTextColor = NeonCyan,
                                unselectedTextColor = TextMuted
                            ),
                            modifier = Modifier.testTag("nav_item_$index")
                        )
                    }
                }
            }
        ) { padding ->
            // Handle Back Button inside secondary tabs to return to Focus Timer
            if (selectedTab != 0) {
                BackHandler {
                    selectedTab = 0
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (selectedTab) {
                    0 -> FocusTimerScreen(viewModel = viewModel)
                    1 -> SubjectsScreen(viewModel = viewModel)
                    2 -> AnalyticsScreen(viewModel = viewModel)
                    3 -> AlarmSettingsScreen(
                        viewModel = viewModel,
                        onChangeCurriculumRequested = {
                            isReconfiguringCurriculum = true
                        }
                    )
                }
            }
        }
    }
}
