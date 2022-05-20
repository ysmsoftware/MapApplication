package com.application.mapapplication.models

data class SendRoadData(
    val roadId: Int,
    val actualRoadLength: String,
    val actualRoadWidth: String,
    val customiseCode: String,
    val latitudeLongitude: List<String>,
    val pincode: String,
    val roadLength: String,
    val roadName: String,
    val roadTypeId: List<Int>,
    val roadWidth: String,
    val userId : Int,
    val roadSubTypeId : List<Int>,
    val roadType: List<String>,
    val roadSubType: List<String>
)