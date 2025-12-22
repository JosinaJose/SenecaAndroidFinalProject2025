package com.safemail.safemailapp.uiLayer.hubTask.stickyNotes




import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safemail.safemailapp.hubTaskBackend.stickyNoteLocalDb.StickyNoteDao
import com.safemail.safemailapp.hubTaskBackend.stickyNoteLocalDb.StickyNoteModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StickyNotesViewModel(
    private val dao: StickyNoteDao,
    private val adminEmail: String
) : ViewModel() {

    // Observe the database. This replaces the manual _notes list.
    val notes: StateFlow<List<StickyNoteModel>> = dao.getNotesForAdmin(adminEmail)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNote(text: String, color: Long) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val newNote = StickyNoteModel(
                text = text,
                color = color,
                adminEmail = adminEmail
            )
            dao.insertNote(newNote)
        }
    }

    fun updateNote(id: Int, newText: String) {
        viewModelScope.launch {
            // Find the current note in our list and update it
            notes.value.find { it.id == id }?.let { existingNote ->
                dao.updateNote(existingNote.copy(text = newText))
            }
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            dao.deleteNoteById(id)
        }
    }
}