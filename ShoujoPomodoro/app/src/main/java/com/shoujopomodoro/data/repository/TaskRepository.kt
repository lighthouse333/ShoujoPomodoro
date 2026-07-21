package com.shoujopomodoro.data.repository

import com.shoujopomodoro.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeAll(): Flow<List<Task>>
    suspend fun getCurrentTask(): Task?
    suspend fun addTask(name: String): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun setCurrentTask(taskId: Long)
    suspend fun toggleComplete(task: Task)
}
