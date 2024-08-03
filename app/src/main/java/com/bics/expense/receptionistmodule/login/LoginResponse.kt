package com.bics.expense.receptionistmodule.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class  LoginResponse (
    @SerializedName("success")
    @Expose
    var success: Boolean? = null,

    @SerializedName("error")
    @Expose
    var error: String? = null,

    @SerializedName("data")
    @Expose
    var data: Data? = null,

    @SerializedName("statusCode")
    @Expose
    var statusCode: Int? = null
)
