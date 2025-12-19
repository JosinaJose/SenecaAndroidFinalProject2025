package com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb

import com.safemail.safemailapp.uiLayer.hubTask.todoTask.TodoViewModel

class TodoViewModelFactory(
    private val todoDao: TodoDao,
    private val adminEmail: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(todoDao, adminEmail) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}