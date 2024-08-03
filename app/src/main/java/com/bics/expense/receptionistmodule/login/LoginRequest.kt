package com.bics.expense.receptionistmodule.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class LoginRequest (

    @SerializedName("userID")
    @Expose
    var userID: String? = null,

    @SerializedName("password")
    @Expose
    var password: String? = null
)