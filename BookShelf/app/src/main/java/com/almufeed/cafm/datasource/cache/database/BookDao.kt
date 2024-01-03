package com.almufeed.cafm.datasource.cache.database

import androidx.room.*
import com.almufeed.cafm.datasource.cache.models.book.BookEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.AttachmentEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.EventsEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.InstructionSetEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.RatingEntity
import com.almufeed.cafm.datasource.cache.models.offlineDB.TaskEntity

@Dao
interface BookDao {
    @Insert
    fun insertBook(bookEntity: BookEntity): Long

    @Insert
    fun insertSet(setEntity: InstructionSetEntity): Long

    @Insert
    fun insertTask(taskEntity: TaskEntity): Long

    @Delete
    fun deleteTask(taskEntity: TaskEntity)

    @Query("DELETE FROM tasklist WHERE TaskId = :taskId")
    fun deleteTaskByColumnValue(taskId: String?)

    @Query("DELETE FROM GetInstructionSet WHERE TaskId = :taskId")
    fun deleteInstructionByColumnValue(taskId: String?)

    @Insert
    fun insertInstructionSet(taskEntity: GetInstructionSetEntity): Long
    @Insert
    fun insertAddAttachmentSet(attachmentEntity: AttachmentEntity): Long
    @Insert
    fun insertRating(ratingEntity: RatingEntity): Long
    @Insert
    fun insertEvents(eventEntity: EventsEntity): Long

    @Query("UPDATE Events SET Events=:Event, Comments=:comment WHERE taskId = :taskId")
    fun update(Event : String,taskId : String,comment : String)
    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * from Task")
    fun getAllBooks(): List<BookEntity>

    @Query("SELECT * from TaskList")
    fun getAllTask(): List<TaskEntity>

    @Query("SELECT * from TaskList WHERE taskId= :taskId")
    fun getTaskFromId(taskId : String): TaskEntity

    @Query("SELECT * from Attachment WHERE taskId= :taskId")
    fun getAllAttachment(taskId : String): List<AttachmentEntity>

    @Query("SELECT * from Attachment")
    fun getAllAttachmentSet(): List<AttachmentEntity>

    @Query("SELECT * from InstructionSet")
    fun getAllInstructionSet(): List<InstructionSetEntity>

    @Query("SELECT * from GetInstructionSet WHERE taskId= :taskId")
    fun AllInstructionSet(taskId : String): List<GetInstructionSetEntity>

    @Query("SELECT * from Rating")
    fun getAllRating(): List<RatingEntity>

    @Query("SELECT * from Events")
    fun getAllEvents(): List<EventsEntity>

    @Query("SELECT Events from Events WHERE taskId= :taskId")
    fun getEventsForTask(taskId : String): String

    /*@Query("SELECT task_id from Task")
    fun getBookById(): BookEntity*/
}