package io.github.hcisme.note.network.api

import io.github.hcisme.note.network.BaseResult
import io.github.hcisme.note.network.model.VersionModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface VersionApi {
    @GET("/api/appVersion/checkUpdate/{versionCode}")
    suspend fun getUpdateVersionInfo(@Path("versionCode") versionCode: Int): BaseResult<VersionModel?>

    @GET("/api/file/download/{versionCode}/{versionName}")
    suspend fun downloadNewVersionApp(
        @Path("versionCode") versionCode: Int,
        @Path("versionName") versionName: String
    ): Response<ResponseBody>
}