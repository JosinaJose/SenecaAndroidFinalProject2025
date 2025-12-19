package com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface TodoDao {
    // Get only the tasks for the specific admin currently logged in
    @Query("SELECT * FROM todo_tasks WHERE adminEmail = :email ORDER BY createdAt DESC")
    fun getTasksForAdmin(email: String): Flow<List<TodoTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TodoTask)

    @Update
    suspend fun updateTask(task: TodoTask)

    @Query("DELETE FROM todo_tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)
}