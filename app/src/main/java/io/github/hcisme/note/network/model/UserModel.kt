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

data class LoginFormData(
    var email: String = "@163.com",
    var password: String = "",
    var captcha: String = "",
    var captchaKey: String = ""
) {
    fun validate(): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (email.isEmpty()) {
            errors["email"] = "邮箱不能为空"
        }
        if (password.isEmpty()) {
            errors["password"] = "密码不能为空"
        }
        if (captcha.isEmpty()) {
            errors["captcha"] = "验证码不能为空"
        }
        if (captchaKey.isEmpty()) {
            errors["captchaKey"] = "验证码相关错误"
        }
        return errors
    }
}
