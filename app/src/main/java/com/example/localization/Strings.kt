package com.example.localization

object Strings {

    fun get(key: String, lang: String): String {
        val isSi = lang == "si"
        return when (key) {
            // General & Common
            "app_title" -> if (isSi) "ෆෝකස් ට්‍රැකර්" else "Focus Tracker"
            "app_tagline" -> if (isSi) "උසස් පෙළ හා සාමාන්‍ය පෙළ සිසුන් සඳහා" else "For A/L & O/L Students"
            "save" -> if (isSi) "සුරකින්න" else "Save"
            "cancel" -> if (isSi) "අවලංගු කරන්න" else "Cancel"
            "done" -> if (isSi) "අවසන්" else "Done"
            "delete" -> if (isSi) "මකන්න" else "Delete"
            "confirm" -> if (isSi) "තහවුරු කරන්න" else "Confirm"
            "edit" -> if (isSi) "වෙනස් කරන්න" else "Edit"
            "add" -> if (isSi) "එකතු කරන්න" else "Add"
            "close" -> if (isSi) "වසන්න" else "Close"
            "loading" -> if (isSi) "පූරණය වෙමින්..." else "Loading..."

            // Navigation
            "nav_focus" -> if (isSi) "අවධානය" else "Focus"
            "nav_subjects" -> if (isSi) "විෂයයන්" else "Subjects"
            "nav_analytics" -> if (isSi) "විශ්ලේෂණ" else "Analytics"
            "nav_alarm_settings" -> if (isSi) "ඇලර්ම් සහ සැකසුම්" else "Alarm & Settings"

            // Onboarding
            "onboarding_welcome" -> if (isSi) "සාදරයෙන් පිළිගනිමු!" else "Welcome to Focus Tracker"
            "onboarding_desc" -> if (isSi) "ඔබේ පාඩම් කාලය හා ඉලක්ක ක්‍රමවත්ව සැලසුම් කරමු." else "Plan, track, and master your study routine."
            "step_language" -> if (isSi) "භාෂාව තෝරන්න" else "Select Language"
            "step_exam" -> if (isSi) "විභාගය තෝරන්න" else "Select Your Exam"
            "step_stream" -> if (isSi) "විෂය ධාරාව තෝරන්න" else "Select A/L Stream"
            "step_subjects" -> if (isSi) "විෂයයන් තහවුරු කරන්න" else "Confirm Your Subjects"
            "select_stream_subtitle" -> if (isSi) "ඔබේ උසස් පෙළ විෂය ධාරාව තෝරන්න" else "Choose your G.C.E. A/L stream"
            "permanent_subject_hint" -> if (isSi) "නියමිත ස්ථිර විෂයයන් (වෙනස් කළ නොහැක)" else "Permanent Subjects (Compulsory)"
            "elective_subject_hint" -> if (isSi) "3 වන විෂය තෝරන්න (එක් විෂයයක් පමණි)" else "Choose 3rd Subject (Pick exactly 1)"
            "must_select_3_al" -> if (isSi) "උසස් පෙළ සඳහා තෝරාගත හැක්කේ විෂයයන් 3ක් පමණි." else "You must select exactly 3 subjects for A/L."
            "ol_subjects_hint" -> if (isSi) "සාමාන්‍ය පෙළ ප්‍රධාන මොඩියුල 9" else "Standard 9 O/L Subject Modules"
            "other_focus_notice" -> if (isSi) "වෙනත් පාඩම්/කියවීම් සඳහා 'වෙනත් අවධානය' (Other Focus) වෙනම ගණනය වේ." else "'Other Focus Time' is always available and counted separately."
            "get_started" -> if (isSi) "ආරම්භ කරමු" else "Get Started"
            "next" -> if (isSi) "ඊළඟ" else "Next"
            "back" -> if (isSi) "ආපසු" else "Back"

            // Focus Timer Screen
            "focus_mode" -> if (isSi) "අධ්‍යයන අවධානය" else "Deep Focus"
            "select_subject" -> if (isSi) "විෂය තෝරන්න" else "Select Subject"
            "select_lesson" -> if (isSi) "පාඩම / පරිච්ඡේදය තෝරන්න" else "Select Lesson / Chapter"
            "no_lesson_selected" -> if (isSi) "සාමාන්‍ය පාඩම් කිරීම (පාඩමක් නැත)" else "General Study (No Lesson Tag)"
            "other_focus_mode" -> if (isSi) "වෙනත් අවධානය (Other Focus)" else "Other Focus Time"
            "other_focus_badge" -> if (isSi) "වෙනම ගණනය වේ" else "Counted Separately"
            "start_focus" -> if (isSi) "ආරම්භ කරන්න" else "Start Focus"
            "pause_focus" -> if (isSi) "විරාමය" else "Pause"
            "resume_focus" -> if (isSi) "නැවත අරඹන්න" else "Resume"
            "stop_finish" -> if (isSi) "අවසන් කරන්න" else "Finish & Log"
            "reset" -> if (isSi) "නැවත සකසන්න" else "Reset"
            "time_remaining" -> if (isSi) "ඉතිරි කාලය" else "Time Remaining"
            "keep_screen_on" -> if (isSi) "තිරය සක්‍රීයව තබන්න (Always Active)" else "Keep Screen Always Active"
            "display_over_other" -> if (isSi) "අවධානයෙන් පාඩම් කරන්න" else "Stay In Focus"
            "quick_durations" -> if (isSi) "කාල පරාසයන්" else "Presets"
            "custom_minutes" -> if (isSi) "අභිමත මිනිත්තු" else "Custom Min"
            "session_saved" -> if (isSi) "පාඩම් කාලය සාර්ථකව සටහන් විය!" else "Focus session successfully logged!"
            "today_total" -> if (isSi) "අද මුළු කාලය" else "Today's Total"

            // Subjects & Profile Screen
            "profile_dashboard" -> if (isSi) "මගේ විෂය පැතිකඩ" else "Personalized Dashboard"
            "your_subjects" -> if (isSi) "ඔබේ විෂයයන්" else "Your Subjects"
            "add_custom_subject" -> if (isSi) "නව විෂයයක් එක් කරන්න" else "Add Custom Subject"
            "subject_name_placeholder" -> if (isSi) "විෂය නම ඇතුළත් කරන්න" else "Enter Subject Name"
            "lessons_for_subject" -> if (isSi) "පාඩම් හා පරිච්ඡේද" else "Lessons & Chapters"
            "add_lesson" -> if (isSi) "පාඩමක් එක් කරන්න" else "Add Lesson"
            "lesson_name_placeholder" -> if (isSi) "පාඩමේ / මාතෘකාවේ නම" else "Enter Lesson Title"
            "no_lessons_yet" -> if (isSi) "තවම පාඩම් එක් කර නැත. නව පාඩමක් එක් කරන්න." else "No lessons added yet. Add your syllabus chapters."
            "completed" -> if (isSi) "සම්පූර්ණයි" else "Completed"
            "in_progress" -> if (isSi) "කරමින් පවතී" else "In Progress"

            // Analytics Screen
            "analytics_title" -> if (isSi) "පාඩම් කාල විශ්ලේෂණය" else "Productivity Analytics"
            "daily_view" -> if (isSi) "දිනපතා" else "Daily"
            "weekly_view" -> if (isSi) "සතිපතා" else "Weekly"
            "study_distribution" -> if (isSi) "විෂය බෙදීයාම (Pie Chart)" else "Subject Distribution"
            "weekly_bar_chart" -> if (isSi) "සතියේ දින අනුව පැය ගණන (Bar Chart)" else "Weekly Daily Focus Hours"
            "smart_recommendations" -> if (isSi) "බුද්ධිමත් අධ්‍යයන නිර්දේශ" else "Smart Study Recommendations"
            "no_sessions_yet" -> if (isSi) "තවම සටහන් වූ පාඩම් කාලයක් නැත. අදම පළමු සැසිය ආරම්භ කරන්න!" else "No study sessions recorded yet. Start your first focus session today!"
            "total_study_time" -> if (isSi) "මුළු පාඩම් කාලය" else "Total Study Time"
            "core_subjects_time" -> if (isSi) "ප්‍රධාන විෂයයන්" else "Core Exam Subjects"
            "other_focus_time_label" -> if (isSi) "වෙනත් අවධානය" else "Other Focus"
            "hours_abbr" -> if (isSi) "පැය" else "hrs"
            "mins_abbr" -> if (isSi) "මිනි" else "mins"

            // Alarm & Settings Screen
            "alarm_settings_title" -> if (isSi) "ඇලර්ම් සහ කාලසටහන" else "Alarm & Schedule"
            "morning_alarm" -> if (isSi) "උදෑසන අවදිවීමේ ඇලර්මය" else "Morning Getting Up Alarm"
            "alarm_sound" -> if (isSi) "ඇලර්ම් නාදය (Ringtone)" else "Alarm Ringtone"
            "test_sound" -> if (isSi) "නාදය ශ්‍රවණය කරන්න" else "Preview Tone"
            "stop_sound" -> if (isSi) "නවතන්න" else "Stop Sound"
            "focus_alert_sound" -> if (isSi) "සැසිය අවසන් වීමේ ශබ්දය" else "Focus Completion Sound"
            "notification_sound_desc" -> if (isSi) "නියමිත කාලය අවසන් වූ විට ශබ්දය නාද වේ." else "Plays sound when focus time ends."
            "schedule_focus_hours" -> if (isSi) "පාඩම් කාලය කලින් සැලසුම් කරන්න" else "Schedule Focus Hours"
            "add_schedule" -> if (isSi) "නව සැලසුමක්" else "Add Schedule"
            "time_label" -> if (isSi) "වේලාව" else "Time"
            "duration_label" -> if (isSi) "කාලය" else "Duration"
            "change_exam_subject" -> if (isSi) "විභාගය හෝ විෂයයන් වෙනස් කරන්න" else "Change Exam & Subjects"
            "language_setting" -> if (isSi) "භාෂාව / Language" else "Language / භාෂාව"
            "switch_to_sinhala" -> "සිංහල වෙත මාරු වන්න"
            "switch_to_english" -> "Switch to English"

            // Alarm Ringtones
            "tone_sunrise" -> if (isSi) "සුබ උදෑසන සීනුව (Sunrise Bells)" else "Gentle Sunrise Bells"
            "tone_temple" -> if (isSi) "සන්සුන් සීනුව (Calm Temple Chime)" else "Calm Temple Chime"
            "tone_birds" -> if (isSi) "උදෑසන කුරුළු නාදය (Morning Birds)" else "Morning Forest Birds"
            "tone_cyber" -> if (isSi) "ඩිජිටල් ඇලර්මය (Digital Beep)" else "Energetic Digital Pulse"
            "tone_system" -> if (isSi) "දුරකථන පෙරනිමි නාදය" else "Default System Alarm"

            // Recommendation texts
            "rec_balanced" -> if (isSi) "විශිෂ්ටයි! ඔබගේ සියලු ප්‍රධාන විෂයයන් අතර මනා සමතුලිතතාවයක් පවතී." else "Great balance! Your focus time is distributed well across all your exam subjects."
            "rec_neglected_prefix" -> if (isSi) "අවධානය යොමු කරන්න: ඔබ " else "Attention needed: You have spent less than 20% on "
            "rec_neglected_suffix" -> if (isSi) " විෂය සඳහා යොදවා ඇති කාලය ප්‍රමාණවත් නොවේ. අද එම විෂය සඳහා වැඩි කාලයක් වෙන් කරන්න." else ". Dedicate an extra session to it today to maintain balance."
            "rec_maths_tip" -> if (isSi) "ගණිත සිසුන් සඳහා: සංයුක්ත ගණිත ගැටලු දිනපතා අවම වශයෙන් පැය 2ක් වත් පුහුණු වන්න." else "Maths Stream Tip: Practice Combined Mathematics problems daily for at least 2 hours."
            "rec_bio_tip" -> if (isSi) "ජීව විද්‍යා සිසුන් සඳහා: රූප සටහන් සහ ප්‍රධාන සංකල්ප සතිපතා කෙටි සටහන් ලෙස නැවත මතක් කරගන්න." else "Bio Stream Tip: Review Biology structured diagrams and active recall notes regularly."
            "rec_morning_tip" -> if (isSi) "උදෑසන 5:30 අවදිවී පාඩම් කිරීම මතක තබාගැනීම 40% කින් වැඩි කරයි." else "Early morning study sessions between 5:30 - 7:30 AM yield maximum cognitive retention."
            "rec_other_focus_balanced" -> if (isSi) "ඔබේ වෙනත් අධ්‍යයන කාලය (Other Focus) නිසි පරිදි වෙන්ව ගණනය වී ඇති අතර විභාග විෂයයන්ට බලපෑමක් නැත." else "Your Other Focus time is tracked independently without skewing core exam analytics."

            else -> key
        }
    }
}
