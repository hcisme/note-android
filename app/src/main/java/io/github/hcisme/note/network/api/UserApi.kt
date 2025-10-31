package io.github.hcisme.note.network.api

import io.github.hcisme.note.network.BaseResult
import io.github.hcisme.note.network.model.UserInfoModel
import io.github.hcisme.note.pages.login.LoginFormData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    /**
     * UserInfoModel
     */
    @POST("/api/user/login")
    suspend fun login(@Body loginFormData: LoginFormData): BaseResult<Map<String, String>>

    @GET("/api/user/userInfo")
    suspend fun getUserInfo(): BaseResult<UserInfoModel>

    @GET("/api/user/logout")
    suspend fun logout(): BaseResult<Any?>
}