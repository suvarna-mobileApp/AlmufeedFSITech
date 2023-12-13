package com.android.almufeed.datasource.network.models.attachment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetAttachmentData (

    @SerializedName("taskId")
    @Expose
    val taskId: String,

    @SerializedName("datetime")
    @Expose
    val datetime: String,

    @SerializedName("resource")
    @Expose
    val resource: String,

    @SerializedName("Image1")
    @Expose
    val Image1: String,

    @SerializedName("Image2")
    @Expose
    val Image2: String,

    @SerializedName("Image3")
    @Expose
    val Image3: String,

    @SerializedName("Image4")
    @Expose
    val Image4: String,

    @SerializedName("Image5")
    @Expose
    val Image5: String,

    @SerializedName("Image6")
    @Expose
    val Image6: String,

    @SerializedName("description")
    @Expose
    val description: String,

    @SerializedName("type")
    @Expose
    val type: Int,
)