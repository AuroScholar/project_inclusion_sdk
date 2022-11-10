package com.pi.projectinclusion.model

data class SaveProfileModel(var result: Result? = null)
data class Result(
    var status: String? = null,
    var message: String? = null,
    var title: String? = null,
    var response: List<ProfileResponse>? = null
)

data class ProfileResponse(var profilePicURL: String? = null)