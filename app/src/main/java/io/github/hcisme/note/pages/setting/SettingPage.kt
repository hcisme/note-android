package io.github.hcisme.note.pages.setting

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.BuildConfig
import io.github.hcisme.note.components.Dialog
import io.github.hcisme.note.constants.VersionConstant
import io.github.hcisme.note.enums.DownloadDialogTextEnum.Download
import io.github.hcisme.note.enums.DownloadDialogTextEnum.Downloading
import io.github.hcisme.note.enums.DownloadDialogTextEnum.Install
import io.github.hcisme.note.navigation.navigateToLoginAndClearStack
import io.github.hcisme.note.pages.home.user.UserViewModel
import io.github.hcisme.note.utils.DownloadProgressManager
import io.github.hcisme.note.utils.LocalNavController
import io.github.hcisme.note.utils.LocalSharedPreferences
import io.github.hcisme.note.utils.clearToken
import io.github.hcisme.note.utils.clearUserInfo
import io.github.hcisme.note.utils.withBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val navHostController = LocalNavController.current
    val sharedPreferences = LocalSharedPreferences.current
    val userVM = viewModel<UserViewModel>(context as ComponentActivity)
    val settingVM = viewModel<SettingViewModel>()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        settingVM.getUpdateVersionInfo()
    }

    LaunchedEffect(showUpdateDialog) {
        if (showUpdateDialog) {
            settingVM.updateConfirmText()
        }
    }

    Scaffold(
        modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "设置") },
                navigationIcon = {
                    IconButton(
                        onClick = { navHostController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            ListItem(
                modifier = Modifier.clickable {
                    if (settingVM.updateVersionInfo == null) {
                        Toast.makeText(context, "您的应用已经是最新版本了", Toast.LENGTH_SHORT)
                            .show()
                        return@clickable
                    }
                    showUpdateDialog = true
                },
                headlineContent = {
                    Text(
                        text = "检查版本(${BuildConfig.BUILD_TYPE})",
                        modifier = Modifier
                            .withBadge(
                                showBadge = settingVM.updateVersionInfo != null,
                                offset = {
                                    Offset(
                                        size.width,
                                        with(density) { 2.dp.toPx() })
                                }
                            )
                    )
                },
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "version_name: v${VersionConstant.NAME}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            )
                            Text(
                                text = "version_code: ${VersionConstant.CODE}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            )
                        }
                        if (DownloadProgressManager.downloadProgress != null) {
                            Box(
                                modifier = Modifier.width(72.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("下载中${DownloadProgressManager.downloadProgress}%")
                            }
                        }
                    }
                },
                tonalElevation = 1.dp,
                shadowElevation = 4.dp
            )

            ListItem(
                modifier = Modifier.clickable { settingVM.clearCache() },
                headlineContent = { Text(text = "清除缓存") },
                tonalElevation = 1.dp,
                shadowElevation = 4.dp
            )

            Spacer(modifier = Modifier.weight(1f))

            // 退出登录按钮 - 固定在底部
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .align(alignment = Alignment.CenterHorizontally),
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
        }
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

    Dialog(
        visible = showUpdateDialog,
        confirmButtonText = settingVM.confirmTextEnum.text,
        cancelButtonText = "取消",
        title = {
            val info = settingVM.updateVersionInfo
            val versionName = if (info?.versionName == null) "" else "v${info.versionName}"
            val versionCode = if (info?.versionCode == null) "" else "(${info.versionCode})"
            Text("更新内容 $versionName $versionCode")
        },
        onConfirm = {
            showUpdateDialog = false
            when (settingVM.confirmTextEnum) {
                Download -> {
                    settingVM.download(onSuccess = { showUpdateDialog = false })
                }

                Install -> {
                    val info = settingVM.updateVersionInfo!!
                    settingVM.installManager.installApk(
                        settingVM.getTargetFile(
                            code = info.versionCode!!,
                            name = info.versionName!!
                        )
                    )
                }

                Downloading -> {}
            }
        },
        onDismissRequest = { showUpdateDialog = false }
    ) {
        settingVM.updateVersionInfo?.updateContent?.let {
            Text(it)
        }
    }
}
