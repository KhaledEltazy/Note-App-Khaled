package com.example.noteappkhaled.ui.fragment

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteappkhaled.R
import com.example.noteappkhaled.databinding.FragmentUpdateNoteBinding
import com.example.noteappkhaled.model.Notes
import com.example.noteappkhaled.ui.MainActivity
import com.example.noteappkhaled.ui.NoteViewmodel
import java.text.SimpleDateFormat
import java.util.Locale


class UpdateNoteFragment : Fragment() {
   private lateinit var binding : FragmentUpdateNoteBinding
    private lateinit var viewmodel: NoteViewmodel
    private var title : String? = null
    private var description : String? = null
    private val dataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val updatedTime : Long = System.currentTimeMillis()

    private val args : UpdateNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_update_note,container,false)
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set viewmodel
        viewmodel = (activity as MainActivity).viewmodel

        //set backbutton in toolbar
        requireActivity().setActionBar(binding.updateNoteTB)
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.updateNoteTB.navigationIcon
            ?.setColorFilter(getResources().getColor(R.color.bWhite), PorterDuff.Mode.SRC_ATOP)
        binding.updateNoteTB.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_updateNoteFragment_to_notesListFragment3)
        }

        //get old data
        title = args.note.title
        description = args.note.description

        //set updated data
        binding.titleEditText.setText(title)
        binding.descriptionEditText.setText(description)
        binding.timeText.text = timeFormat.format(updatedTime)
        binding.dateText.text = dataFormat.format(updatedTime)

        //check if it a fav or not
            if(args.note.fav ==0){
                binding.favImage.setImageResource(R.drawable.baseline_favorite_24)
            }
            else{
                binding.favImage.setImageResource(R.drawable.baseline_favorite_24_red)
            }

        //click fav image
        binding.favImage.setOnClickListener {
            if(args.note.fav ==0){
                binding.favImage.setImageResource(R.drawable.baseline_favorite_24_red)
                args.note.fav = 1
            }
            else{
                binding.favImage.setImageResource(R.drawable.baseline_favorite_24)
                args.note.fav = 0
            }
        }

        //update notes
        binding.updateFAB.setOnClickListener {
            if (binding.descriptionEditText.text.toString() == "") {
                Toast.makeText(context,"Please insert Note description", Toast.LENGTH_SHORT).show()
            }
            else{
                title = binding.titleEditText.text.toString()
                description = binding.descriptionEditText.text.toString()
                val newNote = Notes(title!!, description!!)
                newNote.fav = args.note.fav
                newNote.id = args.note.id
                newNote.time = updatedTime
                viewmodel.update(newNote)
                findNavController().navigate(R.id.action_updateNoteFragment_to_notesListFragment3)
            }
        }

    }
}