package com.shoujopomodoro.data.repository

import com.shoujopomodoro.data.local.dao.DailyStats
import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    suspend fun recordSession(date: String, durationMs: Long)
    fun getTodayTotalMs(date: String): Flow<Long>
    fun getWeekTotalMs(dates: List<String>): Flow<Long>
    fun getMonthlyStats(yearMonth: String): Flow<List<DailyStats>>
}
