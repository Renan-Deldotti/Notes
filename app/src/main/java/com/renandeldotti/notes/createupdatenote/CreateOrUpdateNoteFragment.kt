package com.renandeldotti.notes.createupdatenote

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.renandeldotti.notes.R
import com.renandeldotti.notes.database.Note
import com.renandeldotti.notes.databinding.FragmentCreateOrUpdateNoteBinding
import com.renandeldotti.notes.utils.Constants
import com.renandeldotti.notes.utils.getInputMethodManager
import java.text.SimpleDateFormat
import java.util.*

class CreateOrUpdateNoteFragment : Fragment() {
    companion object {
        private const val TAG = "CoUNoteFragment"
    }

    private lateinit var binding: FragmentCreateOrUpdateNoteBinding
    private val viewModel: CreateOrUpdateViewModel by viewModels()
    private val couArgs: CreateOrUpdateNoteFragmentArgs by navArgs()
    private var isUpdate: Boolean = false
    private var noteId: Long = -1

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            s?.let {
                if (!TextUtils.isEmpty(it.toString())){
                    Log.d(TAG, "afterTextChanged $it")
                    viewModel.hasAnyChangedBeenDone.value = true
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateOrUpdateNoteBinding.inflate(inflater, container, false)

        isUpdate = couArgs.actionCreateOrUpdate == Constants.UPDATE_NOTE
        Log.d(TAG, "onCreateView: $isUpdate ${couArgs.noteToUpdate}")

        if (isUpdate) {
            noteId = couArgs.noteToUpdate
            viewModel.getNoteById(noteId)
            viewModel.currentNoteData.observe(viewLifecycleOwner) {
                binding.createNoteTitle.setText(it.title)
                binding.createNoteDescription.setText(it.description)
                binding.createNoteDate.text = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(it.date)

                Log.d(TAG, "onCreateView: adding text watchers")
                binding.createNoteTitle.addTextChangedListener(textWatcher)
                binding.createNoteDescription.addTextChangedListener(textWatcher)
                viewModel.hasAnyChangedBeenDone.observe(viewLifecycleOwner) { textChanged ->
                    if(textChanged) {
                        Log.d(TAG, "onCreateView: removing text watchers")
                        binding.createNoteTitle.removeTextChangedListener(textWatcher)
                        binding.createNoteDescription.removeTextChangedListener(textWatcher)
                    }
                }
            }
            requireActivity().setTitle(R.string.update_note)
        } else {
            binding.createNoteDate.text = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date().time)
            requireActivity().setTitle(R.string.create_note)
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    if (shouldDisplayConfirmationDialog()) {
                        displayExitConfirmationDialog()
                    } else {
                        findNavController().navigateUp()
                    }
                }
            })

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (isUpdate) {
                    menuInflater.inflate(R.menu.update_note_menu, menu)
                } else {
                    menuInflater.inflate(R.menu.add_note_menu, menu)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save_note -> {
                        hideKeyboard(binding.createNoteDate)
                        if (shouldDisplayConfirmationDialog()) {
                            if (addOrUpdateNote()) {
                                findNavController().navigateUp()
                            }
                        }
                        true
                    }
                    R.id.delete_note -> {
                        confirmDeleteNote()
                        true
                    }
                    android.R.id.home -> {
                        Log.d(TAG, "onMenuItemSelected - home clicked: ${shouldDisplayConfirmationDialog()}")
                        if (shouldDisplayConfirmationDialog()) {
                            displayExitConfirmationDialog()
                            true
                        } else {
                            findNavController().navigateUp()
                        }
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    private fun shouldDisplayConfirmationDialog() : Boolean {
        if (isUpdate) {
            return viewModel.hasAnyChangedBeenDone.value == true
        } else {
            if (!TextUtils.isEmpty(binding.createNoteTitle.text.toString())
                ||!TextUtils.isEmpty(binding.createNoteDescription.text.toString())) {
                return true
            }
        }
        return false
    }

    private fun displayExitConfirmationDialog() {
        AlertDialog.Builder(context).apply {
            setMessage(getString(R.string.changes_done_dialog_confirmation))
            setPositiveButton(getString(R.string.Yes)) { _, _ -> findNavController().navigateUp() }
            setNegativeButton(getString(R.string.No), null)
        }.create().show()
    }

    private fun confirmDeleteNote() {
        AlertDialog.Builder(context).apply {
            setMessage(getString(R.string.delete_this_note))
            setPositiveButton(getString(R.string.Yes)) { _, _ ->
                viewModel.deleteNoteWithId(noteId)
                findNavController().navigateUp()
            }
            setNegativeButton(getString(R.string.No), null)
        }.create().show()
    }

    private fun addOrUpdateNote(): Boolean {
        if (TextUtils.isEmpty(binding.createNoteTitle.text.toString())) {
            Snackbar.make(requireView(), getString(R.string.title_cnb_empty), Snackbar.LENGTH_SHORT)
                .show()
            return false
        }
        val newTitle = binding.createNoteTitle.text.toString()
        val newDesc = if (!TextUtils.isEmpty(binding.createNoteDescription.text.toString())) {
            binding.createNoteDescription.text.toString()
        } else {
            getString(R.string.no_description)
        }

        val note = Note(newTitle, newDesc, Date().time)

        if (isUpdate) {
            note.id = noteId
            viewModel.updateNote(note)
        } else {
            viewModel.insertNote(note)
        }

        return true
    }

    private fun hideKeyboard(view: View) {
        requireContext().getInputMethodManager().hideSoftInputFromWindow(
            view.windowToken,
            0
        )
    }
}