package com.example.data.model

data class SubjectBlueprint(
    val id: String,
    val nameEn: String,
    val nameSi: String,
    val colorHex: String,
    val isPermanent: Boolean = false,
    val isDefaultSelected: Boolean = false
)

object PresetCurriculum {

    // A/L Physical Science (Maths)
    val mathsPermanent = listOf(
        SubjectBlueprint(
            id = "al_comb_maths",
            nameEn = "Combined Mathematics",
            nameSi = "සංයුක්ත ගණිතය",
            colorHex = "#00E5FF", // Neon Cyan
            isPermanent = true,
            isDefaultSelected = true
        ),
        SubjectBlueprint(
            id = "al_physics",
            nameEn = "Physics",
            nameSi = "භෞතික විද්‍යාව",
            colorHex = "#6366F1", // Electric Indigo
            isPermanent = true,
            isDefaultSelected = true
        )
    )

    val mathsElectives = listOf(
        SubjectBlueprint(
            id = "al_chemistry",
            nameEn = "Chemistry",
            nameSi = "රසායන විද්‍යාව",
            colorHex = "#10B981", // Emerald
            isPermanent = false,
            isDefaultSelected = true
        ),
        SubjectBlueprint(
            id = "al_ict",
            nameEn = "Information & Comm. Tech (ICT)",
            nameSi = "තොරතුරු හා සන්නිවේදන තාක්ෂණය (ICT)",
            colorHex = "#F59E0B", // Amber
            isPermanent = false,
            isDefaultSelected = false
        )
    )

    // A/L Biological Science (Bio)
    val bioPermanent = listOf(
        SubjectBlueprint(
            id = "al_biology",
            nameEn = "Biology",
            nameSi = "ජීව විද්‍යාව",
            colorHex = "#10B981", // Emerald
            isPermanent = true,
            isDefaultSelected = true
        ),
        SubjectBlueprint(
            id = "al_chemistry",
            nameEn = "Chemistry",
            nameSi = "රසායන විද්‍යාව",
            colorHex = "#00E5FF", // Cyan
            isPermanent = true,
            isDefaultSelected = true
        )
    )

    val bioElectives = listOf(
        SubjectBlueprint(
            id = "al_physics",
            nameEn = "Physics",
            nameSi = "භෞතික විද්‍යාව",
            colorHex = "#6366F1", // Indigo
            isPermanent = false,
            isDefaultSelected = true
        ),
        SubjectBlueprint(
            id = "al_agri",
            nameEn = "Agricultural Science",
            nameSi = "කෘෂි විද්‍යාව",
            colorHex = "#84CC16", // Lime
            isPermanent = false,
            isDefaultSelected = false
        )
    )

    // A/L Commerce
    val commerceSubjects = listOf(
        SubjectBlueprint("al_acc", "Accounting", "ගිණුම්කරණය", "#10B981", false, true),
        SubjectBlueprint("al_bs", "Business Studies", "ව්‍යාපාර අධ්‍යයනය", "#3B82F6", false, true),
        SubjectBlueprint("al_econ", "Economics", "ආර්ථික විද්‍යාව", "#F59E0B", false, true),
        SubjectBlueprint("al_ict", "Information Tech (ICT)", "තොරතුරු තාක්ෂණය", "#8B5CF6", false, false),
        SubjectBlueprint("al_bstat", "Business Statistics", "ව්‍යාපාර සංඛ්‍යානය", "#EC4899", false, false)
    )

    // A/L Arts
    val artsSubjects = listOf(
        SubjectBlueprint("al_sinhala", "Sinhala", "සිංහල සාහිත්‍යය", "#F59E0B", false, true),
        SubjectBlueprint("al_pol_sci", "Political Science", "දේශපාලන විද්‍යාව", "#3B82F6", false, true),
        SubjectBlueprint("al_geog", "Geography", "භූගෝල විද්‍යාව", "#10B981", false, true),
        SubjectBlueprint("al_logic", "Logic & Scientific Method", "තර්ක ශාස්ත්‍රය", "#8B5CF6", false, false),
        SubjectBlueprint("al_buddhist_cult", "Buddhist Civilization", "බෞද්ධ ශිෂ්ටාචාරය", "#F97316", false, false),
        SubjectBlueprint("al_history", "History", "ඉතිහාසය", "#EC4899", false, false),
        SubjectBlueprint("al_media", "Media & Comm. Studies", "සන්නිවේදනය හා මාධ්‍ය", "#06B6D4", false, false)
    )

