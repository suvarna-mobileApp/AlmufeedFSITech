package com.android.almufeed.datasource.cache.database

import androidx.room.*
import com.android.almufeed.datasource.cache.models.book.BookEntity
import com.android.almufeed.datasource.cache.models.offlineDB.InstructionSetEntity
import com.android.almufeed.datasource.cache.models.offlineDB.TaskEntity

@Dao
interface BookDao {
    @Insert
    fun insertBook(bookEntity: BookEntity): Long

    @Insert
    fun insertSet(setEntity: InstructionSetEntity): Long

    @Insert
    fun insertTask(taskEntity: TaskEntity): Long

    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * from Task")
    fun getAllBooks(): List<BookEntity>

    /*@Query("SELECT task_id from Task")
    fun getBookById(): BookEntity*/
}