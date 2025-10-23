package io.github.hcisme.note.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.hcisme.note.components.Dialog
import io.github.hcisme.note.navigation.NavigationName
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.LocalSharedPreferences
import io.github.hcisme.note.utils.clearToken

class AuthManager {
    var loginDialogVisible by mutableStateOf(false)

    fun showLoginDialog() {
        loginDialogVisible = true
    }

    fun hideLoginDialog() {
        loginDialogVisible = false
    }
}

@Composable
fun rememberAuthManager() = remember { AuthManager() }

@Composable
fun AuthDialog(authManager: AuthManager) {
    val sharedPreferences = LocalSharedPreferences.current
    val navController = LocalNavController.current

    LaunchedEffect(Unit) {
        // TODO 调取接口 判端token是否有效
    }

    Dialog(
        visible = authManager.loginDialogVisible,
        confirmButtonText = "重新登陆",
        onConfirm = {
            authManager.hideLoginDialog()
            sharedPreferences.clearToken()
            navController.navigate(NavigationName.LOGIN_PAGE) {
                popUpTo(NavigationName.HOME_PAGE) {
                    inclusive = true
                }
            }
        }
    ) {
        Text(text = "您的身份验证过期\n请重新登录")
    }
}
