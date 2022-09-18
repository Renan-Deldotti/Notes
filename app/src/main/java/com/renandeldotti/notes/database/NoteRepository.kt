package com.renandeldotti.notes.database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(private val database: NoteDatabase) {

    val allNotes: LiveData<List<Note>> = database.noteDao.getAllNotes()

    fun getNotesById(vararg ids:Int): LiveData<List<Note>> = database.noteDao.getNoteByIds(*ids)

    suspend fun getNoteWithId(noteId: Long) : Note? {
        val note: Note?
        withContext(Dispatchers.IO) {
            note = database.noteDao.getNoteWithId(noteId)
        }
        return note
    }

    suspend fun insertNote(note: Note) {
        withContext(Dispatchers.IO) {
            database.noteDao.insertNote(note)
        }
    }

    suspend fun updateNote(note: Note) {
        withContext(Dispatchers.IO) {
            database.noteDao.updateNote(note)
        }
    }

    suspend fun deleteNote(note: Note) {
        withContext(Dispatchers.IO) {
            database.noteDao.deleteNote(note)
        }
    }

    suspend fun deleteAllNotes(): Int {
        return withContext(Dispatchers.IO) {
            database.noteDao.deleteAllNotes()
        }
    }

    suspend fun deleteNoteWithId(noteId: Long): Int {
        return withContext(Dispatchers.IO) {
            database.noteDao.deleteNoteWithId(noteId)
        }
    }
}