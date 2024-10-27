package com.example.noteappkhaled.model


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity("note_table")
data class Notes @RequiresApi(Build.VERSION_CODES.O) constructor(
    @ColumnInfo("note_title")
    var title: String,
    @ColumnInfo("note_description")
    var description: String
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null
    @ColumnInfo("time and date")
    var time : Long = System.currentTimeMillis()
    @ColumnInfo("favourite")
    var fav : Int = 0
}