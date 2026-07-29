package com.shoujopomodoro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shoujopomodoro.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

data class DailyStats(
    val date: String,
    val totalMs: Long
)

@Dao
interface FocusSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: FocusSessionEntity)

    @Query("SELECT COALESCE(SUM(durationMs), 0) FROM focus_sessions WHERE date = :date")
    fun getTodayTotalMs(date: String): Flow<Long>

    @Query("SELECT COALESCE(SUM(durationMs), 0) FROM focus_sessions WHERE date IN (:dates)")
    fun getWeekTotalMs(dates: List<String>): Flow<Long>

    @Query("SELECT date, SUM(durationMs) AS totalMs FROM focus_sessions WHERE date LIKE :yearMonth || '%' GROUP BY date ORDER BY date ASC")
    fun getMonthlyStats(yearMonth: String): Flow<List<DailyStats>>

    @Query("SELECT * FROM focus_sessions ORDER BY completedAt DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>
}
