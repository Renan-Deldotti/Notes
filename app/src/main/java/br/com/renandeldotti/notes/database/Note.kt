package br.com.renandeldotti.notes.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(val title: String?, val description: String?, val date: Long) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    ) {
        id = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeLong(date)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}