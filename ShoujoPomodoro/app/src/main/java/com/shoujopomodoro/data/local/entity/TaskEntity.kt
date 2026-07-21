package com.shoujopomodoro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isCompleted: Boolean = false,
    val isCurrentTask: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
