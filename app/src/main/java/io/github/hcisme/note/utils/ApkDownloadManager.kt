package io.github.hcisme.note.utils

import android.content.Context
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class ApkDownloadManager(private val context: Context) {
    private val downloadManager = DownloadManager(context)

    suspend fun downloadApk(
        versionCode: Int,
        versionName: String,
        expectedMd5: String,
        downloadCall: suspend (code: Int, name: String) -> Response<ResponseBody>,
        onProgress: (Float) -> Unit = {},
        onSuccess: (File) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        val fileName = "app_v${versionName}_${versionCode}.apk"
        val result = downloadManager.downloadFile(
            downloadRequest = { downloadCall(versionCode, versionName) },
            fileName = fileName,
            expectedMd5 = expectedMd5,
            onProgress = onProgress
        )
        if (result.isFailure) {
            val throwable = result.exceptionOrNull()!!
            onError(throwable)
            return
        }
        onSuccess(result.getOrNull()!!)
    }
}
