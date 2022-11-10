package com.auro.projectinclusion.model

import com.google.gson.annotations.SerializedName

data class WebinarBookingModel(
    @SerializedName("userId"      ) var userId      : Int?    = null,
    @SerializedName("webinarId"   ) var webinarId   : Int?    = null,
    @SerializedName("slotId"      ) var slotId      : Int?    = null,
    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("createdDate" ) var createdDate : String? = null,
    @SerializedName("updatedDate" ) var updatedDate : String? = null,
    @SerializedName("createdBy"   ) var createdBy   : Int?    = null,
    @SerializedName("updatedBy"   ) var updatedBy   : Int?    = null,
    @SerializedName("status"      ) var status      : Int?    = null,
    @SerializedName("priority"    ) var priority    : Int?    = null
)
