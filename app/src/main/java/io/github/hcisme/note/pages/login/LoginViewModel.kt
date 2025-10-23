package io.github.hcisme.note.pages.login

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.hcisme.note.components.NotificationManager
import io.github.hcisme.note.enums.Message
import io.github.hcisme.note.enums.ResponseCodeEnum
import io.github.hcisme.note.network.CaptchaService
import io.github.hcisme.note.network.UserService
import io.github.hcisme.note.network.model.LoginRequest
import io.github.hcisme.note.network.safeRequestCall
import io.github.hcisme.note.utils.base64ToImageBitmap
import io.github.hcisme.note.utils.getSps
import io.github.hcisme.note.utils.saveToken
import io.github.hcisme.note.utils.saveUserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val application: Application) : AndroidViewModel(application) {
    var email by mutableStateOf("@163.com")
    var password by mutableStateOf("")
    var captcha by mutableStateOf("")
    var captchaKey by mutableStateOf("")
    var emailError by mutableStateOf("")
    var passwordError by mutableStateOf("")
    var captchaError by mutableStateOf("")
    var captchaBitmap by mutableStateOf<ImageBitmap?>(null)
    var passwordVisible by mutableStateOf(false)
    var isLoginIng by mutableStateOf(false)

    fun submit(onSuccess: () -> Unit) {
        emailError = if (email.isEmpty()) "邮箱为必填项" else ""
        passwordError = if (password.isEmpty()) "密码为必填项" else ""
        captchaError = if (captcha.isEmpty()) "验证码为必填项" else ""

        if (emailError.isNotEmpty() || passwordError.isNotEmpty() || captchaError.isNotEmpty()) return
        isLoginIng = true

        viewModelScope.launch {
            safeRequestCall(
                isShowErrorInfo = false,
                call = {
                    withContext(Dispatchers.IO) {
                        UserService.login(
                            LoginRequest(
                                email = email,
                                password = password,
                                captchaKey = captchaKey,
                                captcha = captcha
                            )
                        )
                    }
                },
                onStatusCodeError = { result ->
                    NotificationManager.showNotification(result.info)
                    getCaptcha()
                },
                onFinally = { isLoginIng = false },
                onError = {
                    NotificationManager.showNotification(ResponseCodeEnum.CODE_501.msg)
                },
                onSuccess = { result ->
                    val data = result.data
                    val token = data.token
                    application.getSps().apply {
                        saveToken(token)
                        saveUserInfo(data)
                    }
                    NotificationManager.showNotification(Message.LOGIN_SUCCESS.message)
                    onSuccess()
                }
            )
        }
    }

    fun getCaptcha() {
        viewModelScope.launch {
            safeRequestCall(
                call = { withContext(Dispatchers.IO) { CaptchaService.getCaptcha() } },
                onSuccess = { result ->
                    captchaBitmap = base64ToImageBitmap(result.data.captcha)
                    captchaKey = result.data.captchaKey
                }
            )
        }
    }
}
