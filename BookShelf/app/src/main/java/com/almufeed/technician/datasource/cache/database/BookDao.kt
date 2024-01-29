package com.almufeed.technician.datasource.cache.database

import androidx.room.*
import com.almufeed.technician.datasource.cache.models.book.BookEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.AttachmentEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.CustomerDetailEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.EventsEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.GetEventEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.GetInstructionSetEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.InstructionSetEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.RatingEntity
import com.almufeed.technician.datasource.cache.models.offlineDB.TaskEntity

@Dao
interface BookDao {
    @Insert
    fun insertBook(bookEntity: BookEntity): Long

    @Insert
    fun insertSet(setEntity: InstructionSetEntity): Long

    @Query("UPDATE InstructionSet SET AnswerSet=:AnswerSet WHERE Refrecid = :refrecid")
    fun updateSet(AnswerSet: String, refrecid : Long)

    @Insert
    fun insertTask(taskEntity: TaskEntity): Long

    @Query("UPDATE TaskList SET LOC=:loc WHERE taskId = :taskId")
    fun updateTask(loc : String?,taskId : String?)

    @Delete
    fun deleteTask(taskEntity: TaskEntity)

    @Query("DELETE FROM tasklist WHERE TaskId = :taskId")
    fun deleteTaskByColumnValue(taskId: String?)

    @Query("DELETE FROM GetInstructionSet WHERE TaskId = :taskId")
    fun deleteInstructionByColumnValue(taskId: String?)

    @Query("DELETE FROM Rating WHERE task_id = :taskId")
    fun deleteRatingByColumnValue(taskId: String?)
    @Query("DELETE FROM Events WHERE taskId = :taskId")
    fun deleteEventsByColumnValue(taskId: String?)

    @Query("DELETE FROM InstructionSet WHERE TaskId = :taskId")
    fun deleteStoreInstructionByColumnValue(taskId: String?)

    @Query("DELETE FROM CustomerDetail WHERE TaskId = :taskId")
    fun deleteCustomerDetailsByColumnValue(taskId: String?)

    @Query("DELETE FROM Attachment WHERE TaskId = :taskId")
    fun deleteAttachmentByColumnValue(taskId: String?)

    @Insert
    fun insertInstructionSet(taskEntity: GetInstructionSetEntity): Long
    @Insert
    fun insertAddAttachmentSet(attachmentEntity: AttachmentEntity): Long
    @Insert
    fun insertRating(ratingEntity: RatingEntity): Long
    @Insert
    fun insertCustomerDetail(customerDetailEntity: CustomerDetailEntity): Long
    @Insert
    fun insertEvents(eventEntity: EventsEntity): Long
    @Insert
    fun insertGetEvents(eventEntity: GetEventEntity): Long

    @Query("DELETE FROM GetEvents")
    fun deleteTable()

    @Query("UPDATE Events SET Events=:Event, Comments=:comment WHERE taskId = :taskId")
    fun update(Event : String,taskId : String,comment : String)

    @Query("UPDATE TaskList SET LOC=:LOC WHERE taskId = :taskId")
    fun updateLOC(LOC : String,taskId : String)

    @Query("UPDATE TaskList SET BeforeCount=:BeforeCount WHERE taskId = :taskId")
    fun updateBeforeCount(BeforeCount : Int,taskId : String)

    @Query("UPDATE TaskList SET AfterCount= :AfterCount WHERE taskId = :taskId")
    fun updateAfterCount(AfterCount : Int,taskId : String)
    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * from Task")
    fun getAllBooks(): List<BookEntity>

    @Query("SELECT * from TaskList WHERE userName= :userName")
    fun getAllTask(userName : String): List<TaskEntity>

    @Query("SELECT * from TaskList WHERE taskId= :taskId")
    fun getTaskFromId(taskId : String): TaskEntity

    @Query("SELECT * from Attachment WHERE taskId= :taskId")
    fun getAllAttachment(taskId : String): List<AttachmentEntity>

    @Query("SELECT * from Attachment")
    fun getAllAttachmentSet(): List<AttachmentEntity>

    @Query("SELECT * from InstructionSet")
    fun getAllInstructionSet(): List<InstructionSetEntity>
    @Query("SELECT * from customerdetail")
    fun getAllCustomerDetails(): List<CustomerDetailEntity>

    @Query("SELECT Refrecid from InstructionSet WHERE Refrecid= :Refrecid AND taskId= :taskId")
    fun getRefId(Refrecid : Long,taskId: String): Long

    @Query("SELECT * from GetInstructionSet WHERE taskId= :taskId")
    fun AllInstructionSet(taskId : String): List<GetInstructionSetEntity>

    @Query("SELECT * from Rating")
    fun getAllRating(): List<RatingEntity>

    @Query("SELECT * from Events")
    fun getAllEvents(): List<EventsEntity>
    @Query("SELECT * from GetEvents")
    fun getAllAddEvents(): List<GetEventEntity>

    @Query("SELECT Events from Events WHERE taskId= :taskId")
    fun getEventsForTask(taskId : String): String

    /*@Query("SELECT task_id from Task")
    fun getBookById(): BookEntity*/
}