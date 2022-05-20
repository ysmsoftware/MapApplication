package com.application.mapapplication.repository

import com.application.mapapplication.api.ApiService
import com.application.mapapplication.models.LoginData
import com.application.mapapplication.models.SendRoadData
import com.application.mapapplication.models.UserDetails
import retrofit2.http.Body
import retrofit2.http.Path
import javax.inject.Inject

class MapRepository
@Inject
constructor(private val apiService: ApiService){
    suspend fun loginUser(loginData: LoginData) = apiService.loginUser(loginData)
    suspend fun signUpUser(userDetails: UserDetails) = apiService.signUpUser(userDetails)
    suspend fun getTalukaList() = apiService.getTalukaList()
    suspend fun getVillageListByTaluka(talukaCode: Int) = apiService.getVillageListByTaluka(talukaCode)
    suspend fun getRoadTypeList() = apiService.getRoadTypeList()
    suspend fun saveRoadData(sendRoadData: SendRoadData) = apiService.saveRoadData(sendRoadData)
    suspend fun getRoadSubTypeList() = apiService.getRoadSubTypeList()
    suspend fun findByRoadId(roadId: Int) = apiService.findByRoadId(roadId)
    suspend fun findByUserId(userId: Int) = apiService.findByUserId(userId)
    suspend fun deleteRoadDataById(roadId: Int) = apiService.deleteRoadDataById(roadId)
}