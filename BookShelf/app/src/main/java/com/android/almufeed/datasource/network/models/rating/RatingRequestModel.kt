package com.android.almufeed.datasource.network.models.rating

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RatingRequestModel (
    @SerializedName("FsiRating")
    @Expose
    val FsiRating: RatingData
        )

data class RatingData (
    @SerializedName("customerSignature")
    @Expose
    val customerSignature: String,
    @SerializedName("techiSignature")
    @Expose
    val techiSignature: String,
    @SerializedName("rating")
    @Expose
    val rating: Double,
    @SerializedName("comment")
    @Expose
    val comment: String,
    @SerializedName("dateTime")
    @Expose
    val dateTime: String,
    @SerializedName("taskId")
    @Expose
    val taskId: String,
    @SerializedName("resource")
    @Expose
    val resource: String,
)