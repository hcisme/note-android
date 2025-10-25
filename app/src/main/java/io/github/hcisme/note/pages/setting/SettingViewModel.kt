package io.github.hcisme.note.pages.setting

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import io.github.hcisme.note.constants.VersionConstant
import io.github.hcisme.note.network.VersionService
import io.github.hcisme.note.network.createVersionApiService
import io.github.hcisme.note.network.model.VersionModel
import io.github.hcisme.note.network.safeRequestCall
import io.github.hcisme.note.utils.FileTools
import io.github.hcisme.note.utils.getSps
import kotlinx.coroutines.launch
import java.io.File

class SettingViewModel(application: Application) : AndroidViewModel(application) {
    val versionService = createVersionApiService(sharedPreferences = application.getSps())
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

    suspend fun download(
        onProgress: (Float) -> Unit = {},
        onSuccess: (File) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (updateVersionInfo == null) {
            return
        }
        val versionCode = updateVersionInfo!!.versionCode!!
        val versionName = updateVersionInfo!!.versionName!!
        val fileMd5 = updateVersionInfo!!.fileMd5!!
        try {
            // 创建下载目录（应用私有目录，无需权限）
            val downloadDir = File(
                application.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "apk_updates"
            ).apply { if (!exists()) mkdirs() }

            val apkFile = File(downloadDir, "app_v${versionName}_${versionCode}.apk")

            if (apkFile.exists() && FileTools.calculateMd5(apkFile) != fileMd5) {
                apkFile.delete()
            }
            // 如果文件已存在，hash值相同 直接使用
            if (apkFile.exists() && FileTools.calculateMd5(apkFile) == fileMd5) {
                onSuccess(apkFile)
                return
            }

            val response = versionService.downloadNewVersionApp(versionCode, versionName)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    // 获取文件总大小
                    val contentLength = body.contentLength()
                    var totalBytesRead = 0L

                    // 写入文件
                    apkFile.outputStream().use { outputStream ->
                        body.byteStream().use { inputStream ->
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            var bytesRead = inputStream.read(buffer)

                            while (bytesRead >= 0) {
                                outputStream.write(buffer, 0, bytesRead)
                                totalBytesRead += bytesRead

                                // 计算并回调进度
                                if (contentLength > 0) {
                                    val progress =
                                        (totalBytesRead * 100f / contentLength).coerceIn(0f, 100f)
                                    downloadProgress = progress
                                    onProgress(progress)
                                }

                                bytesRead = inputStream.read(buffer)
                            }
                        }
                    }

                    if (FileTools.calculateMd5(apkFile) == fileMd5) {
                        onSuccess(apkFile)
                    } else {
                        apkFile.delete()
                        Log.e("@Note 文件hash异常", "文件hash异常")
                        onError("文件hash不匹配")
                    }
                } ?: run {
                    apkFile.delete()
                    Log.e("@Note 文件下载异常", "下载响应为空")
                    onError("下载响应为空")
                }
            } else {
                apkFile.delete()
                Log.e("@Note 文件下载异常", "下载失败: ${response.code()}")
                onError("下载失败: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("@Note 文件下载异常", "下载异常: ${e.message}")
            onError("下载异常: ${e.message}")
        }
    }
}

fun installApk(context: Context, apkFile: File) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            setDataAndType(apkUri, "application/vnd.android.package-archive")
        }

        // 检查安装权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (context.packageManager.canRequestPackageInstalls()) {
                context.startActivity(intent)
            } else {
                // 请求安装权限
                requestInstallPermission(context)
            }
        } else {
            context.startActivity(intent)
        }

    } catch (e: Exception) {
        Log.e("@Note 安装异常", e.message, e)
        Toast.makeText(context, "安装失败: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun requestInstallPermission(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
        data = "package:${context.packageName}".toUri()
    }

    if (context is Activity) {
        context.startActivityForResult(intent, 0)
    } else {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
