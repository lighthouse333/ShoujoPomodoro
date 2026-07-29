package com.shoujopomodoro.data.repository

import com.shoujopomodoro.data.local.dao.DailyStats
import com.shoujopomodoro.data.local.dao.FocusSessionDao
import com.shoujopomodoro.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

class FocusSessionRepositoryImpl(
    private val dao: FocusSessionDao
) : FocusSessionRepository {

    override suspend fun recordSession(date: String, durationMs: Long) {
        dao.insert(
            FocusSessionEntity(
                date = date,
                durationMs = durationMs
            )
        )
    }

    override fun getTodayTotalMs(date: String): Flow<Long> = dao.getTodayTotalMs(date)

    override fun getWeekTotalMs(dates: List<String>): Flow<Long> = dao.getWeekTotalMs(dates)

    override fun getMonthlyStats(yearMonth: String): Flow<List<DailyStats>> = dao.getMonthlyStats(yearMonth)
}
