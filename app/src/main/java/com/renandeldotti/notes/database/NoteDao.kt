package com.renandeldotti.notes.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {
    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM note_table WHERE id=:noteId")
    suspend fun deleteNoteWithId(noteId: Long): Int

    @Query("DELETE FROM note_table")
    suspend fun deleteAllNotes() : Int

    @Query("SELECT * FROM note_table ORDER BY date DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE id IN (:noteIds)")
    fun getNoteByIds(vararg noteIds: Int): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE id=:noteId")
    suspend fun getNoteWithId(noteId: Long) : Note?
}