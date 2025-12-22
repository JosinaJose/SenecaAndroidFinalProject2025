package com.safemail.safemailapp.hubTaskBackend.stickyNoteLocalDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sticky_notes") // <--- MUST HAVE THIS
data class StickyNoteModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val text: String,
    val color: Long,
    val adminEmail: String,
    val timestamp: Long = System.currentTimeMillis()
)