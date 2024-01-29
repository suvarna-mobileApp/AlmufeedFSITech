package com.almufeed.technician.datasource.network.models.instructionSet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InstructionSetRequestModel(

    @SerializedName("_ID")
    @Expose
    val _ID: String
)