package com.almufeed.technician.datasource.cache.models.offlineDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Rating")
data class RatingEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int,

    @ColumnInfo(name = "customerSignature") var customerSignature: String,

    @ColumnInfo(name = "techiSignature") var techiSignature: String,

    @ColumnInfo(name = "rating") var rating: Double,

    @ColumnInfo(name = "comment") var comment: String,

    @ColumnInfo(name = "dateTime") var dateTime: String,

    @ColumnInfo(name = "task_id") var task_id: String,

    @ColumnInfo(name = "resource") var resource: String,
)