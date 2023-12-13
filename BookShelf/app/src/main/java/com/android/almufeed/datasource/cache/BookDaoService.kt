package com.android.almufeed.datasource.cache

import com.android.almufeed.datasource.cache.models.book.BookEntity

interface BookDaoService {

    suspend fun insert(bookEntity: BookEntity): Long
}