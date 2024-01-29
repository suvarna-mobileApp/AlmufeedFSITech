package com.almufeed.technician.datasource.cache.models.offlineDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CustomerDetail")
data class CustomerDetailEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "customerName") var customerName: String,
    @ColumnInfo(name = "Email") var Email: String,
    @ColumnInfo(name = "Phone") var Phone: String,
    @ColumnInfo(name = "taskId") var taskId: String,
)