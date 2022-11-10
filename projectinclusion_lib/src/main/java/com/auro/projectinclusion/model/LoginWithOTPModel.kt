package com.pi.projectinclusion.model

data class LoginWithOTPModel(
    var status: String? = null,
    var message: String? = null,
    var token: String? = null,
    var userResponse: UserDataModel? = null,
    var userId: String? = null,
)
