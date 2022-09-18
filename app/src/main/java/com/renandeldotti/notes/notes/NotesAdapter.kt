package com.renandeldotti.notes.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.renandeldotti.notes.database.Note
import com.renandeldotti.notes.databinding.SingleNoteItemBinding
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter(private val listener: NoteSelectedListener): ListAdapter<Note, NotesAdapter.NoteHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SingleNoteItemBinding.inflate(inflater, parent, false)
        return NoteHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val note: Note = getItem(position)
        holder.itemView.setOnClickListener {
            listener.selectedItem(note.id)
        }
        holder.bind(note)
    }

    inner class NoteHolder(private val itemBinding: SingleNoteItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var noteId: Long = -1
        fun bind(note: Note) {
            noteId = note.id
            itemBinding.textViewTitle.text = note.title
            itemBinding.textViewDescription.text = note.description
            itemBinding.textViewDay.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(note.date)
            itemBinding.textViewHour.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(note.date)
        }
    }

    companion object DiffCallBack: DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    interface NoteSelectedListener {
        fun selectedItem(noteId: Long)
    }
}