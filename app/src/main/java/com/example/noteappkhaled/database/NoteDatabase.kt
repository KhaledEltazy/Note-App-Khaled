package com.example.noteappkhaled.database

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.noteappkhaled.model.Notes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Notes :: class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao() : NoteDao

    companion object{
        @Volatile
        var Instance : NoteDatabase? = null

        fun getDatabase(context : Context , scope: CoroutineScope) : NoteDatabase {
            return Instance ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "NoteDB.db"
                ).addCallback(NotesDatabaseCallBack(scope))
                    .build()
                Instance = instance
                instance
            }
        }
    }

    private class NotesDatabaseCallBack(private val scope : CoroutineScope) : RoomDatabase.Callback(){

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            Instance?.let{database->
                scope.launch {
                    database.noteDao().insert(Notes("title1","Description1"))
                    database.noteDao().insert(Notes("title2","Description2"))
                }
            }
        }
    }
}