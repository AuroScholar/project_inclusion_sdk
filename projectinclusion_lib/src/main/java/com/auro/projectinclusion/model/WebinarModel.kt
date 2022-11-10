package com.pi.projectinclusion.model

import kotlinx.serialization.Serializable

data class WebinarResponse(
    var status: Int? = null,
    var errorCode: String? = null,
    var message: String? = null,
    var response: ArrayList<WebinarModel>? = null
)

data class WebinarModel(

    var name: String,
    var webinarId: Int,
    var webinarDate: String,
    var description: String,
    var webinarLink: String,
    var priority: Int,
    var languageId: Int,
    var language: String,
    var hostName: String,
    var hostId: Int,
    var totalslot: Int,
    var hostProfilePicUrl: String?,
    var iconURL: String?,
    var subject: String?,
) : java.io.Serializable

data class SlotIdResponse(
    var status: Int? = null,
    var errorCode: String? = null,
    var message: String? = null,
    var response: ArrayList<SlotIdModel>? = null
)

data class SlotIdModel(

    var slotId: Int?,
    var startTime: String?,
    var endTime: String?,
) : java.io.Serializable

