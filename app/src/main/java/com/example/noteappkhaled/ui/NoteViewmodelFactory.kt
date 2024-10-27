package com.example.noteappkhaled.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.noteappkhaled.repository.NoteRepository

class NoteViewmodelFactory(val noteRepository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NoteViewmodel::class.java)){
            return NoteViewmodel(noteRepository) as T
        } else {
            return throw IllegalArgumentException("NoteViewmodel failed to loaded")
        }
    }
}