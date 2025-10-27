package io.github.hcisme.note.pages.setting

import android.app.Application
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import io.github.hcisme.note.components.NotificationManager
import io.github.hcisme.note.constants.Constant
import io.github.hcisme.note.constants.VersionConstant
import io.github.hcisme.note.network.VersionService
import io.github.hcisme.note.network.model.VersionModel
import io.github.hcisme.note.network.safeRequestCall
import io.github.hcisme.note.utils.ApkDownloadManager
import io.github.hcisme.note.utils.DownloadProgressManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SettingViewModel(application: Application) : AndroidViewModel(application) {
    private val apkDownloadManager = ApkDownloadManager(application)
    var updateVersionInfo by mutableStateOf<VersionModel?>(null)

    fun getUpdateVersionInfo() {
        viewModelScope.launch {
            safeRequestCall(
                call = { VersionService.getUpdateVersionInfo(versionCode = VersionConstant.CODE) },
                onSuccess = { result ->
                    val data = result.data
                    if (data == null) {
                        return@safeRequestCall
                    }
                    updateVersionInfo = data
                }
            )
        }
    }

    fun download(
        onSuccess: (File) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val versionInfo = updateVersionInfo ?: run {
            onError("版本信息为空")
            return
        }

        val versionCode = versionInfo.versionCode ?: run {
            onError("版本号为空")
            return
        }

        val versionName = versionInfo.versionName ?: run {
            onError("版本名称为空")
            return
        }

        val fileMd5 = versionInfo.fileMd5 ?: run {
            onError("文件MD5为空")
            return
        }

        Toast.makeText(application, "下载开始", Toast.LENGTH_LONG).show()
        DownloadProgressManager.updateProgress(0f)
        viewModelScope.launch {
            apkDownloadManager.downloadApk(
                versionCode = versionCode,
                versionName = versionName,
                expectedMd5 = fileMd5,
                downloadCall = { code, name ->
                    VersionService.downloadNewVersionApp(code, name)
                },
                onSuccess = {
                    Toast.makeText(application, "下载完成", Toast.LENGTH_LONG).show()
                    onSuccess(it)
                },
                onError = { throwable ->
                    onError(throwable.message ?: "")
                    Log.e(
                        "${Constant.APP_LOG_TAG} APK下载异常",
                        "下载失败: ${throwable.message}",
                        throwable
                    )
                }
            )
        }
    }

    fun clearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            val downloadDir = File(
                application.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                Constant.INNER_DOWNLOAD_DIR_NAME
            )

            try {
                if (downloadDir.exists() && downloadDir.isDirectory) {
                    downloadDir.listFiles()?.forEach { file ->
                        if (file.isFile && file.name.contains(".apk")) {
                            file.delete()
                        }
                    }
                }
            } catch (_: Exception) {
            }
            NotificationManager.showNotification("清除完毕")
        }
    }
}
