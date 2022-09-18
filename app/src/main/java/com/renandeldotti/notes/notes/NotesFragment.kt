package com.renandeldotti.notes.notes

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.renandeldotti.notes.R
import com.renandeldotti.notes.databinding.FragmentNotesBinding
import com.renandeldotti.notes.utils.Constants
import com.renandeldotti.notes.utils.getAppSharedPreferences

class NotesFragment : Fragment() {

    private lateinit var binding: FragmentNotesBinding
    private val viewModel: NoteViewModel by viewModels()
    private lateinit var adapter: NotesAdapter
    //private lateinit var notesList: List<Int>
    private var currentLayoutInt: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)

        adapter = NotesAdapter(object : NotesAdapter.NoteSelectedListener {
            override fun selectedItem(noteId: Long) {
                findNavController().navigate(
                    NotesFragmentDirections.actionNotesFragmentToCreateOrUpdateNoteFragment(
                        Constants.UPDATE_NOTE,
                        noteId
                    )
                )
            }
        })

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = getCurrentLayoutManager()
        binding.recyclerView.adapter = adapter

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val itemId = (viewHolder as NotesAdapter.NoteHolder).noteId
                Log.d("TAG", "onSwiped: $itemId")
                viewModel.deleteNoteById(itemId)
                Snackbar.make(requireView(),getString(R.string.noteDeleted), Snackbar.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(binding.recyclerView)

        viewModel.allNotes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.buttonAddNote.setOnClickListener {
            findNavController().navigate(
                NotesFragmentDirections.actionNotesFragmentToCreateOrUpdateNoteFragment(Constants.CREATE_NEW_NOTE)
            )
        }

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.delete_all_notes -> {
                        showDeleteAllNotesConfirmationDialog()
                        true
                    }
                    R.id.changeViewMode -> {
                        updateLayoutManager()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    private fun showDeleteAllNotesConfirmationDialog() {
        AlertDialog.Builder(context).apply {
            setMessage(getString(R.string.alert_dialog_danc_messages))
            setNegativeButton(getString(R.string.No), null)
            setPositiveButton(getString(R.string.Yes)) { _, _ ->
                viewModel.deleteAllNotes()
                Toast.makeText(context,getString(R.string.allDeleted), Toast.LENGTH_SHORT).show()
            }
        }.create().show()
    }

    private fun getCurrentLayoutManager(): RecyclerView.LayoutManager {
        val sp = requireContext().getAppSharedPreferences()
        if (!sp.contains(Constants.CURRENT_LAYOUT_KEY)) {
            sp.edit().apply {
                putInt(Constants.CURRENT_LAYOUT_KEY, 0)
            }.apply()
            return LinearLayoutManager(context)
        }
        currentLayoutInt = sp.getInt(Constants.CURRENT_LAYOUT_KEY, 0)
        val currentLayout = if (currentLayoutInt == 0) {
            LinearLayoutManager(context)
        } else {
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        return currentLayout
    }

    private fun updateLayoutManager() {
        val sharedPreferencesEditor = requireContext().getAppSharedPreferences().edit()
        val newLayoutManager: RecyclerView.LayoutManager
        if (currentLayoutInt == 0) {
            newLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            currentLayoutInt = 1
            sharedPreferencesEditor.putInt(Constants.CURRENT_LAYOUT_KEY, 1)
        } else {
            newLayoutManager = LinearLayoutManager(context)
            currentLayoutInt = 0
            sharedPreferencesEditor.putInt(Constants.CURRENT_LAYOUT_KEY, 0)
        }
        binding.recyclerView.layoutManager = newLayoutManager
        sharedPreferencesEditor.apply()
    }
}