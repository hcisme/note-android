package io.github.hcisme.note.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.File

class InstallManager(private val context: Context) {
    fun installApk(apkFile: File) {
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
                    requestInstallPermission()
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
    private fun requestInstallPermission() {
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
}