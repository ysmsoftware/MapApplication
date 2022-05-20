package com.application.mapapplication.models

data class UserDetails(
    val userId: Int,
    val mobileNumber: String,
    val talukaCode: Int,
    val userName: String,
    val userPassword: String,
    val villageCode: Int
)