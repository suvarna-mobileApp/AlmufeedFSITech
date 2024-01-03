package com.almufeed.cafm.datasource.network.models.tasklist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TaskListRequest(

    @SerializedName("_ID")
    @Expose
    val _ID: String
)