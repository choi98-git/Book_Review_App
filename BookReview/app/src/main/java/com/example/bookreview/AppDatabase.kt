package com.example.bookreview

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bookreview.dao.HistoryDao
import com.example.bookreview.dao.ReviewDao
import com.example.bookreview.model.History
import com.example.bookreview.model.Review

@Database(entities = [History::class, Review::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}
