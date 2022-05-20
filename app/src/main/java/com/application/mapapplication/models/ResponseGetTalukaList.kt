package com.application.mapapplication.models

import com.google.gson.annotations.SerializedName

data class ResponseGetTalukaList(
    val code: Int,
    val id: Int,
    val message: String,
    @SerializedName("object")
    val taluka: List<Taluka>
)