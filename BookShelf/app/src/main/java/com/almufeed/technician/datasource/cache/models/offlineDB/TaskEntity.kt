package com.almufeed.technician.datasource.cache.models.offlineDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TaskList")
data class TaskEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int,

    @ColumnInfo(name = "tid") var tid: String?,

    @ColumnInfo(name = "hazard") var hazard: Boolean,

    @ColumnInfo(name = "scheduledDate") var scheduledDate: String?,

    @ColumnInfo(name = "attendDate") var attendDate: String?,

    @ColumnInfo(name = "TaskId") var TaskId: String?,

    @ColumnInfo(name = "ServiceType") var ServiceType: Int,

    @ColumnInfo(name = "CustAccount") var CustAccount: String?,

    @ColumnInfo(name = "Email") var Email: String?,

    @ColumnInfo(name = "Building") var Building: String?,

    @ColumnInfo(name = "CustId") var CustId: String?,

    @ColumnInfo(name = "CustName") var CustName: String?,

    @ColumnInfo(name = "Location") var Location: String?,

    @ColumnInfo(name = "Problem") var Problem: String?,

    @ColumnInfo(name = "Notes") var Notes: String?,

    @ColumnInfo(name = "LOC") var LOC: String?,

    @ColumnInfo(name = "Priority") var Priority: String?,

    @ColumnInfo(name = "Contract") var Contract: String?,

    @ColumnInfo(name = "Category") var Category: String?,

    @ColumnInfo(name = "Phone") var Phone: String?,

    @ColumnInfo(name = "Discipline") var Discipline: String?,

    @ColumnInfo(name = "CostCenter") var CostCenter: String?,

    @ColumnInfo(name = "Source") var Source: String?,

    @ColumnInfo(name = "Asset") var Asset: String?,

    @ColumnInfo(name = "BeforeCount") var BeforeCount: Int,

    @ColumnInfo(name = "AfterCount") var AfterCount: Int,

    @ColumnInfo(name = "userName") var UserName: String,
)