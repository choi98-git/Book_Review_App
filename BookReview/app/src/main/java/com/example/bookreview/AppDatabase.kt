package com.example.bookreview

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bookreview.dao.HistoryDao
import com.example.bookreview.model.History

@Database(entities = [History::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}