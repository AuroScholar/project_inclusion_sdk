package com.pi.projectinclusion.model

import kotlinx.serialization.Serializable

@Serializable
data class appBridge(
    var sync: Int,
    var cid: String,
    var suspend_data: String,
    var lesson_status: String,
)
