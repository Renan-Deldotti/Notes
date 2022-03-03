package br.com.renandeldotti.notes.createnote

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.com.renandeldotti.notes.NoteViewModel
import br.com.renandeldotti.notes.R
import br.com.renandeldotti.notes.database.Note
import br.com.renandeldotti.notes.databinding.FragmentCreateNoteBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class CreateNoteFragment : Fragment() {

    companion object {
        const val CREATE_NEW_NOTE = 1
        const val UPDATE_NOTE = 2
        const val TAG = "CreateNoteFragment"
    }

    private lateinit var createNoteBinding: FragmentCreateNoteBinding
    private lateinit var viewModel: NoteViewModel
    private var noteToUpdateId: Long = -1
    private var isUpdate: Boolean = false
    private var lastUpdated: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        createNoteBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_note, container, false)

        setHasOptionsMenu(true)

        arguments?.let {
            val notePassed: Note? = CreateNoteFragmentArgs.fromBundle(it).noteToUpdate
            notePassed?.let {
                lastUpdated = notePassed.date
                createNoteBinding.createNoteTitle.setText(notePassed.title)
                createNoteBinding.createNoteDescription.setText(notePassed.description)
                noteToUpdateId = notePassed.id
                isUpdate = true
            }
        }

        if (isUpdate) {
            requireActivity().setTitle(R.string.update_note)
            createNoteBinding.fullDate =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(lastUpdated)
        } else {
            requireActivity().setTitle(R.string.create_note)
            createNoteBinding.fullDate =
                SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date().time)
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                checkForChanges(true)
            }
        })

        return createNoteBinding.root
    }

    private fun checkForChanges(fromBackButton: Boolean) {

        Log.d(TAG, "fromBackButton: $fromBackButton")

        var hasAnyChangedBeenDone = false

        if (!TextUtils.isEmpty(createNoteBinding.createNoteTitle.text.toString())
            || !TextUtils.isEmpty(createNoteBinding.createNoteDescription.text.toString())) {
            hasAnyChangedBeenDone = true
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setMessage("Changes has been done, are you sure you want to leave?")
            builder.setPositiveButton("Yes") { _, _ -> findNavController().navigateUp() }
            builder.setNegativeButton("No", null)
            val dialog = builder.create()
            dialog.show()
        } else if (fromBackButton) {
            findNavController().navigateUp()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_note_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: ${item.title} id:${item.itemId}")
        if (item.itemId == R.id.save_note) {
            hideKeyboard(createNoteBinding.createNoteDate)
            addNewNote()
            return false
        } else if (item.itemId == 16908332) {
            checkForChanges(true)
            return false
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    private fun addNewNote(): Boolean {
        if (TextUtils.isEmpty(createNoteBinding.createNoteTitle.text.toString())) {
            Snackbar.make(requireView(), getString(R.string.title_cnb_empty), Snackbar.LENGTH_SHORT)
                .show()
            return false
        }
        val newTitle = createNoteBinding.createNoteTitle.text.toString()
        val newDesc =
            if (!TextUtils.isEmpty(createNoteBinding.createNoteDescription.text.toString())) createNoteBinding.createNoteDescription.text.toString() else getString(
                R.string.no_description
            )

        val note = Note(newTitle, newDesc, Date().time)

        if (isUpdate) {
            note.id = noteToUpdateId
            viewModel.update(note)
        } else {
            viewModel.insert(note)
        }

        findNavController().navigate(R.id.notesFragment)
        return true
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}