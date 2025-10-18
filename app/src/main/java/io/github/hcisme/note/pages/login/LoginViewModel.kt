package io.github.hcisme.note.pages.login

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import io.github.hcisme.note.enums.ResponseCodeEnum
import io.github.hcisme.note.network.CaptchaService
import io.github.hcisme.note.network.UserService
import io.github.hcisme.note.network.model.LoginRequest
import io.github.hcisme.note.utils.base64ToImageBitmap
import io.github.hcisme.note.utils.getSps
import io.github.hcisme.note.utils.saveToken
import io.github.hcisme.note.utils.saveUserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel(private val application: Application) : AndroidViewModel(application) {
    var email by mutableStateOf("147@qq.com")
    var password by mutableStateOf("emmmm000")
    var captcha by mutableStateOf("")
    var captchaKey by mutableStateOf("")
    var emailError by mutableStateOf("")
    var passwordError by mutableStateOf("")
    var captchaError by mutableStateOf("")
    var captchaBitmap by mutableStateOf<ImageBitmap?>(null)
    var passwordVisible by mutableStateOf(false)
    var isLoginIng by mutableStateOf(false)

    suspend fun submit(onSuccess: () -> Unit, onError: () -> Unit) {
        emailError = if (email.isEmpty()) "邮箱为必填项" else ""
        passwordError = if (password.isEmpty()) "密码为必填项" else ""
        captchaError = if (captcha.isEmpty()) "验证码为必填项" else ""

        if (emailError.isNotEmpty() || passwordError.isNotEmpty() || captchaError.isNotEmpty()) return
        isLoginIng = true
        try {
            val result = withContext(Dispatchers.IO) {
                UserService.login(
                    LoginRequest(
                        email = email,
                        password = password,
                        captchaKey = captchaKey,
                        captcha = captcha
                    )
                )
            }

            if (result.code == ResponseCodeEnum.CODE_200.code) {
                val data = result.data
                val token = data.token
                application.getSps().apply {
                    saveToken(token)
                    saveUserInfo(data)
                }
                onSuccess()
            } else {
                getCaptcha(onError = onError)
            }
        } catch (_: Exception) {
            onError()
        } finally {
            isLoginIng = false
        }
    }

    suspend fun getCaptcha(onError: () -> Unit) {
        try {
            val result = withContext(Dispatchers.IO) { CaptchaService.getCaptcha() }
            if (result.code == ResponseCodeEnum.CODE_200.code) {
                captchaBitmap = base64ToImageBitmap(result.data.captcha)
                captchaKey = result.data.captchaKey
            }
        } catch (_: Exception) {
            onError()
        }
    }
}
