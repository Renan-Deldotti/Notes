package br.com.renandeldotti.notes

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.com.renandeldotti.notes.database.Note
import br.com.renandeldotti.notes.database.NoteDao
import br.com.renandeldotti.notes.database.NoteDatabase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class NoteDatabaseTest {
    private lateinit var noteDao: NoteDao
    private lateinit var database: NoteDatabase

    @Before
    fun createDb(){
        // Use this database in temporary memory
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context,NoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        noteDao = database.noteDao
    }

    @Test
    @Throws(Exception::class)
    fun insertGetNote(){
        val note = Note("","",1)
        noteDao.insert(note)
        val whichNote = noteDao.getAllNotes()
        note == whichNote.value?.get(0)
    }

    @After
    @Throws(IOException::class)
    fun closedDatabase(){
        database.close()
    }
}