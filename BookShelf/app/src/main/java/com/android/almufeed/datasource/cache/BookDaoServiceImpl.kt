package com.android.almufeed.datasource.cache

import com.android.almufeed.datasource.cache.database.BookDao
import com.android.almufeed.datasource.cache.models.book.BookEntity

class BookDaoServiceImpl constructor(
    private val bookDao: BookDao
) : BookDaoService {

    override suspend fun insert(bookEntity: BookEntity): Long {
        return bookDao.insertBook(bookEntity)
    }
}