package com.almufeed.cafm.datasource.network.models.instructionSet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InstructionData (
    @SerializedName("LineNumber")
    @Expose
    val LineNumber: Int,

    @SerializedName("Steps")
    @Expose
    val Steps: String?,

    @SerializedName("FeedbackType")
    @Expose
    val FeedbackType: Int,

    @SerializedName("Refrecid")
    @Expose
    val Refrecid: Long,

    @SerializedName("taskId")
    @Expose
    val taskId: String,
)