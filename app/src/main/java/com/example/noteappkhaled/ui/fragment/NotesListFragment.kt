package com.example.noteappkhaled.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noteappkhaled.R
import com.example.noteappkhaled.adapters.LargeGridAdapter
import com.example.noteappkhaled.adapters.MediumGridAdapter
import com.example.noteappkhaled.adapters.NotesAdapter
import com.example.noteappkhaled.adapters.SimpleListAdapter
import com.example.noteappkhaled.adapters.SmallGridAdapter
import com.example.noteappkhaled.databinding.FragmentNotesListBinding
import com.example.noteappkhaled.model.Notes
import com.example.noteappkhaled.ui.MainActivity
import com.example.noteappkhaled.ui.NoteViewmodel
import com.example.noteappkhaled.util.Constant.Companion.AtoZ
import com.example.noteappkhaled.util.Constant.Companion.ZtoA
import com.example.noteappkhaled.util.Constant.Companion.ascendingByTime
import com.example.noteappkhaled.util.Constant.Companion.descendingByTime
import com.example.noteappkhaled.util.Constant.Companion.largeGridView
import com.example.noteappkhaled.util.Constant.Companion.listView
import com.example.noteappkhaled.util.Constant.Companion.mediumGirdView
import com.example.noteappkhaled.util.Constant.Companion.simpleListView
import com.example.noteappkhaled.util.Constant.Companion.smallGridView
import com.google.android.material.snackbar.Snackbar
import java.util.Locale


class NotesListFragment : Fragment() {
    private lateinit var binding: FragmentNotesListBinding
    private val noteAdapter = NotesAdapter()
    private val simpleListAdapter = SimpleListAdapter()
    private val smallGirdAdapter = SmallGridAdapter()
    private val mediumGirdAdapter = MediumGridAdapter()
    private val largeGirdAdapter = LargeGridAdapter()
    private lateinit var viewmodel: NoteViewmodel
    private val TAG = "NotesList"
    private var savedView: String? = null
    private var savedSort : String? = null
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var listPosition: Notes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set viewmodel
        viewmodel = (activity as MainActivity).viewmodel

        //retrieveData
        retrieveData()

        //setupRecyclerView
        viewmodel.adapterView.observe(viewLifecycleOwner, Observer { newView ->
            viewmodel.sort.observe(viewLifecycleOwner, Observer { newSort->
                setupRecyclerView(newView,newSort)
                //change value of savedView variable
                savedView = newView
                savedSort = newSort
                savedData()
            })
        })

        //change three-dot color of optionMenu
        binding.mainTB.inflateMenu(R.menu.option_menu)
        binding.mainTB.getOverflowIcon()
            ?.setColorFilter(getResources().getColor(R.color.bWhite), PorterDuff.Mode.SRC_ATOP)
        binding.mainTB.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list -> {
                    viewmodel.setAdapterView(listView)
                }

                R.id.simple_list -> {
                    viewmodel.setAdapterView(simpleListView)
                }

                R.id.small_grid -> {
                    viewmodel.setAdapterView(smallGridView)
                }

                R.id.medium_grid -> {
                    viewmodel.setAdapterView(mediumGirdView)
                }

                R.id.large_grid -> {
                    viewmodel.setAdapterView(largeGridView)
                }

                R.id.from_z_to_a ->{
                    viewmodel.setSorting(ZtoA)
                }

                R.id.from_a_to_z ->{
                    viewmodel.setSorting(AtoZ)
                }

                R.id.ascending ->{
                    viewmodel.setSorting(ascendingByTime)
                }

