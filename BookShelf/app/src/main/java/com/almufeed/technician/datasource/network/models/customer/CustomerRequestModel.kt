package com.almufeed.technician.datasource.network.models.customer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CustomerRequestModel (
    @SerializedName("FsiRating")
    @Expose
    val FsiRating: CustomerData
    )

data class CustomerData (

    @SerializedName("customerName")
    @Expose
    val customerName: String,
    @SerializedName("Email")
    @Expose
    val Email: String,
    @SerializedName("Phone")
    @Expose
    val Phone: String,
    @SerializedName("taskId")
    @Expose
    val taskId: String,
)