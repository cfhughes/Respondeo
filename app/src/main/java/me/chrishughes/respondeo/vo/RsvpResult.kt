package me.chrishughes.respondeo.vo

import com.google.gson.annotations.SerializedName

data class RsvpResult(

    @field:SerializedName("response")
    var response: String? = null
)