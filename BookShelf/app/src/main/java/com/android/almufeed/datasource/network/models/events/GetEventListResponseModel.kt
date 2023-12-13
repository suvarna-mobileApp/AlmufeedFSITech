package com.android.almufeed.datasource.network.models.events

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetEventListResponseModel (
    @SerializedName("EventList")
    @Expose
    val EventList: ArrayList<GetEventData>
    )

data class GetEventData (
    @SerializedName("TaskEvent")
    @Expose
    val TaskEvent: String
)
