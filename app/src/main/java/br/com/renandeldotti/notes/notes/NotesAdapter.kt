package br.com.renandeldotti.notes.notes

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.renandeldotti.notes.R
import br.com.renandeldotti.notes.database.Note
import kotlinx.android.synthetic.main.single_note_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter(private val noteList: List<Note>, private val itemListener: ItemListener) :
    RecyclerView.Adapter<NotesAdapter.NoteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.single_note_item, parent, false)
        return NoteHolder(view, itemListener)
    }

    override fun getItemCount() = noteList.size

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.titleTextView.text = noteList[position].title
        holder.descriptionTextView.text = noteList[position].description
        holder.dayTextView.text =
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(noteList[position].date)
        holder.hourTextView.text =
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(noteList[position].date)
    }

    class NoteHolder(itemView: View, itemListener: ItemListener) :
        RecyclerView.ViewHolder(itemView) {
        var titleTextView: TextView = itemView.text_view_title
        var descriptionTextView: TextView = itemView.text_view_description
        var dayTextView: TextView = itemView.text_view_day
        var hourTextView: TextView = itemView.text_view_hour

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    itemListener.selectedItem(adapterPosition)
                }
            }
        }
    }

    interface ItemListener {
        fun selectedItem(position: Int)
    }
}