package com.android.almufeed.datasource.cache

import com.android.almufeed.datasource.cache.database.BookDao
import com.android.almufeed.datasource.cache.models.book.BookEntity
import com.android.almufeed.datasource.cache.models.offlineDB.AttachmentEntity
import com.android.almufeed.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.android.almufeed.datasource.cache.models.offlineDB.InstructionSetEntity
import com.android.almufeed.datasource.cache.models.offlineDB.TaskEntity

class BookDaoServiceImpl constructor(
    private val bookDao: BookDao
) : BookDaoService {

    override suspend fun insert(bookEntity: BookEntity): Long {
        return bookDao.insertBook(bookEntity)
    }

    override suspend fun insert(setEntity: InstructionSetEntity): Long {
        return bookDao.insertSet(setEntity)
    }

    override suspend fun insert(taskEntity: TaskEntity): Long {
        return bookDao.insertTask(taskEntity)
    }

    override suspend fun insert(getInstructionSetEntity: GetInstructionSetEntity): Long {
        return bookDao.insertInstructionSet(getInstructionSetEntity)
    }

    override suspend fun insert(addAttachmentSetEntity: AttachmentEntity): Long {
        return bookDao.insertAddAttachmentSet(addAttachmentSetEntity)
    }
}