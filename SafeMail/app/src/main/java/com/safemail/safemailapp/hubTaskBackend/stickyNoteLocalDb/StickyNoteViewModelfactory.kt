package com.safemail.safemailapp.hubTaskBackend.stickyNoteLocalDb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.safemail.safemailapp.uiLayer.hubTask.stickyNotes.StickyNotesViewModel


class StickyNoteViewModelFactory(
    private val dao: StickyNoteDao,
    private val adminEmail: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StickyNotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StickyNotesViewModel(dao, adminEmail) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}