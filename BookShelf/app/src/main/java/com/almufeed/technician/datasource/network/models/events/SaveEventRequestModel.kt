package com.almufeed.technician.datasource.network.models.events

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SaveEventRequestModel (
        @SerializedName("EventtoTask")
        @Expose
        val fsiImage: SaveEventData,
        )

data class SaveEventData (
        @SerializedName("taskId")
        @Expose
        val taskId: String,
        @SerializedName("resource")
        @Expose
        val resource: String,
        @SerializedName("Comments")
        @Expose
        val Comments: String,
        @SerializedName("Events")
        @Expose
        val Events: String
)