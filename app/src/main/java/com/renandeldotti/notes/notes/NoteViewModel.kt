package com.renandeldotti.notes.notes

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.renandeldotti.notes.database.Note
import com.renandeldotti.notes.database.NoteDatabase
import com.renandeldotti.notes.database.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "NoteViewModel"
    }

    private val database = NoteDatabase.getInstance(application)
    private val repository = NoteRepository(database)

    val allNotes: LiveData<List<Note>> = repository.allNotes

    fun deleteNoteById(noteId: Long) {
        viewModelScope.launch {
            val delRow = repository.deleteNoteWithId(noteId)
            Log.d(TAG, "deleteNoteById: $delRow")
        }
    }
    
    fun deleteAllNotes() {
        viewModelScope.launch { 
            val notesDeleted = repository.deleteAllNotes()
            Log.d(TAG, "deleteAllNotes: $notesDeleted")
        }
    }
}