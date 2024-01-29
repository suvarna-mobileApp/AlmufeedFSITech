package com.almufeed.technician.business.domain.state

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class API(
    @SerializedName("error")
    @Expose
    val error: APIError
)

data class APIError(

    @SerializedName("code")
    @Expose
    val code: Int,
    @SerializedName("message")
    @Expose
    val message: String,
    @SerializedName("status")
    @Expose
    val status: String
) {
    constructor() : this(400, "Something went wrong", "Bad Request")
}