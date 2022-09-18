package com.renandeldotti.notes.createupdatenote

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.renandeldotti.notes.database.Note
import com.renandeldotti.notes.database.NoteDatabase
import com.renandeldotti.notes.database.NoteRepository
import kotlinx.coroutines.launch

class CreateOrUpdateViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "CreateOrUpdateViewModel"
    }

    private val database = NoteDatabase.getInstance(application)
    private val repository = NoteRepository(database)
    private val _currentNoteData: MutableLiveData<Note> = MutableLiveData()
    val currentNoteData: LiveData<Note> get() = _currentNoteData

    val hasAnyChangedBeenDone: MutableLiveData<Boolean> = MutableLiveData(false)

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun deleteNoteWithId(noteId: Long) {
        viewModelScope.launch {
            repository.deleteNoteWithId(noteId)
        }
    }

    fun getNoteById(noteId: Long) {
        viewModelScope.launch {
            _currentNoteData.value = repository.getNoteWithId(noteId)
        }
    }
}