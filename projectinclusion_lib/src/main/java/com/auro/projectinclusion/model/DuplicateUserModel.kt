package com.pi.projectinclusion.model

data class DuplicateUserModel(
    var status: String? = null,
    var errorCode: String? = null,
    var message: String? = null,
    var isDuplicateUser: Int? = null,
    var response: UserCheckResponse? = null
)

data class UserCheckResponse(
    var id: String? = null,
    var mobileNo: String? = null,
)


