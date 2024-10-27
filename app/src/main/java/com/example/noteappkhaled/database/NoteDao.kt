package com.example.noteappkhaled.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.noteappkhaled.model.Notes

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(notes: Notes)

    @Delete
    suspend fun delete(notes: Notes)

    @Update
    suspend fun update(notes:Notes)

    @Query("SELECT * FROM note_table")
    fun getAllNotes() : LiveData<List<Notes>>

    @Query("DELETE FROM note_table")
    suspend fun deleteAllNotes()
}