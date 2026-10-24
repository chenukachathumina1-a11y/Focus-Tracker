# Focus Tracking Application for A/L & O/L Science & Stream Students

A specialized Android focus and study tracking application crafted for Sri Lankan G.C.E. Advanced Level (A/L) and Ordinary Level (O/L) students. Features stream-specific subject configuration (Maths, Bio, Commerce, Arts, Tech), strict 3-subject validation rules, separate "Other Focus" tracking, customizable morning wake-up alarms, lesson chapter management, a glowing circular countdown timer with background notifications, and daily/weekly productivity analytics (pie charts, bar graphs, and smart balance recommendations) in both English and Sinhala.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following user preferences were confirmed during Phase 1:
> - **Visual Theme**: Deep Focus Dark Mode with circular glowing timer and distinctive neon-accented subject palettes.
> - **Recommendation Engine**: Smart rule-based analysis with subject balance targets comparing actual study hours against ideal stream allocations.
> - **Timer Style**: Standard countdown timer with flexible background tracking and notification alerts.
> - **Languages**: English and Sinhala (සිංහල) with on-device toggle and first-launch localization.

---

### 1. Overview & Core Concept

- **What It Does**: Provides Sri Lankan students with an organized, distraction-free study companion tailored specifically to their exam syllabi. Students configure their exam and stream, track dedicated focus hours per subject or chapter, set morning study alarms with custom ringtones, schedule focus sessions, and review daily/weekly graphical analytics with actionable study recommendations.
- **Target Audience**: Sri Lankan A/L students (Physical Science/Maths, Biological Science, Commerce, Arts, Engineering Technology, Bio-systems Technology) and O/L students preparing for national examinations.
- **Key Value**: Eliminates syllabus confusion with strict curriculum validation (e.g., permanent Combined Maths & Physics for Maths students), ensures balanced study distribution via visual analytics, and boosts consistency through customizable morning alarms and session scheduling.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Onboarding & First-Run Setup**:
   - Language Selection (English / සිංහල).
   - Exam Selection (G.C.E. O/L or G.C.E. A/L).
   - If A/L: Stream Selection (Physical Science / Maths, Biological Science, Commerce, Arts, E-Tech, B-Tech).
   - Subject Configuration:
     * **Maths**: Combined Maths & Physics locked as permanent; student chooses 3rd subject (Chemistry or ICT).
     * **Bio**: Biology & Chemistry locked as permanent; student chooses 3rd subject (Physics or Agriculture).
     * **Commerce / Arts / Tech**: Student selects or inputs their 3 stream subjects.
     * Enforces the 3-subject limit while offering an independent "Other Focus" category.
   - If O/L: Standard 6 core subjects (Science, Maths, English, Sinhala, History, Religion) + 3 Basket selections.
2. **Dashboard / Focus Hub**:
   - Quick subject cards showing today's studied hours vs. goal.
   - Lesson tag selector (e.g., "Rotational Dynamics", "Organic Reactions").
   - Large circular countdown timer with glowing animated progress ring, quick duration presets (25m, 45m, 60m, 90m, custom), start/pause/reset.
   - Separate "Other Focus" toggle for non-exam learning (languages, extra-curriculars, reading).
3. **Analytics & Study Insights**:
   - Daily & Weekly tabs.
   - Interactive Canvas-rendered Pie Chart showing subject distribution percentage vs. Other Focus.
   - Day-by-day Bar Graph comparing daily focus hours across Monday–Sunday.
   - Smart Rule-Based Recommendations card (e.g., alert if Chemistry study time is below 20% of total weekly hours or if study sessions are imbalanced).
4. **Alarm & Schedule Manager**:
   - Morning Wake-Up Alarm with toggle, time picker, repeat days, and custom ringtone/sound selector with preview.
   - Focus Hour Scheduler to set planned study blocks for the day.
   - Notification channel for session completion with custom alert sound.
5. **Profile & Syllabus Settings**:
   - Switch language between English and Sinhala anytime.
   - Change Exam (O/L vs A/L) and modify Stream/Subjects without losing historical study logs.
   - Manage lessons/chapters per subject.

