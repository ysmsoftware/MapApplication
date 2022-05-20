package com.application.mapapplication.api

import com.application.mapapplication.models.*
import com.application.mapapplication.utils.Constants.DELETE_ROAD_DATA_BY_ID
import com.application.mapapplication.utils.Constants.FIND_BY_ROAD_ID
import com.application.mapapplication.utils.Constants.FIND_BY_USER_ID
import com.application.mapapplication.utils.Constants.GET_ROAD_SUBTYPE_LIST
import com.application.mapapplication.utils.Constants.GET_ROAD_TYPE_LIST
import com.application.mapapplication.utils.Constants.GET_TALUKA_LIST
import com.application.mapapplication.utils.Constants.GET_VILLAGE_LIST_BY_TALUKA
import com.application.mapapplication.utils.Constants.LOGIN_USER
import com.application.mapapplication.utils.Constants.SAVED_ROAD_DATA
import com.application.mapapplication.utils.Constants.SIGNUP_USER
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST(LOGIN_USER)
    suspend fun loginUser(
        @Body loginData: LoginData
    ): Response<ResponseLoginUser>

    @POST(SIGNUP_USER)
    suspend fun signUpUser(
        @Body userDetails: UserDetails
    ): Response<ResponseLoginUser>

    @GET(GET_TALUKA_LIST)
    suspend fun getTalukaList() : Response<ResponseGetTalukaList>

    @GET(GET_VILLAGE_LIST_BY_TALUKA)
    suspend fun getVillageListByTaluka(
        @Path("talukaCode") talukaCode: Int
    ): Response<ResponseGetVillageList>

    @GET(GET_ROAD_TYPE_LIST)
    suspend fun getRoadTypeList(): Response<ResponseGetRoadTypeList>

    @POST(SAVED_ROAD_DATA)
    suspend fun saveRoadData(
        @Body sendRoadData: SendRoadData
    ): Response<ResponseSendRoadData>

    @GET(GET_ROAD_SUBTYPE_LIST)
    suspend fun getRoadSubTypeList(): Response<ResponseGetRoadSubTypeList>

    @GET(FIND_BY_ROAD_ID)
    suspend fun findByRoadId(
        @Path("roadId") roadId: Int
    ): Response<ResponseSendRoadData>

    @GET(FIND_BY_USER_ID)
    suspend fun findByUserId(
        @Path("userId") userId: Int
    ): Response<ResponseUserIdGetData>

    @GET(DELETE_ROAD_DATA_BY_ID)
    suspend fun deleteRoadDataById(
        @Path("roadId") roadId: Int
    ): Response<ResponseDeleteData>

}