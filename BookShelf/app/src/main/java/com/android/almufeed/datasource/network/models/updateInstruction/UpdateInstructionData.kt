package com.android.almufeed.datasource.network.models.updateInstruction

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateInstructionData (

    @SerializedName("Refrecid")
    @Expose
    val Refrecid: Long,

    @SerializedName("FeedbackType")
    @Expose
    val FeedbackType: Int,

    @SerializedName("AnswerSet")
    @Expose
    val AnswerSet: String,

    @SerializedName("taskId")
    @Expose
    val taskId: String
)