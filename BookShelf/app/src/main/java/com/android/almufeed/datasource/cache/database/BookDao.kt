package com.android.almufeed.datasource.cache.database

import androidx.room.*
import com.android.almufeed.datasource.cache.models.book.BookEntity

@Dao
interface BookDao {

    @Insert
    fun insertBook(bookEntity: BookEntity): Long

    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * from Task")
    fun getAllBooks(): List<BookEntity>

    /*@Query("SELECT task_id from Task")
    fun getBookById(): BookEntity*/
}