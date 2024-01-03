package com.almufeed.cafm.datasource.network.models.token

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreateTokenRequest(

    @SerializedName("client_id")
    @Expose
    val client_id: String,
    @SerializedName("client_secret")
    @Expose
    val client_secret: String,
    @SerializedName("grant_type")
    @Expose
    val grant_type: String,
    @SerializedName("resource")
    @Expose
    val resource: String,
)