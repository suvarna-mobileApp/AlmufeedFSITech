package com.almufeed.cafm.datasource.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.almufeed.cafm.datasource.cache.models.book.BookEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.AttachmentEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.EventsEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.InstructionSetEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.RatingEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.TaskEntity

@Database(entities =  [BookEntity::class,
                      InstructionSetEntity::class,
                      TaskEntity::class,
                      GetInstructionSetEntity::class,
                      AttachmentEntity::class,
                      RatingEntity::class,
                      EventsEntity::class], version = 1)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        const val DATABASE_NAME: String = "Book_DB"
    }
}