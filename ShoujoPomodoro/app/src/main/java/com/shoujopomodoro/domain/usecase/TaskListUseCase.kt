package com.shoujopomodoro.domain.usecase

import com.shoujopomodoro.data.repository.TaskRepository
import com.shoujopomodoro.domain.model.Task
import kotlinx.coroutines.flow.Flow

class TaskListUseCase(
    private val taskRepository: TaskRepository
) {
    fun observeTasks(): Flow<List<Task>> = taskRepository.observeAll()

    suspend fun getCurrentTask(): Task? = taskRepository.getCurrentTask()

    suspend fun addTask(name: String): Long = taskRepository.addTask(name)

    suspend fun deleteTask(task: Task) = taskRepository.deleteTask(task)

    suspend fun setCurrentTask(taskId: Long) = taskRepository.setCurrentTask(taskId)

    suspend fun toggleComplete(task: Task) = taskRepository.toggleComplete(task)
}
