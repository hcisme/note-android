package io.github.hcisme.note.network.model

data class UserInfoModel(
    var id: String,
    var username: String,
    var email: String,
    var createdTime: String,
    var updatedTime: String,
    var lastLoginTime: String,
    var token: String,
    var expireAt: Long
)

data class LoginRequest(
    val email: String,
    val password: String,
    val captchaKey: String,
    val captcha: String
)

