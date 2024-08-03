package com.bics.expense.receptionistmodule.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("accountID")
    @Expose
    var accountID: String? = null,

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null,

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null,

    @SerializedName("email")
    @Expose
    var email: String? = null,

    @SerializedName("phone")
    @Expose
    var phone: String? = null,

    @SerializedName("token")
    @Expose
    var token: String? = null,

    @SerializedName("isActive")
    @Expose
    var isActive: Boolean? = null,

    @SerializedName("role")
    @Expose
    var role: String? = null,

    @SerializedName("profileImage")
    @Expose
    var profileImage : String? = null


)
