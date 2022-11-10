package com.pi.projectinclusion.model

data class UserAuthenticationModel(
    var token: String? = null,
    var status: String? = null,
    var message: String? = null
)