                R.id.descending ->{
                    viewmodel.setSorting(descendingByTime)
                }

            }
            true
        }

        //add new note
        binding.addFAB.setOnClickListener {
            findNavController().navigate(R.id.action_notesListFragment_to_noteFragment)
        }

        //delete one item
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.DOWN or ItemTouchHelper.UP,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(viewmodel.adapterView.value){
                    listView ->{
                        listPosition =  noteAdapter.noteList[viewHolder.adapterPosition]
                    }
                    simpleListView-> {
                        listPosition = simpleListAdapter.noteList[viewHolder.adapterPosition]
                    }
                    smallGridView-> {
                        listPosition =  smallGirdAdapter.noteList[viewHolder.adapterPosition]
                    }
                    mediumGirdView-> {
                        listPosition =  mediumGirdAdapter.noteList[viewHolder.adapterPosition]
                    }
                    largeGridView-> {
                        listPosition =   largeGirdAdapter.noteList[viewHolder.adapterPosition]
                    }
                }
                viewmodel.delete(listPosition)
                Snackbar.make(view, "Undo deleted Note", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewmodel.insert(listPosition)
                    }
                }.show()
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvNotesList)

        //delete all notes
        binding.deleteAllFAB.setOnClickListener {
            deleteIcon()
        }

    }


    //selectView
    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView(view: String,sort:String) {
        when (view) {
            listView -> {
                //set RecyclerView
                binding.rvNotesList.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = noteAdapter
                }

                //getAllNotes
                viewmodel.getAllNotes().observe(viewLifecycleOwner, Observer {notes->
                    //set view ant sorted view
                    val newNotes = sortedView(notes,sort)
                    noteAdapter.setNote(newNotes)
                })

                //click handle
                noteAdapter.onItemClickListener = { currentNote ->
                    val bundle = Bundle().apply {
                        putSerializable("note", currentNote)
                    }
                    findNavController().navigate(
                        R.id.action_notesListFragment_to_updateNoteFragment,
                        bundle
                    )
                }
                noteAdapter.onFavClickListener = { currentList->
                    if(currentList.fav ==0){
                        currentList.fav =1
                    }
                    else{
                        currentList.fav = 0
                    }
                    viewmodel.update(currentList)
                }

                //search
                binding.search.clearFocus()
                viewmodel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->
                    val filteredList = ArrayList<Notes>()

                    binding.search.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if(newText != null){

                                for(i in notes){
                                    if(i.title.toLowerCase(Locale.ROOT).contains(newText)){
                                        filteredList.add(i)
                                    }
                                }

                                if(filteredList.isEmpty()){
                                    Toast.makeText(activity,"No Data found",Toast.LENGTH_SHORT).show()
                                } else{
                                    noteAdapter.setFilteredList(filteredList)
                                }
                            }
                            return true
                        }
                    })
                })
            }

            simpleListView -> {
                //set RecyclerView
                binding.rvNotesList.apply {
                    layoutManager = LinearLayoutManager(activity)
                    adapter = simpleListAdapter
                }

                //getAllNotes
                viewmodel.getAllNotes().observe(viewLifecycleOwner, Observer {notes->
                    //set view ant sorted view
                    val newNotes = sortedView(notes,sort)
                    simpleListAdapter.setNote(newNotes)
                })

                //click handle
                simpleListAdapter.onItemClickListener = { currentNote ->
                    val bundle = Bundle().apply {
                        putSerializable("note", currentNote)
                    }
                    findNavController().navigate(
                        R.id.action_notesListFragment_to_updateNoteFragment,
                        bundle
                    )
                }

                simpleListAdapter.onFavClickListener = { currentList->
                    if(currentList.fav ==0){
                        currentList.fav =1
                    }
                    else{
                        currentList.fav = 0
                    }
                    viewmodel.update(currentList)
                }

                //search
                binding.search.clearFocus()
                viewmodel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->
                    binding.search.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if(newText != null){
                                val filteredList = ArrayList<Notes>()
                                for(i in notes){
                                    if(i.title.toLowerCase(Locale.ROOT).contains(newText)){
                                        filteredList.add(i)
                                    }
                                }

                                if(filteredList.isEmpty()){
                                    Toast.makeText(activity,"No Data found",Toast.LENGTH_SHORT).show()
                                } else{
                                    simpleListAdapter.setFilteredList(filteredList)
                                }
                            }
                            return true
                        }
                    })
                })
            }

            smallGridView -> {
                //set RecyclerView
                binding.rvNotesList.apply {
                    val gridManager: RecyclerView.LayoutManager =
                        GridLayoutManager(requireActivity(), 4)
                    layoutManager = gridManager
                    adapter = smallGirdAdapter
                }

                //getAllNotes
                viewmodel.getAllNotes().observe(viewLifecycleOwner, Observer {notes->
                    //set view ant sorted view
                    val newNotes = sortedView(notes,sort)
                    smallGirdAdapter.setNote(newNotes)
                })

                //click handle
                smallGirdAdapter.onItemClickListener = { currentNote ->
                    val bundle = Bundle().apply {
                        putSerializable("note", currentNote)
                    }
                    findNavController().navigate(
                        R.id.action_notesListFragment_to_updateNoteFragment,
                        bundle
                    )

                }

                smallGirdAdapter.onFavClickListener = { currentList->
                    if(currentList.fav ==0){
                        currentList.fav =1
                    }
                    else{
                        currentList.fav = 0
                    }
                    viewmodel.update(currentList)
                }

                //search
                binding.search.clearFocus()
                viewmodel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->

                    binding.search.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if(newText != null){
                                val filteredList = ArrayList<Notes>()
                                for(i in notes){
                                    if(i.title.toLowerCase(Locale.ROOT).contains(newText)){
                                        filteredList.add(i)
                                    }
                                }

                                if(filteredList.isEmpty()){
                                    Toast.makeText(activity,"No Data found",Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    smallGirdAdapter.setFilteredList(filteredList)
                                }
                            }
                            return true
                        }
                    })
                })
            }

            mediumGirdView -> {
                //set RecyclerView
                binding.rvNotesList.apply {
                    val gridManager: RecyclerView.LayoutManager =
                        GridLayoutManager(requireActivity(), 3)
                    layoutManager = gridManager
                    adapter = mediumGirdAdapter
                }

                //getAllNotes
                viewmodel.getAllNotes().observe(viewLifecycleOwner, Observer {notes->
                    //set view ant sorted view
                    val newNotes = sortedView(notes,sort)
                    mediumGirdAdapter.setNote(newNotes)
                })

                //click handle
                mediumGirdAdapter.onItemClickListener = { currentNote ->
                    val bundle = Bundle().apply {
                        putSerializable("note", currentNote)
                    }
                    findNavController().navigate(
                        R.id.action_notesListFragment_to_updateNoteFragment,
                        bundle
                    )

                }

                mediumGirdAdapter.onFavClickListener = { currentList->
                    if(currentList.fav ==0){
                        currentList.fav =1
                    }
                    else{
                        currentList.fav = 0
                    }
                    viewmodel.update(currentList)
                }

                //search
                binding.search.clearFocus()
                viewmodel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->

                    binding.search.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if(newText != null){
                                val filteredList = ArrayList<Notes>()
                                for(i in notes){
                                    if(i.title.toLowerCase(Locale.ROOT).contains(newText)){
                                        filteredList.add(i)
                                    }
                                }

                                if(filteredList.isEmpty()){
                                    Toast.makeText(activity,"No Data found",Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    mediumGirdAdapter.setFilteredList(filteredList)
                                }
                            }
                            return true
                        }
                    })
                })

            }

            largeGridView -> {
                //set RecyclerView
                binding.rvNotesList.apply {
                    val gridManager: RecyclerView.LayoutManager =
                        GridLayoutManager(requireActivity(), 2)
                    layoutManager = gridManager
                    adapter = largeGirdAdapter
                }

                //getAllNotes
                viewmodel.getAllNotes().observe(viewLifecycleOwner, Observer {notes->
                    //set view ant sorted view
                    val newNotes = sortedView(notes,sort)
                    largeGirdAdapter.setNote(newNotes)
                })

                //click handle
                largeGirdAdapter.onItemClickListener = { currentNote ->
                    val bundle = Bundle().apply {
                        putSerializable("note", currentNote)
                    }
                    findNavController().navigate(
                        R.id.action_notesListFragment_to_updateNoteFragment,
                        bundle
                    )

                }

                largeGirdAdapter.onFavClickListener = { currentList->
                    if(currentList.fav ==0){
                        currentList.fav =1
                    }
                    else{
                        currentList.fav = 0
                    }
                    viewmodel.update(currentList)
                }

                //search
                binding.search.clearFocus()
                viewmodel.getAllNotes().observe(viewLifecycleOwner, Observer { notes ->

                    binding.search.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if(newText != null){
                                val filteredList = ArrayList<Notes>()
                                for(i in notes){
                                    if(i.title.toLowerCase(Locale.ROOT).contains(newText)){
                                        filteredList.add(i)
                                    }
                                }

                                if(filteredList.isEmpty()){
                                    Toast.makeText(activity,"No Data found",Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    largeGirdAdapter.setFilteredList(filteredList)
                                }
                            }
                            return true
                        }
                    })
                })
            }

        }
    }

    //delete all items function
    private fun deleteIcon() {
        val alert = AlertDialog.Builder(activity as MainActivity)
        alert.setTitle("Delete All Notes")
        alert.setMessage("Do you want delete all Notes?!")
        alert.setCancelable(false)
        alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            viewmodel.deleteAllNotes()
        })
        alert.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        alert.create()
        alert.show()
    }

    //save data sharedPreferences
    @SuppressLint("SuspiciousIndentation")
    private fun savedData() {
        sharedPreferences =
            this.requireActivity().getSharedPreferences("saveData", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
                editor.putString("view",savedView)
                editor.putString("sort",savedSort)
                editor.apply()
    }

    //retrieve data sharedPreferences
    private fun retrieveData() {
        sharedPreferences =
            this.requireActivity().getSharedPreferences("saveData", Context.MODE_PRIVATE)
        sharedPreferences.getString("view", null)?.let { viewmodel.setAdapterView(it) }
        sharedPreferences.getString("sort",null)?.let { viewmodel.setSorting(it) }
    }

    //sorted fun
    private fun sortedView( notes: List<Notes>, sort : String): List<Notes>{
        var sortedNotes = notes
        when(sort){
            AtoZ->{
                sortedNotes =notes.sortedBy {notes: Notes -> notes.title.toLowerCase(Locale.ROOT) }
            }
            ZtoA->{
                sortedNotes =notes.sortedByDescending {notes: Notes -> notes.title.toLowerCase(Locale.ROOT) }
            }
            ascendingByTime->{
                sortedNotes =notes.sortedByDescending {notes: Notes -> notes.time }
            }
            descendingByTime->{
                sortedNotes =notes.sortedBy {notes: Notes -> notes.time }
            }
        }
        return sortedNotes.sortedByDescending { notes -> notes.fav }
    }

}