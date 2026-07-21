package com.shoujopomodoro.domain.model

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    CHINESE("zh", "中文");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code == code } ?: ENGLISH
    }
}
