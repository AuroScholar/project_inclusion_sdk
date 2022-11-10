package com.pi.projectinclusion.model

data class LoginModel(
    var status: String? = null,
    var message: String? = null,
    var token: String? = null,
    var userResponse: UserDataModel? = null,
    var userId: String? = null,
)

data class UserDataModel(
    var firstName: String? = null,
    var lastName: String? = null,
    var emailId: String? = null,
    var mobileNo: String? = null,
    var whatsAppNo: String? = null,
    var gender: Int? = null,
    var stateId: Int? = null,
    var districtId: Int? = null,
    var dob: String? = null,
    var profilePicURL: String? = null,
)

