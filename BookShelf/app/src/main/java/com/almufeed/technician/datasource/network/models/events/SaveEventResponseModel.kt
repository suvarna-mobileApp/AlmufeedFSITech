package com.almufeed.technician.datasource.network.models.events

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveEventResponseModel (
    @SerializedName("result")
    @Expose
    val Success: Boolean
    )