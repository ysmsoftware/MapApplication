package com.application.mapapplication.models

import com.google.gson.annotations.SerializedName

data class ResponseGetRoadTypeList(
    val code: Int,
    val id: Int,
    val message: String,
    @SerializedName("object")
    val roadType: List<RoadType>
)