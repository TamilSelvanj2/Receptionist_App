package com.bics.expense.receptionistmodule.scheduleDoctor

import com.google.gson.annotations.SerializedName


class SpecialityId {
    @SerializedName("doctorID")
    var doctorID: String? = null

    @SerializedName("firstName")
    var firstName: String? = null

    @SerializedName("lastName")
    var lastName: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("gender")
    var gender: String? = null

    @SerializedName("phone")
    var phone: String? = null

    override fun toString(): String {

        return "Dr.$firstName $lastName".trim()
    }
}