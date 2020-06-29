package br.com.renandeldotti.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.renandeldotti.notes.database.Note
import br.com.renandeldotti.notes.database.NoteRepository

class NoteViewModel(application: Application) : AndroidViewModel(application){

    private val repository:NoteRepository = NoteRepository(application)
    var allNotes:LiveData<List<Note>> = repository.allNotes

    fun insert(note: Note){
        repository.insert(note)
    }

    fun update(note: Note){
        repository.update(note)
    }

    fun delete(note: Note){
        repository.delete(note)
    }

    fun deleteAll(){
        repository.deleteAll()
    }

    override fun onCleared() {
        super.onCleared()
        repository.cancelAllJobs()
    }
}