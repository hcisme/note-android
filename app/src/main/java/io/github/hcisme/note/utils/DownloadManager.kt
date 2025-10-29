package io.github.hcisme.note.utils

import android.content.Context
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import io.github.hcisme.note.constants.Constant
import io.github.hcisme.note.network.BaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

/**
 * 下载管理器
 */
class DownloadManager(private val context: Context) {
    private val gson = Gson()

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
                Constant.INNER_DOWNLOAD_DIR_NAME
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
                val contentType = body.contentType()
                val isFileStream = isFileStreamResponse(contentType, response)

                if (!isFileStream) {
                    return@withContext handleJsonResponse(body)
                }

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

    /**
     * 判断响应是否为文件流
     */
    private fun isFileStreamResponse(
        contentType: MediaType?,
        response: Response<ResponseBody>
    ): Boolean {
        //通过 Content-Type 判断
        val isStreamContentType = contentType?.let { type ->
            when {
                type.type == "application" && type.subtype == "octet-stream" -> true
                type.type == "application" && type.subtype.contains("zip") -> true
                type.type == "application" && type.subtype.contains("pdf") -> true
                type.type == "image" -> true
                type.type == "audio" -> true
                type.type == "video" -> true
                else -> false
            }
        } ?: false

        //通过响应头判断
        val contentDisposition = response.headers()["Content-Disposition"]
        val isAttachment = contentDisposition?.contains("attachment") == true

        return isStreamContentType || isAttachment
    }

    /**
     * 处理 JSON 错误响应
     */
    private fun handleJsonResponse(body: ResponseBody): Result<File> {
        return try {
            val responseString = body.string()
            val baseResult = gson.fromJson(responseString, BaseResult::class.java)
            Result.failure(Exception(baseResult.info))
        } catch (e: Exception) {
            Result.failure(Exception("响应解析错误: ${e.message}"))
        }
    }
}

/**
 * 下载进度管理器
 */
object DownloadProgressManager {
    var downloadProgress by mutableStateOf<Float?>(null)
        private set

    fun updateProgress(progress: Float) {
        downloadProgress = progress
    }

    fun resetProgress() {
        downloadProgress = null
    }
}
