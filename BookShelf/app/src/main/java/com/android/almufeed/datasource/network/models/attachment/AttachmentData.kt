package com.android.almufeed.datasource.network.models.attachment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AttachmentData (
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

    @SerializedName("Image7")
    @Expose
    val Image7: String,

    @SerializedName("Image8")
    @Expose
    val Image8: String,

    @SerializedName("Image9")
    @Expose
    val Image9: String,

    @SerializedName("Image10")
    @Expose
    val Image10: String,

    @SerializedName("Image11")
    @Expose
    val Image11: String,

    @SerializedName("Image12")
    @Expose
    val Image12: String,

    @SerializedName("Image13")
    @Expose
    val Image13: String,

    @SerializedName("Image14")
    @Expose
    val Image14: String,

    @SerializedName("Image15")
    @Expose
    val Image15: String,

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