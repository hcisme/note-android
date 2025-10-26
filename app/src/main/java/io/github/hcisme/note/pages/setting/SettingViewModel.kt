package io.github.hcisme.note.pages.setting

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.hcisme.note.constants.VersionConstant
import io.github.hcisme.note.network.VersionService
import io.github.hcisme.note.network.model.VersionModel
import io.github.hcisme.note.network.safeRequestCall
import io.github.hcisme.note.utils.ApkDownloadManager
import kotlinx.coroutines.launch
import java.io.File

class SettingViewModel(application: Application) : AndroidViewModel(application) {
    private val apkDownloadManager = ApkDownloadManager(application)
    var updateVersionInfo by mutableStateOf<VersionModel?>(null)
    var downloadProgress by mutableFloatStateOf(0f)

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
        onProgress: (Float) -> Unit = {},
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

        viewModelScope.launch {
            apkDownloadManager.downloadApk(
                versionCode = versionCode,
                versionName = versionName,
                expectedMd5 = fileMd5,
                downloadCall = { code, name ->
                    VersionService.downloadNewVersionApp(code, name)
                },
                onProgress = { progress ->
                    downloadProgress = progress
                    onProgress(progress)
                },
                onSuccess = onSuccess,
                onError = { throwable ->
                    Log.e("@Note APK下载异常", "下载失败: ${throwable.message}", throwable)
                    onError(throwable.message ?: "")
                }
            )
        }
    }
}
