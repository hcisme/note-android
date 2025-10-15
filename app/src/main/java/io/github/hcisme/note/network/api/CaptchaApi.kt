package io.github.hcisme.note.network.api

import io.github.hcisme.note.network.BaseResult
import io.github.hcisme.note.network.model.CaptchaModel
import retrofit2.http.GET

interface CaptchaApi {
    @GET("/api/user/captcha")
    suspend fun getCaptcha(): BaseResult<CaptchaModel>
}