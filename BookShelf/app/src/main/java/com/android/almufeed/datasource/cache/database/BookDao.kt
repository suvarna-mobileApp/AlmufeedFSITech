package com.android.almufeed.datasource.cache.database

import androidx.room.*
import com.android.almufeed.datasource.cache.models.book.BookEntity
import com.android.almufeed.datasource.cache.models.offlineDB.AttachmentEntity
import com.android.almufeed.datasource.cache.models.offlineDB.GetInstructionSetEntity
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
    @Insert
    fun insertInstructionSet(taskEntity: GetInstructionSetEntity): Long

    @Insert
    fun insertAddAttachmentSet(attachmentEntity: AttachmentEntity): Long

    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * from Task")
    fun getAllBooks(): List<BookEntity>

    @Query("SELECT * from TaskList")
    fun getAllTask(): List<TaskEntity>

    @Query("SELECT * from Attachment WHERE taskId= :taskId")
    fun getAllAttachment(taskId : String): List<AttachmentEntity>

    /*@Query("SELECT task_id from Task")
    fun getBookById(): BookEntity*/
}