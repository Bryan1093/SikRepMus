package com.example.sikrepmus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sikrepmus.data.local.dao.ExpenseDao
import com.example.sikrepmus.data.local.dao.SongDao
import com.example.sikrepmus.data.local.dao.FolderDao
import com.example.sikrepmus.data.local.entities.ExpenseEntity
import com.example.sikrepmus.data.local.entities.SongEntity
import com.example.sikrepmus.data.local.entities.FolderEntity

@Database(
    entities = [ExpenseEntity::class, SongEntity::class, FolderEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun songDao(): SongDao
    abstract fun folderDao(): FolderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sikrepmus_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
