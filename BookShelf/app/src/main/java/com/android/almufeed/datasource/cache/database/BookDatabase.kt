package com.android.almufeed.datasource.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.almufeed.datasource.cache.models.book.BookEntity
import com.android.almufeed.datasource.cache.models.offlineDB.AttachmentEntity
import com.android.almufeed.datasource.cache.models.offlineDB.EventsEntity
import com.android.almufeed.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.android.almufeed.datasource.cache.models.offlineDB.InstructionSetEntity
import com.android.almufeed.datasource.cache.models.offlineDB.RatingEntity
import com.android.almufeed.datasource.cache.models.offlineDB.TaskEntity

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