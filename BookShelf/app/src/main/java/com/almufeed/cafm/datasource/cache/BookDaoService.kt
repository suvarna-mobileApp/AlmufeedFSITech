package com.almufeed.cafm.datasource.cache

import com.almufeed.cafm.datasource.cache.models.book.BookEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.AttachmentEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.EventsEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.InstructionSetEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.RatingEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.TaskEntity

interface BookDaoService {
    suspend fun insert(bookEntity: BookEntity): Long
    suspend fun insert(setEntity: InstructionSetEntity): Long
    suspend fun insert(taskEntity: TaskEntity): Long
    suspend fun insert(getInstructionSetEntity: GetInstructionSetEntity): Long
    suspend fun insert(AddAttachmentSetEntity: AttachmentEntity): Long
    suspend fun insert(eventsEntity: EventsEntity): Long
    suspend fun insert(ratingEntity: RatingEntity): Long
}