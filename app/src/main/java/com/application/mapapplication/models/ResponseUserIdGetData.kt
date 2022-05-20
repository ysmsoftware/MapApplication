package com.application.mapapplication.models

import com.google.gson.annotations.SerializedName

data class ResponseUserIdGetData(
    val code: Int,
    val id: Int,
    val message: String,
    @SerializedName("object")
    val sendRoadData: List<SendRoadData>
)