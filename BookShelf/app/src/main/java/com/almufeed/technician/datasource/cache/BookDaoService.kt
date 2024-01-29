package com.almufeed.technician.datasource.cache

import com.almufeed.technician.datasource.cache.models.book.BookEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.AttachmentEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.CustomerDetailEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.EventsEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.GetEventEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.InstructionSetEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.RatingEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.TaskEntity

interface BookDaoService {
    suspend fun insert(bookEntity: BookEntity): Long
    suspend fun insert(setEntity: InstructionSetEntity): Long
    suspend fun insert(taskEntity: TaskEntity): Long
    suspend fun insert(getInstructionSetEntity: GetInstructionSetEntity): Long
    suspend fun insert(AddAttachmentSetEntity: AttachmentEntity): Long
    suspend fun insert(eventsEntity: EventsEntity): Long
    suspend fun insert(ratingEntity: RatingEntity): Long
    suspend fun insert(customerDetailEntity: CustomerDetailEntity): Long
    suspend fun insert(getEventEntity: GetEventEntity): Long
}