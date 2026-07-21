package com.shoujopomodoro.ui.screen.tasklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shoujopomodoro.ShoujoPomodoroApp
import com.shoujopomodoro.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val showAddDialog: Boolean = false
)

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as ShoujoPomodoroApp).container
    private val taskListUseCase = container.taskListUseCase

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            taskListUseCase.observeTasks().collect { tasks ->
                _uiState.value = _uiState.value.copy(tasks = tasks)
            }
        }
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }

    fun dismissAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }

    fun addTask(name: String) {
        viewModelScope.launch {
            taskListUseCase.addTask(name)
            _uiState.value = _uiState.value.copy(showAddDialog = false)
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            taskListUseCase.toggleComplete(task)
        }
    }

    fun setCurrentTask(taskId: Long) {
        viewModelScope.launch {
            taskListUseCase.setCurrentTask(taskId)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskListUseCase.deleteTask(task)
        }
    }
}
