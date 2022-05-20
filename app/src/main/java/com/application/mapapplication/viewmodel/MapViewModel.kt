package com.application.mapapplication.viewmodel

import android.app.Activity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.application.mapapplication.api.ApiService
import com.application.mapapplication.R
import com.application.mapapplication.models.LoginData
import com.application.mapapplication.models.SendRoadData
import com.application.mapapplication.models.UserDetails
import com.application.mapapplication.repository.MapRepository
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel
@Inject
constructor(private val mapRepository: MapRepository): ViewModel() {
    suspend fun loginUser(loginData: LoginData) = mapRepository.loginUser(loginData)
    suspend fun signUpUser(userDetails: UserDetails) = mapRepository.signUpUser(userDetails)
    suspend fun getTalukaList() = mapRepository.getTalukaList()
    suspend fun getVillageListByTaluka(talukaCode: Int) = mapRepository.getVillageListByTaluka(talukaCode)
    suspend fun getRoadTypeList() = mapRepository.getRoadTypeList()
    suspend fun saveRoadData(sendRoadData: SendRoadData) = mapRepository.saveRoadData(sendRoadData)
    suspend fun getRoadSubTypeList() = mapRepository.getRoadSubTypeList()
    suspend fun findByRoadId(roadId: Int) = mapRepository.findByRoadId(roadId)
    suspend fun findByUserId(userId: Int) = mapRepository.findByUserId(userId)
    suspend fun deleteRoadDataById(roadId: Int) = mapRepository.deleteRoadDataById(roadId)

    fun showErrorSnackBar(activity: Activity,snackBar: Snackbar) {
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.red))
        snackBar.show()
    }
}