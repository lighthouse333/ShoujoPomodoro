package com.shoujopomodoro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shoujopomodoro.data.local.dao.FocusSessionDao
import com.shoujopomodoro.data.local.dao.TaskDao
import com.shoujopomodoro.data.local.entity.FocusSessionEntity
import com.shoujopomodoro.data.local.entity.TaskEntity

@Database(entities = [TaskEntity::class, FocusSessionEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun focusSessionDao(): FocusSessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shoujo_pomodoro_db"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
