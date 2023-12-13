package com.android.almufeed.datasource.cache

import com.android.almufeed.datasource.cache.models.book.BookEntity
import com.android.almufeed.datasource.cache.models.offlineDB.InstructionSetEntity
import com.android.almufeed.datasource.cache.models.offlineDB.TaskEntity

interface BookDaoService {
    suspend fun insert(bookEntity: BookEntity): Long
    suspend fun insert(setEntity: InstructionSetEntity): Long
    suspend fun insert(taskEntity: TaskEntity): Long
}