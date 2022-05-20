package com.application.mapapplication.models

data class ResponseDeleteData(
    val code: Int,
    val id: Int,
    val message: String,
    val `object`: Any
)