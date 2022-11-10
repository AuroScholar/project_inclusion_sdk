package com.pi.projectinclusion.model

data class ResetPasswordModel(
    var result: ResetResult? = null
)

data class ResetResult(

    var status: String? = null,
    var errorCode: String? = null,
    var message: String? = null,
    var response: String? = null

)

data class SetPasswordModel(
    var result: Boolean? = null
)
