package br.com.renandeldotti.notes.notes

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.renandeldotti.notes.MainActivity
import br.com.renandeldotti.notes.NoteViewModel
import br.com.renandeldotti.notes.R
import br.com.renandeldotti.notes.createnote.CreateNoteFragment
import br.com.renandeldotti.notes.database.Note
import br.com.renandeldotti.notes.databinding.FragmentNotesBinding
import com.google.android.material.snackbar.Snackbar


class NotesFragment : Fragment(), NotesAdapter.ItemListener {

    companion object{
        private const val TAG:String = "NotesFragment"
        private const val SET_LAYOUT_KEY:String = "SET_LAYOUT_KEY"
    }

    private lateinit var binding: FragmentNotesBinding
    private lateinit var viewModel:NoteViewModel
    private lateinit var adapter: NotesAdapter
    private lateinit var noteList: List<Note>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_notes,container,false)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = selectedLayoutManager()

        noteList = viewModel.allNotes.value?: ArrayList()

        adapter = NotesAdapter(noteList,this)
        binding.recyclerView.adapter = adapter

        viewModel.allNotes.observe(viewLifecycleOwner) {
            it?.let {
                noteList = it
                binding.recyclerView.adapter = NotesAdapter(noteList, this)
            }
        }

        binding.buttonAddNote.setOnClickListener{
            Navigation.findNavController(it).navigate(NotesFragmentDirections.actionNotesFragmentToCreateNoteFragment(CreateNoteFragment.CREATE_NEW_NOTE, null))
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView,viewHolder: ViewHolder, target: ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder,direction: Int) {
                deleteThisNote(viewHolder.adapterPosition)
            }
        }).attachToRecyclerView(binding.recyclerView)

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun confirmDeleteNote(adapterPosition: Int) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setMessage("Delete note?")
        alertBuilder.setNegativeButton("No", null)
        alertBuilder.setPositiveButton("Yes") { _, _ -> deleteThisNote(adapterPosition)}
        val alertDialog = alertBuilder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_all_notes -> deleteAllNotes()
            R.id.changeViewMode -> changeLayoutManager()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteThisNote(position: Int){
        viewModel.delete(noteList[position])
        Snackbar.make(requireView(),getString(R.string.noteDeleted),Snackbar.LENGTH_SHORT).show()
    }

    private fun deleteAllNotes(){
        viewModel.deleteAll()
        Toast.makeText(context,getString(R.string.allDeleted),Toast.LENGTH_SHORT).show()
    }

    private fun changeLayoutManager(){
        val sharedPreferencesEditor = context?.getSharedPreferences(MainActivity.notesAppPreferences, Context.MODE_PRIVATE)?.edit()
        if (binding.recyclerView.layoutManager.toString().contains("LinearLayoutManager")){
            binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            sharedPreferencesEditor?.putInt(SET_LAYOUT_KEY, 1)
        }else{
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            sharedPreferencesEditor?.putInt(SET_LAYOUT_KEY, 0)
        }
        sharedPreferencesEditor?.apply()
    }

    private fun selectedLayoutManager():RecyclerView.LayoutManager{
        val sharedPreferences = context?.getSharedPreferences(MainActivity.notesAppPreferences, Context.MODE_PRIVATE)
        var selectedLayoutManager:RecyclerView.LayoutManager = LinearLayoutManager(context)
        if (sharedPreferences!!.contains(SET_LAYOUT_KEY)){
            when(sharedPreferences.getInt(SET_LAYOUT_KEY,0)){
                1 -> selectedLayoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            }
        }
        return selectedLayoutManager
    }

    override fun selectedItem(position: Int) {
        Navigation.findNavController(binding.root).navigate(NotesFragmentDirections.actionNotesFragmentToCreateNoteFragment(CreateNoteFragment.UPDATE_NOTE, noteList[position]))
    }
}
