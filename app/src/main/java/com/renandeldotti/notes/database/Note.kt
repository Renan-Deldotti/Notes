package com.renandeldotti.notes.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "note_table")
@Parcelize
data class Note(
    val title: String?,
    val description: String?,
    val date: Long
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}