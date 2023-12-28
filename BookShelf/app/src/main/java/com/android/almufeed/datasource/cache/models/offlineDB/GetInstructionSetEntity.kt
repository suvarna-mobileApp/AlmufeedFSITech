package com.android.almufeed.datasource.cache.models.offlineDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GetInstructionSet")
data class GetInstructionSetEntity (
    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "id") var id: Int,

    @ColumnInfo(name = "LineNumber") var LineNumber: Int,

    @ColumnInfo(name = "Steps") var Steps: String?,

    @ColumnInfo(name = "FeedbackType") var FeedbackType: Int,

    @ColumnInfo(name = "Refrecid") var Refrecid: Long,

    @ColumnInfo(name = "taskId") var taskId: String
    )