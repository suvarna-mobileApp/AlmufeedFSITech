package com.almufeed.technician.datasource.network.models.updateInstruction

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateInstructionSetRequestModel (
    @SerializedName("ProblemList")
    @Expose
    val problem: UpdateInstructionData,
)