package com.almufeed.cafm.datasource.network.models.instructionSet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class InstructionSetResponseModel(
    @SerializedName("ProblemList")
    @Expose
    val problem: ArrayList<InstructionData>,
)