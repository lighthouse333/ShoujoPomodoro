package com.shoujopomodoro.domain.model

data class Task(
    val id: Long = 0,
    val name: String,
    val isCompleted: Boolean = false,
    val isCurrentTask: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
