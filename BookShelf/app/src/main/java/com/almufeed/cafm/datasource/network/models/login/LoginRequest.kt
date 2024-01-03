package com.almufeed.cafm.datasource.network.models.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @SerializedName("_userName")
    @Expose
    val _userName: String,
    @SerializedName("_password")
    @Expose
    val _password: String,
)