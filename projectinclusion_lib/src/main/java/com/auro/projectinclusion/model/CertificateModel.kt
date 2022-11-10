package com.pi.projectinclusion.model

data class CertificateModel(

    var status: String? = null,
    var message: String? = null,
    var errorCode: String? = null,
    var response: List<CertificateDetailModel>? = null
)

data class CertificateDetailModel(
    var id: Int? = null,
    var name: String? = null,
    var certificateType: String? = null,
    var course: String? = null,
    var score: Int? = null,
    var source: String? = null,
    var certificatePath: String? = null,
    var module: String? = null,
    var issueDate: String? = null,

    )

