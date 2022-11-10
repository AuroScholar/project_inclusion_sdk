package com.pi.projectinclusion.model

data class UserFeedbacksModel(

    var id: Int,
    var createdDate: String,
    var updatedDate: String? = null,
    var createdBy: Int,
    var updatedBy: Int? = null,
    var status: Int,
    var priority: Int,
    var userMaster: UserDataModel,
    var userID: Int,
    var source: String? = null,
    var project: String? = null,
    var module: String? = null,
    var chapter: String? = null,
    var question: String? = null,
    var ans: String? = null,
)