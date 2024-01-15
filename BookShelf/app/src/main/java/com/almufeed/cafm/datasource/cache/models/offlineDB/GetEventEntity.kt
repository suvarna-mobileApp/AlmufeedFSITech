package com.almufeed.cafm.datasource.cache.models.offlineDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GetEvents")
data class GetEventEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int,

    @ColumnInfo(name = "EventList") var EventList: String,
)