package com.example.noteappkhaled.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteappkhaled.model.Notes
import com.example.noteappkhaled.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewmodel(private val noteRepository: NoteRepository) : ViewModel() {

    //to save last view selected
    private val _adapterView = MutableLiveData<String>("list")
    val adapterView : LiveData<String>
        get() = _adapterView
    fun setAdapterView(value : String){
        _adapterView.value = value
    }

    //to save last sort_option selected
    private val _sort = MutableLiveData<String>("ascending")
    val sort : LiveData<String>
        get() = _sort
    fun setSorting(value : String){
        _sort.value = value
    }

    fun insert(notes : Notes) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.insert(notes)
    }

    fun delete(notes: Notes) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.delete(notes)
    }

    fun update(notes: Notes) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.update(notes)
    }

    fun getAllNotes() = noteRepository.getAllNotes()

    fun deleteAllNotes() = viewModelScope.launch(Dispatchers.IO) { noteRepository.deleteAllNote() }
}