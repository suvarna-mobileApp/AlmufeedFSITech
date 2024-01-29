package com.almufeed.technician.datasource.network.models.customer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CustomerResponseModel (
    @SerializedName("result")
    @Expose
    val Success: Boolean
)