#### Visual Identity & Theme
- **Theme**: Deep Focus Obsidian Dark (`#0B0E14` background, `#161B26` surface cards, `#232B3E` elevated containers).
- **Accents**: Neon Cyan (`#00E5FF`), Electric Indigo (`#6366F1`), Radiant Emerald (`#10B981`), Amber Flame (`#F59E0B`), and Rose (`#F43F5E`) for distinct subject identities.
- **Typography**: Clean, high-legibility sans-serif typography with native Unicode support for flawless Sinhala glyph rendering.
- **Micro-Interactions**: Smooth Canvas arc sweeps, pulsing glowing effects on active timer, tactile haptic feedback on timer buttons, and fluid M3 transitions.

---

### 3. Key Product Decisions & Trade-Offs

- **Data Persistence via Room Database**:
  - *Chosen Approach*: Embedded SQLite database via Room with Kotlin Coroutines and Flows for reactive updates.
  - *Why*: Study sessions, custom lessons, alarms, and historical chart data require reliable on-device persistence without requiring an internet connection or sign-in gate.
- **Audio & Alarm Management via Android AlarmManager & RingtoneManager**:
  - *Chosen Approach*: Exact alarms with `AlarmManager` and `BroadcastReceiver`, paired with `RingtoneManager` allowing selection between system alarm sounds and bundled study tones.
  - *Why*: Guarantees reliable morning wake-ups and session completion chimes even if the app is in the background.
- **Bilingual Localization System**:
  - *Chosen Approach*: Dynamic in-app language provider storing preference in preferences, dynamically delivering string resources and formatting in English and Sinhala (සිංහල).
  - *Why*: Enables seamless instant switching without requiring the user to change their entire Android device system language.
- **Subject Validation Logic**:
  - *Chosen Approach*: Strict model validation engine enforcing Sri Lankan national exam rules:
    * Maths stream: Permanent [Combined Maths, Physics] + Choice of [Chemistry | ICT].
    * Bio stream: Permanent [Biology, Chemistry] + Choice of [Physics | Agriculture].
    * Total A/L core subjects capped strictly at 3.
    * "Other Focus" tracked under an isolated category identifier to preserve curriculum analytics accuracy.

---

### 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                      Jetpack Compose UI                     │
├──────────────┬──────────────┬──────────────┬────────────────┤
│  Onboarding  │  Focus Timer │  Analytics   │ Alarm & Profile│
│  Exam/Stream │ Glowing Ring │ Pie & Bar    │ Ringtone Picker│
│  Setup View  │ Lesson Tag   │ Smart Advice │ Sinhala Toggle │
└──────┬───────┴──────┬───────┴──────┬───────┴────────┬───────┘
       │              │              │                │
┌──────▼──────────────▼──────────────▼────────────────▼───────┐
│                       ViewModels                            │
│   (AppViewModel, FocusTimerViewModel, AnalyticsViewModel)   │
└──────────────────────────────┬──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                    FocusRepository                          │
│   (Coordinates Room DB, Alarm Scheduling & Preferences)    │
└──────────────┬──────────────────────────────┬───────────────┘
               │                              │
┌──────────────▼──────────────┐┌──────────────▼───────────────┐
│        Room Database        ││    Services / Receivers      │
│ - UserProfileEntity         ││ - FocusAlarmReceiver         │
│ - SubjectEntity             ││ - FocusNotificationService   │
│ - LessonEntity              ││ - AlarmManager Controller    │
│ - StudySessionEntity        ││ - Ringtone Player            │
└─────────────────────────────┘└──────────────────────────────┘
```

#### Core Database Entities
- **UserProfile**: `id`, `examType` (OL, AL), `alStream` (MATHS, BIO, COMMERCE, ARTS, ETECH, BTECH), `language` (EN, SI), `morningAlarmHour`, `morningAlarmMinute`, `morningAlarmEnabled`, `morningAlarmRingtoneUri`.
- **Subject**: `id`, `nameEn`, `nameSi`, `stream`, `isPermanent`, `colorHex`, `orderIndex`, `isCustom`.
- **Lesson**: `id`, `subjectId`, `title`, `isCompleted`.
- **StudySession**: `id`, `subjectId` (or null for "Other Focus"), `lessonId` (optional), `durationSeconds`, `timestamp`, `dateString` (YYYY-MM-DD), `notes`, `isOtherFocus`.

#### Verification & Quality Gates
- Code cleanly compiled via `compile_applet`.
- Full edge-to-edge support with `WindowInsets.safeDrawing`.
- TestTags attached to primary actions for testability.
- No deprecated APIs or unhandled state transitions.
