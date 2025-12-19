package com.safemail.safemailapp.uiLayer.hubTask.stickyNotes


import androidx.compose.runtime.*

import androidx.lifecycle.ViewModel
import com.safemail.safemailapp.hubTaskBackend.stickyNoteLocalDb.StickyNoteModel


class StickyNotesViewModel : ViewModel() {
    private val _notes = mutableStateOf<List<StickyNoteModel>>(emptyList())
    val notes: State<List<StickyNoteModel>> = _notes

    fun addNote(text: String, color: Long) {
        if (text.isBlank()) return

        // Fix: Use the provided color and ensure unique ID
        val newId = (_notes.value.maxOfOrNull { it.id } ?: 0) + 1
        val newNote = StickyNoteModel(
            id = newId,
            text = text,
            color = color // Color is now applied correctly
        )
        _notes.value = _notes.value + newNote
    }

    fun updateNote(id: Int, newText: String) {
        _notes.value = _notes.value.map {
            if (it.id == id) it.copy(text = newText) else it
        }
    }

    fun deleteNote(id: Int) {
        _notes.value = _notes.value.filterNot { it.id == id }
    }
}

