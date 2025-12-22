package com.safemail.safemailapp.hubTaskBackend.stickyNoteLocalDb

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StickyNoteDao {
    @Query("SELECT * FROM sticky_notes WHERE adminEmail = :email ORDER BY timestamp DESC")
    fun getNotesForAdmin(email: String): Flow<List<StickyNoteModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StickyNoteModel)

    @Update
    suspend fun updateNote(note: StickyNoteModel)

    @Query("DELETE FROM sticky_notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Int)
}