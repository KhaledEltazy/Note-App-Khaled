package com.example.noteappkhaled.repository

import com.example.noteappkhaled.database.NoteDatabase
import com.example.noteappkhaled.model.Notes

class NoteRepository(val notedb :NoteDatabase) {

    suspend fun insert(notes: Notes) = notedb.noteDao().insert(notes)

    suspend fun delete(notes: Notes) = notedb.noteDao().delete(notes)

    suspend fun update(notes: Notes) = notedb.noteDao().update(notes)

    fun getAllNotes() = notedb.noteDao().getAllNotes()

    suspend fun deleteAllNote() = notedb.noteDao().deleteAllNotes()
}