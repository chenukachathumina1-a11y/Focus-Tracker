package com.example.data.model

enum class AppLanguage(val code: String, val labelEn: String, val labelSi: String) {
    EN("en", "English", "English (ඉංග්‍රීසි)"),
    SI("si", "Sinhala", "සිංහල")
}

enum class ExamType(val code: String, val nameEn: String, val nameSi: String) {
    AL("AL", "G.C.E. Advanced Level (A/L)", "උසස් පෙළ (A/L)"),
    OL("OL", "G.C.E. Ordinary Level (O/L)", "සාමාන්‍ය පෙළ (O/L)")
}

enum class ALStream(
    val code: String,
    val nameEn: String,
    val nameSi: String,
    val descriptionEn: String,
    val descriptionSi: String
) {
    MATHS(
        "MATHS",
        "Physical Science (Maths)",
        "භෞතික විද්‍යා (ගණිත) අංශය",
        "Combined Maths, Physics + Chemistry or ICT",
        "සංයුක්ත ගණිතය, භෞතික විද්‍යාව + රසායන විද්‍යාව හෝ ICT"
    ),
    BIO(
        "BIO",
        "Biological Science (Bio)",
        "ජීව විද්‍යා අංශය",
        "Biology, Chemistry + Physics or Agriculture",
        "ජීව විද්‍යාව, රසායන විද්‍යාව + භෞතික විද්‍යාව හෝ කෘෂි විද්‍යාව"
    ),
    COMMERCE(
        "COMMERCE",
        "Commerce Stream",
        "වාණිජ අංශය",
        "Accounting, Business Studies, Economics, etc.",
        "ගිණුම්කරණය, ව්‍යාපාර අධ්‍යයනය, ආර්ථික විද්‍යාව ආදිය"
    ),
    ARTS(
        "ARTS",
        "Arts Stream",
        "කලා අංශය",
        "Languages, Social Sciences, Aesthetics, etc.",
        "භාෂා, සමාජ විද්‍යා, සෞන්දර්ය විෂයයන් ආදිය"
    ),
    ETECH(
        "ETECH",
        "Engineering Technology (E-Tech)",
        "ඉංජිනේරු තාක්ෂණවේදය (E-Tech)",
        "Engineering Tech, Science for Tech + 1 Basket Subject",
        "ඉංජිනේරු තාක්ෂණවේදය, තාක්ෂණවේදය සඳහා විද්‍යාව + 1 තේරීම් විෂයයක්"
    ),
    BTECH(
        "BTECH",
        "Bio-systems Technology (B-Tech)",
        "ජෛව පද්ධති තාක්ෂණවේදය (B-Tech)",
        "Bio-systems Tech, Science for Tech + 1 Basket Subject",
        "ජෛව පද්ධති තාක්ෂණවේදය, තාක්ෂණවේදය සඳහා විද්‍යාව + 1 තේරීම් විෂයයක්"
    )
}
