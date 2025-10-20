package io.github.hcisme.note.pages.home.user

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import io.github.hcisme.note.network.UserService
import io.github.hcisme.note.network.model.UserInfoModel
import io.github.hcisme.note.network.safeRequestCall
import io.github.hcisme.note.utils.getSps
import io.github.hcisme.note.utils.saveToken
import io.github.hcisme.note.utils.saveUserInfo
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    var userInfo by mutableStateOf<UserInfoModel?>(null)

    fun getUserInfo() {
        viewModelScope.launch {
            safeRequestCall(
                call = { UserService.getUserInfo() },
                onSuccess = { result ->
                    result.data.let {
                        userInfo = it
                        application.getSps().apply {
                            saveUserInfo(it)
                            saveToken(it.token)
                        }
                    }
                }
            )
        }
    }

    fun logout(onFinally: () -> Unit = {}) {
        viewModelScope.launch {
            safeRequestCall(
                call = { UserService.logout() },
                onFinally = onFinally
            )
        }
    }
}