package com.pi.projectinclusion.model

data class ExcellenceCertificateModel(

    var id: Int,
    var createdDate: String,
    var updatedDate: String? = null,
    var createdBy: Int,
    var updatedBy: Int? = null,
    var status: Int,
    var priority: Int,
    var name: String? = null,
    var certificateType: CertificateTypeModel,
    var typeID: Int,
    var userMaster: UserDataModel,
    var userID: Int,
    var course: String? = null,
    var issueDate: String,
    var score: Double,
    var source: String? = null,
    var languageId: Int,
    var serialNo: String? = null,
    var certificatePath: String? = null,
    var moduleName: String? = null,
)

