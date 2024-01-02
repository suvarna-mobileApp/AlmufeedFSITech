package com.android.almufeed.datasource.cache.models.offlineDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Attachment")
data class AttachmentEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var Image1: ByteArray? = null,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var Image2: ByteArray? = null,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var Image3: ByteArray? = null,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var Image4: ByteArray? = null,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var Image5: ByteArray? = null,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var Image6: ByteArray? = null,

    @ColumnInfo(name = "type") var type: Int,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "taskId") var taskId: String,
    @ColumnInfo(name = "resource") var resource: String,
)