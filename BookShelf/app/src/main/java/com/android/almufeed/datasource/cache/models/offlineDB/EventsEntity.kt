package com.android.almufeed.datasource.cache.models.offlineDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Events")
data class EventsEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int,

    @ColumnInfo(name = "taskId") var taskId: String,

    @ColumnInfo(name = "resource") var resource: String,

    @ColumnInfo(name = "Comments") var Comments: String,

    @ColumnInfo(name = "Events") var Events: String,

)