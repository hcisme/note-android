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
import io.github.hcisme.note.enums.DownloadDialogTextEnum
import io.github.hcisme.note.network.VersionService
import io.github.hcisme.note.network.model.VersionModel
import io.github.hcisme.note.network.safeRequestCall
import io.github.hcisme.note.utils.ApkDownloadManager
import io.github.hcisme.note.utils.DownloadProgressManager
import io.github.hcisme.note.utils.FileUtil
import io.github.hcisme.note.utils.InstallManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class SettingViewModel(application: Application) : AndroidViewModel(application) {
    private val apkDownloadManager = ApkDownloadManager(application)
    val installManager = InstallManager(context = application)
    var updateVersionInfo by mutableStateOf<VersionModel?>(null)
    var confirmTextEnum by mutableStateOf(DownloadDialogTextEnum.Download)

    fun getUpdateVersionInfo() {
        viewModelScope.launch {
            safeRequestCall(
                call = { VersionService.getUpdateVersionInfo(versionCode = VersionConstant.CODE) },
                onSuccess = { result ->
                    val data = result.data ?: return@safeRequestCall
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

        val clearCacheJob = clearCache(showMessage = false)
        Toast.makeText(application, "下载开始", Toast.LENGTH_SHORT).show()
        DownloadProgressManager.updateProgress(0f)
        viewModelScope.launch {
            clearCacheJob.join()
            apkDownloadManager.downloadApk(
                versionCode = versionCode,
                versionName = versionName,
                expectedMd5 = fileMd5,
                downloadCall = { code, name ->
                    VersionService.downloadNewVersionApp(code, name)
                },
                onSuccess = {
                    Toast.makeText(application, "下载完成", Toast.LENGTH_SHORT).show()
                    DownloadProgressManager.resetProgress()
                    installManager.installApk(apkFile = it)
                    onSuccess(it)
                },
                onError = { throwable ->
                    DownloadProgressManager.resetProgress()
                    Toast.makeText(application, throwable.message, Toast.LENGTH_LONG).show()
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

    fun updateConfirmText() {
        val info = updateVersionInfo ?: run {
            confirmTextEnum = DownloadDialogTextEnum.Download
            return
        }

        val code = info.versionCode ?: run {
            confirmTextEnum = DownloadDialogTextEnum.Download
            return
        }

        val name = info.versionName ?: run {
            confirmTextEnum = DownloadDialogTextEnum.Download
            return
        }

        val expectedMd5 = info.fileMd5 ?: run {
            confirmTextEnum = DownloadDialogTextEnum.Download
            return
        }

        val outputFile = getTargetFile(code = code, name = name)

        // 判断逻辑
        confirmTextEnum = when {
            // 文件存在且MD5匹配 -> 安装
            isFileDownloadedAndValid(outputFile, expectedMd5) -> {
                DownloadDialogTextEnum.Install
            }
            // 正在下载中 -> 下载中
            isDownloadInProgress() -> {
                DownloadDialogTextEnum.Downloading
            }
            // 默认情况 -> 下载
            else -> {
                DownloadDialogTextEnum.Download
            }
        }
    }

    fun clearCache(showMessage: Boolean = true): Job {
        return viewModelScope.launch(Dispatchers.IO) {
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
            if (showMessage) {
                NotificationManager.showNotification("清除完毕")
            }
        }
    }

    /**
     * 得到目标文件
     */
    fun getTargetFile(code: Int, name: String): File {
        val fileName = ApkDownloadManager.genApkName(code = code, name = name)
        val downloadDir = File(
            application.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            Constant.INNER_DOWNLOAD_DIR_NAME
        ).apply { if (!exists()) mkdirs() }
        return File(downloadDir, fileName)
    }

    /**
     * 检查文件是否已下载且有效
     */
    private fun isFileDownloadedAndValid(file: File, expectedMd5: String): Boolean {
        return try {
            if (!file.exists() || !file.isFile) return false

            // 检查文件大小（可选，防止损坏的文件）
            if (file.length() == 0L) return false

            // 验证MD5
            val actualMd5 = FileUtil.calculateMd5(file)
            actualMd5 == expectedMd5
        } catch (_: Exception) {
            false
        }
    }

    /**
     * 检查是否正在下载
     */
    private fun isDownloadInProgress() = DownloadProgressManager.downloadProgress != null
}
