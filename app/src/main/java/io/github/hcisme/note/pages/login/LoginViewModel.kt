package io.github.hcisme.note.pages.login

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.github.hcisme.note.components.NotificationManager
import io.github.hcisme.note.enums.Message
import io.github.hcisme.note.enums.ResponseCodeEnum
import io.github.hcisme.note.network.CaptchaService
import io.github.hcisme.note.network.UserService
import io.github.hcisme.note.network.model.LoginFormData
import io.github.hcisme.note.network.model.UserInfoModel
import io.github.hcisme.note.network.safeRequestCall
import io.github.hcisme.note.utils.ImageUtil
import io.github.hcisme.note.utils.getSps
import io.github.hcisme.note.utils.saveToken
import io.github.hcisme.note.utils.saveUserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val application: Application) : AndroidViewModel(application) {
    private val gson = Gson()
    var loginFormData by mutableStateOf(LoginFormData())
    var errorMap by mutableStateOf(mapOf<String, String>())
    var captchaBitmap by mutableStateOf<ImageBitmap?>(null)
    var isLoginIng by mutableStateOf(false)

    fun submit(onSuccess: () -> Unit) {
        errorMap = loginFormData.validate()
        if (errorMap.isNotEmpty()) return

        isLoginIng = true
        viewModelScope.launch {
            safeRequestCall(
                isShowErrorInfo = false,
                call = { withContext(Dispatchers.IO) { UserService.login(loginFormData) } },
                onStatusCodeError = { result ->
                    if (result.code == ResponseCodeEnum.CODE_600.code) {
                        errorMap = result.data
                    } else {
                        NotificationManager.showNotification(result.info)
                    }
                    getCaptcha()
                },
                onFinally = { isLoginIng = false },
                onError = {
                    getCaptcha()
                    NotificationManager.showNotification(ResponseCodeEnum.CODE_501.msg)
                },
                onSuccess = { result ->
                    val data = gson.fromJson(gson.toJson(result.data), UserInfoModel::class.java)
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
                    captchaBitmap = ImageUtil.base64ToImageBitmap(result.data.captcha)
                    loginFormData = loginFormData.copy(captchaKey = result.data.captchaKey)
                }
            )
        }
    }
}
