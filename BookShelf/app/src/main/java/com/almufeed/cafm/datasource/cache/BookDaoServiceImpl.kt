package com.almufeed.cafm.datasource.cache

import com.almufeed.cafm.datasource.cache.database.BookDao
import com.almufeed.cafm.datasource.cache.models.book.BookEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.AttachmentEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.EventsEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.InstructionSetEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.RatingEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.TaskEntity

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

    override suspend fun insert(eventsEntity: EventsEntity): Long {
        return bookDao.insertEvents(eventsEntity)
    }

    override suspend fun insert(ratingEntity: RatingEntity): Long {
        return bookDao.insertRating(ratingEntity)
    }
}