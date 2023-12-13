package com.android.almufeed.datasource.network.models.tasklist

import com.android.almufeed.datasource.network.models.bookList.BookData
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TaskListResponse(
    @SerializedName("Task")
    @Expose
    val task: ArrayList<BookData>,
)