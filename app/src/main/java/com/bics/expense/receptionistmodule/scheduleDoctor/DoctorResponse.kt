package com.bics.expense.receptionistmodule.scheduleDoctor

import com.google.gson.annotations.SerializedName


class DoctorResponse {
    @SerializedName("success")
    var success: Boolean? = null

    @SerializedName("error")
    var error: String? = null

    @SerializedName("data")
    var data: ArrayList<SpecialityId>? = null

    @SerializedName("statusCode")
    var statusCode: Int? = null
}

