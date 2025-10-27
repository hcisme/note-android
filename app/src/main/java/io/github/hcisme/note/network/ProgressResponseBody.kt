package io.github.hcisme.note.network

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer

class ProgressResponseBody(
    private val url: String,
    private val responseBody: ResponseBody,
    private val progressCallback: (url: String, percent: Int, bytesRead: Long, contentLength: Long) -> Unit
) : ResponseBody() {
    override fun contentType(): MediaType? = responseBody.contentType()

    override fun contentLength(): Long = responseBody.contentLength()

    override fun source(): BufferedSource {
        val source = responseBody.source()
        return object : ForwardingSource(source) {
            var totalBytesRead: Long = 0
            val contentLength = responseBody.contentLength()

            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                if (bytesRead != -1L) {
                    totalBytesRead += bytesRead
                } else {
                    // 到达流尾，将已读计为 contentLength（兼容 chunked）
                    totalBytesRead = contentLength.takeIf { it != -1L } ?: totalBytesRead
                }

                val percent = if (contentLength <= 0) {
                    0
                } else {
                    ((totalBytesRead * 100) / contentLength).toInt().coerceIn(0, 100)
                }
                progressCallback(url, percent, totalBytesRead, contentLength)

                return bytesRead
            }
        }.buffer()
    }
}
