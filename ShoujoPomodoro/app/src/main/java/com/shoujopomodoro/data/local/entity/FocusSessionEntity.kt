package com.shoujopomodoro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,          // "YYYY-MM-DD" for daily grouping
    val durationMs: Long,      // completed focus session duration in milliseconds
    val completedAt: Long = System.currentTimeMillis()  // epoch timestamp of completion
)
