package io.github.hcisme.note.pages.setting

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.components.Dialog
import io.github.hcisme.note.navigation.navigateToLoginAndClearStack
import io.github.hcisme.note.pages.home.user.UserViewModel
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.LocalSharedPreferences
import io.github.hcisme.note.utils.clearToken
import io.github.hcisme.note.utils.clearUserInfo

@Composable
fun LogoutItem(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val sharedPreferences = LocalSharedPreferences.current
    val userVM = viewModel<UserViewModel>(context as ComponentActivity)
    var showLogoutDialog by remember { mutableStateOf(false) }

    // 退出登录按钮 - 固定在底部
    Button(
        onClick = { showLogoutDialog = true },
        modifier = modifier
            .padding(bottom = 32.dp)
            .fillMaxWidth(0.8f)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = "退出登录"
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "退出登录",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }

    Dialog(
        visible = showLogoutDialog,
        confirmButtonText = "确定",
        cancelButtonText = "取消",
        onConfirm = {
            userVM.logout(
                onFinally = {
                    sharedPreferences.clearToken()
                    sharedPreferences.clearUserInfo()
                }
            )
            showLogoutDialog = false
            navHostController.navigateToLoginAndClearStack()
        },
        onDismissRequest = { showLogoutDialog = false }
    ) {
        val username = userVM.userInfo?.username
        val newName = if (username != null) "@$username" else ""

        Text("退出登录？ $newName")
    }
}