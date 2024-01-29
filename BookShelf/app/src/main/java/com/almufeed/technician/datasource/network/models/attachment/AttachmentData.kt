package com.almufeed.technician.datasource.network.models.attachment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AttachmentData (
    @SerializedName("Image1")
    @Expose
    val Image1: String,

    @SerializedName("type")
    @Expose
    val type: Int,

    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("taskId")
    @Expose
    val taskId: String,

    @SerializedName("resource")
    @Expose
    val resource: String
    )