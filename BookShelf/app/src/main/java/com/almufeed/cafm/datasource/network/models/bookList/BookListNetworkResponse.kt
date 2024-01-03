package com.almufeed.cafm.datasource.network.models.bookList

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BookListNetworkResponse(

    @SerializedName("status")
    @Expose
    val status: String,
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("task")
    @Expose
    val task: ArrayList<BookData>,
)
