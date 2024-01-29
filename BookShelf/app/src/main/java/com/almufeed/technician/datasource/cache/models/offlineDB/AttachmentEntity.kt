package com.almufeed.technician.datasource.cache.models.offlineDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Attachment")
data class AttachmentEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "Image") var Image: String,
    @ColumnInfo(name = "type") var type: Int,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "taskId") var taskId: String,
    @ColumnInfo(name = "resource") var resource: String
)