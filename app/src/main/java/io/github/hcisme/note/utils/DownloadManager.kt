package io.github.hcisme.note.utils

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class DownloadManager(private val context: Context) {

    suspend fun downloadFile(
        downloadRequest: suspend () -> Response<ResponseBody>,
        fileName: String,
        expectedMd5: String? = null,
        onProgress: (Float) -> Unit = {}
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            // 创建下载目录
            val downloadDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "downloads"
            ).apply { if (!exists()) mkdirs() }

            val outputFile = File(downloadDir, fileName)

            // 检查文件是否已存在且MD5匹配
            expectedMd5?.let { md5 ->
                if (outputFile.exists() && FileUtil.calculateMd5(outputFile) == md5) {
                    return@withContext Result.success(outputFile)
                }
            }

            // 如果文件存在但MD5不匹配，删除文件
            if (outputFile.exists() && expectedMd5 != null) {
                outputFile.delete()
            }

            val response = downloadRequest()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("下载失败: ${response.code()}"))
            }

            response.body()?.let { body ->
                val contentLength = body.contentLength()
                var totalBytesRead = 0L

                outputFile.outputStream().use { outputStream ->
                    body.byteStream().use { inputStream ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var bytesRead = inputStream.read(buffer)

                        while (bytesRead >= 0) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead

                            // 更新进度
                            if (contentLength > 0) {
                                val progress =
                                    (totalBytesRead * 100f / contentLength).coerceIn(0f, 100f)
                                onProgress(progress)
                            }

                            bytesRead = inputStream.read(buffer)
                        }
                    }
                }

                // 验证文件MD5
                expectedMd5?.let { md5 ->
                    val actualMd5 = FileUtil.calculateMd5(outputFile)
                    if (actualMd5 != md5) {
                        outputFile.delete()
                        return@withContext Result.failure(Exception("文件hash不匹配"))
                    }
                }

                Result.success(outputFile)
            } ?: Result.failure(Exception("下载响应为空"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
