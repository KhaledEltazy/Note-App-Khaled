package com.example.noteappkhaled

import android.app.Application
import com.example.noteappkhaled.database.NoteDatabase
import com.example.noteappkhaled.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NoteApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy {
        NoteDatabase.getDatabase(this,applicationScope)
    }

    val repository by lazy {
        NoteRepository(database)
    }
}