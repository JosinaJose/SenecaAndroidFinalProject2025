package com.safemail.safemailapp.dataModels



enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    BLOCKED,
    COMPLETED
}
data class TodoTask(
    val id: Int,
    val title: String,
    val status: TaskStatus = TaskStatus.TODO
)