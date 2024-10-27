package com.example.noteappkhaled.ui.fragment

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
import com.example.noteappkhaled.R
import com.example.noteappkhaled.databinding.FragmentNoteBinding
import com.example.noteappkhaled.model.Notes
import com.example.noteappkhaled.ui.MainActivity
import com.example.noteappkhaled.ui.NoteViewmodel
import java.text.SimpleDateFormat
import java.util.Locale


class NoteFragment : Fragment() {
    private lateinit var binding : FragmentNoteBinding
    lateinit var viewmodel: NoteViewmodel
    private var title : String? = null
    private var description : String? = null
    private var fav = 0
    private val dataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val updatedTime : Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_note,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set viewmodel
        viewmodel = (activity as MainActivity).viewmodel

        //set backButton on toolbar
        requireActivity().setActionBar(binding.addNewNoteTB)
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.addNewNoteTB.navigationIcon
            ?.setColorFilter(getResources().getColor(R.color.bWhite), PorterDuff.Mode.SRC_ATOP)
        binding.addNewNoteTB.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_noteFragment_to_notesListFragment3)
        }

        //set current time and date
        binding.timeText.text = timeFormat.format(updatedTime)
        binding.dateText.text = dataFormat.format(updatedTime)

        //click fav image
        binding.favImage.setOnClickListener {
            if(fav ==0){
                binding.favImage.setImageResource(R.drawable.baseline_favorite_24_red)
                fav = 1
            }
            else{
                binding.favImage.setImageResource(R.drawable.baseline_favorite_24)
                fav = 0
            }
        }

        //add new note
        binding.saveFAB.setOnClickListener {
            if (binding.descriptionEditText.text.toString() == "") {
                Toast.makeText(context,"Please insert Note description",Toast.LENGTH_SHORT).show()
            }
            else{
                title = binding.titleEditText.text.toString()
                description = binding.descriptionEditText.text.toString()
                val newNote = Notes(title!!,description!!)
                newNote.fav = fav
                viewmodel.insert(newNote)
                findNavController().navigate(R.id.action_noteFragment_to_notesListFragment3)
            }
        }
    }


}