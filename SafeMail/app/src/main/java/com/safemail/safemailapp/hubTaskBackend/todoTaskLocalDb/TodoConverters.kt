package com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb

import androidx.room.TypeConverter

class TodoConverters {
    @TypeConverter
    fun fromStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): TaskStatus {
        return TaskStatus.valueOf(value)
    }
}