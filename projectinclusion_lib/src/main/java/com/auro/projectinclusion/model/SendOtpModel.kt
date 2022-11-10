package com.pi.projectinclusion.model

data class SendOtpModel(
    var status: String? = null,
    var errorCode: String? = null,
    var message: String? = null,
    var response: String? = null
)