package com.shoujopomodoro.data.repository

import com.shoujopomodoro.data.local.dao.TaskDao
import com.shoujopomodoro.data.local.entity.TaskEntity
import com.shoujopomodoro.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun observeAll(): Flow<List<Task>> {
        return taskDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCurrentTask(): Task? {
        return taskDao.getCurrentTask()?.toDomain()
    }

    override suspend fun addTask(name: String): Long {
        val entity = TaskEntity(name = name)
        return taskDao.insert(entity)
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toEntity())
    }

    override suspend fun setCurrentTask(taskId: Long) {
        taskDao.clearCurrentTask()
        taskDao.setCurrentTask(taskId)
    }

    override suspend fun toggleComplete(task: Task) {
        val updated = task.copy(isCompleted = !task.isCompleted)
        taskDao.update(updated.toEntity())
    }

    private fun TaskEntity.toDomain(): Task = Task(
        id = id,
        name = name,
        isCompleted = isCompleted,
        isCurrentTask = isCurrentTask,
        createdAt = createdAt
    )

    private fun Task.toEntity(): TaskEntity = TaskEntity(
        id = id,
        name = name,
        isCompleted = isCompleted,
        isCurrentTask = isCurrentTask,
        createdAt = createdAt
    )
}
