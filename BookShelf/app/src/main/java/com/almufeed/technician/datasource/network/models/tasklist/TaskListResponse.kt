package com.almufeed.technician.datasource.network.models.tasklist

import com.almufeed.technician.datasource.network.models.bookList.BookData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TaskListResponse(
    @SerializedName("Task")
    @Expose
    var task: ArrayList<BookData>
)