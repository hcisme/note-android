package io.github.hcisme.note.pages.setting

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.hcisme.note.BuildConfig
import io.github.hcisme.note.components.Dialog
import io.github.hcisme.note.constants.VersionConstant
import io.github.hcisme.note.enums.DownloadDialogTextEnum.Download
import io.github.hcisme.note.enums.DownloadDialogTextEnum.Install
import io.github.hcisme.note.utils.DownloadProgressManager
import io.github.hcisme.note.utils.withBadge

@Composable
fun CheckVersionItem(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val settingVM = viewModel<SettingViewModel>(context as ComponentActivity)
    var showUpdateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        settingVM.getUpdateVersionInfo()
    }

    LaunchedEffect(showUpdateDialog) {
        if (showUpdateDialog) {
            settingVM.updateConfirmText()
        }
    }

    ListItem(
        modifier = modifier.clickable {
            if (settingVM.updateVersionInfo == null) {
                Toast.makeText(context, "您的应用已经是最新版本了", Toast.LENGTH_SHORT)
                    .show()
                return@clickable
            }
            showUpdateDialog = true
        },
        headlineContent = {
            Text(
                text = "检查版本",
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
        supportingContent = {
            Text(
                text = BuildConfig.BUILD_TYPE,
                style = MaterialTheme.typography.labelMedium
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

                else -> {}
            }
        },
        onDismissRequest = { showUpdateDialog = false }
    ) {
        settingVM.updateVersionInfo?.updateContent?.let {
            Text(it)
        }
    }
}
