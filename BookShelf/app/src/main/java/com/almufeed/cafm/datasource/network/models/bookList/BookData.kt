package com.almufeed.cafm.datasource.network.models.bookList

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BookData(
    @SerializedName("id")
    @Expose
    val id: String?,

    @SerializedName("hazard")
    @Expose
    val hazard: Boolean,

    @SerializedName("scheduledDate")
    @Expose
    val scheduledDate: String?,

    @SerializedName("attendDate")
    @Expose
    val attendDate: String?,

    @SerializedName("TaskId")
    @Expose
    val TaskId: String?,

    @SerializedName("ServiceType")
    @Expose
    val ServiceType: Int,

    @SerializedName("CustAccount")
    @Expose
    val CustAccount: String?,

    @SerializedName("Email")
    @Expose
    val Email: String?,

    @SerializedName("Building")
    @Expose
    val Building: String?,

    @SerializedName("CustId")
    @Expose
    val CustId: String?,

    @SerializedName("CustName")
    @Expose
    val CustName: String?,

    @SerializedName("Location")
    @Expose
    val Location: String?,

    @SerializedName("Problem")
    @Expose
    val Problem: String?,

    @SerializedName("Notes")
    @Expose
    val Notes: String?,

    @SerializedName("LOC")
    @Expose
    val LOC: String?,

    @SerializedName("Priority")
    @Expose
    val Priority: String?,

    @SerializedName("Contract")
    @Expose
    val Contract: String?,

    @SerializedName("Category")
    @Expose
    val Category: String?,

    @SerializedName("Phone")
    @Expose
    val Phone: String?,

    @SerializedName("Discipline")
    @Expose
    val Discipline: String?,

    @SerializedName("CostCenter")
    @Expose
    val CostCenter: String?,

    @SerializedName("Source")
    @Expose
    val Source: String?,

    @SerializedName("Asset")
    @Expose
    val Asset: String?,

    @SerializedName("BeforeCount")
    @Expose
    val beforeCount: Int,

    @SerializedName("AfterCount")
    @Expose
    val afterCount: Int,
)