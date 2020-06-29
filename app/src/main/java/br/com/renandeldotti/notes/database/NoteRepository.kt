package br.com.renandeldotti.notes.database

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*

class NoteRepository(application:Application)  {
    private var noteDao:NoteDao
    var allNotes:LiveData<List<Note>>

    private var repositoryJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + repositoryJob)

    init{
        val database = NoteDatabase.getInstance(application)
        noteDao = database.noteDao
        allNotes = noteDao.getAllNotes()
    }

    fun insert(note:Note){
        uiScope.launch {
            insertNewNote(note)
        }
    }

    fun update(note:Note){
        uiScope.launch {
            updateSomeNote(note)
        }
    }

    fun delete(note:Note){
        uiScope.launch {
            deleteOldNote(note)
        }
    }

    fun deleteAll(){
        uiScope.launch {
            withContext(Dispatchers.IO){
                noteDao.deleteAllNotes()
            }
        }
    }

    fun cancelAllJobs(){
        repositoryJob.cancel()
    }

    private suspend fun insertNewNote(note:Note){
        withContext(Dispatchers.IO){
            noteDao.insert(note)
        }
    }

    private suspend fun updateSomeNote(note: Note){
        withContext(Dispatchers.IO){
            noteDao.update(note)
        }
    }

    private suspend fun deleteOldNote(note: Note){
        withContext(Dispatchers.IO){
            noteDao.delete(note)
        }
    }
}