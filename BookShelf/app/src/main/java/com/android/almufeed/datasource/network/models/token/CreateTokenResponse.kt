package com.android.almufeed.datasource.network.models.token

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreateTokenResponse(

    @SerializedName("token_type")
    @Expose
    val token_type: String,
    @SerializedName("expires_in")
    @Expose
    val expires_in: String,
    @SerializedName("ext_expires_in")
    @Expose
    val ext_expires_in: String,
    @SerializedName("expires_on")
    @Expose
    val expires_on: String,
    @SerializedName("not_before")
    @Expose
    val not_before: String,
    @SerializedName("resource")
    @Expose
    val resource: String,
    @SerializedName("access_token")
    @Expose
    val access_token: String,
)