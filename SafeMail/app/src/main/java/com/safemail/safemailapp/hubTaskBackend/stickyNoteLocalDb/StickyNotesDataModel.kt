package com.safemail.safemailapp.hubTaskBackend.stickyNoteLocalDb

data class StickyNoteModel(
    val id: Int,
    val text: String,
    val color: Long = 0xFFFFF59D, // Default yellow
    val timestamp: Long = System.currentTimeMillis()
)