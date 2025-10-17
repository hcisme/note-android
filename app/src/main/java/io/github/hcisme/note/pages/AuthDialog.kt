package io.github.hcisme.note.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.Dialog
import io.github.hcisme.note.constants.NavigationName
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.LocalSharedPreferences
import io.github.hcisme.note.utils.clearToken

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
