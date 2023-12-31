package com.almufeed.cafm.datasource.cache.models.offlineDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Rating")
data class RatingEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var customerSignature: ByteArray? = null,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var techiSignature: ByteArray? = null,

    @ColumnInfo(name = "rating") var rating: Double,

    @ColumnInfo(name = "comment") var comment: String,

    @ColumnInfo(name = "dateTime") var dateTime: String,

    @ColumnInfo(name = "task_id") var task_id: String,

    @ColumnInfo(name = "resource") var resource: String,
)