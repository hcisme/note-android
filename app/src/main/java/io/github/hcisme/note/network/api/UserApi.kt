package io.github.hcisme.note.network.api

import io.github.hcisme.note.network.BaseResult
import io.github.hcisme.note.network.model.LoginRequest
import io.github.hcisme.note.network.model.UserInfoModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @POST("/api/user/login")
    suspend fun login(@Body loginRequest: LoginRequest): BaseResult<UserInfoModel>

    @GET("/api/user/userInfo")
    suspend fun getUserInfo(): BaseResult<UserInfoModel>

    @GET("/api/user/logout")
    suspend fun logout(): BaseResult<Any?>
}