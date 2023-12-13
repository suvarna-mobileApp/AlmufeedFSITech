package com.android.almufeed.datasource.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.almufeed.datasource.cache.models.book.BookEntity
import com.android.almufeed.datasource.cache.models.offlineDB.InstructionSetEntity

@Database(entities = [BookEntity::class,InstructionSetEntity::class], version = 1)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        const val DATABASE_NAME: String = "Book_DB"
    }
}