    // A/L Engineering Technology (E-Tech)
    val etechSubjects = listOf(
        SubjectBlueprint("al_et", "Engineering Technology (ET)", "ඉංජිනේරු තාක්ෂණවේදය (ET)", "#00E5FF", true, true),
        SubjectBlueprint("al_sft", "Science for Technology (SFT)", "තාක්ෂණවේදය සඳහා විද්‍යාව (SFT)", "#6366F1", true, true),
        SubjectBlueprint("al_ict", "Information Tech (ICT)", "තොරතුරු තාක්ෂණය (ICT)", "#F59E0B", false, true),
        SubjectBlueprint("al_econ", "Economics", "ආර්ථික විද්‍යාව", "#10B981", false, false),
        SubjectBlueprint("al_geog", "Geography", "භූගෝල විද්‍යාව", "#EC4899", false, false)
    )

    // A/L Bio-systems Technology (B-Tech)
    val btechSubjects = listOf(
        SubjectBlueprint("al_bst", "Bio-systems Technology (BST)", "ජෛව පද්ධති තාක්ෂණවේදය (BST)", "#10B981", true, true),
        SubjectBlueprint("al_sft", "Science for Technology (SFT)", "තාක්ෂණවේදය සඳහා විද්‍යාව (SFT)", "#6366F1", true, true),
        SubjectBlueprint("al_agri_tech", "Agricultural Technology", "කෘෂි තාක්ෂණය", "#84CC16", false, true),
        SubjectBlueprint("al_ict", "Information Tech (ICT)", "තොරතුරු තාක්ෂණය (ICT)", "#F59E0B", false, false),
        SubjectBlueprint("al_econ", "Economics", "ආර්ථික විද්‍යාව", "#3B82F6", false, false)
    )

    // G.C.E. O/L Standard Curriculum Modules
    val olSubjects = listOf(
        SubjectBlueprint("ol_science", "Science", "විද්‍යාව", "#00E5FF", true, true),
        SubjectBlueprint("ol_maths", "Mathematics", "ගණිතය", "#6366F1", true, true),
        SubjectBlueprint("ol_english", "English Language", "ඉංග්‍රීසි භාෂාව", "#3B82F6", true, true),
        SubjectBlueprint("ol_sinhala", "Sinhala Language & Lit.", "සිංහල භාෂාව හා සාහිත්‍යය", "#F59E0B", true, true),
        SubjectBlueprint("ol_history", "History", "ඉතිහාසය", "#EC4899", true, true),
        SubjectBlueprint("ol_religion", "Religion (Buddhism/Other)", "ආගම (බුද්ධ ධර්මය/වෙනත්)", "#F97316", true, true),
        SubjectBlueprint("ol_basket1", "Basket 1 (Commerce/Geog/Civics)", "කාණ්ඩය 1 (වාණිජ/භූගෝල/පුරවැසි)", "#10B981", false, true),
        SubjectBlueprint("ol_basket2", "Basket 2 (Aesthetic: Art/Music/Dance)", "කාණ්ඩය 2 (සෞන්දර්ය: චිත්‍ර/සංගීත/නර්තන)", "#8B5CF6", false, true),
        SubjectBlueprint("ol_basket3", "Basket 3 (ICT/Agri/Health)", "කාණ්ඩය 3 (තොරතුරු තාක්ෂණය/කෘෂි/සෞඛ්‍ය)", "#06B6D4", false, true)
    )

    // Special Separate Category: Other Focus Time
    val otherFocusSubject = SubjectBlueprint(
        id = "other_focus_special",
        nameEn = "Other Focus Time",
        nameSi = "වෙනත් අවධානය යොමු කළ කාලය",
        colorHex = "#A855F7" // Electric Purple
    )
}
