package io.github.hcisme.note.utils

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

object FileUtil {
    private val HEX_CHARS = "0123456789abcdef".toCharArray()

    /**
     * 计算文件的 MD5 值，返回小写十六进制字符串
     * 使用 DigestInputStream 来在读文件的同时更新摘要，适合大文件。
     */
    fun calculateMd5(file: File): String {
        val md = MessageDigest.getInstance("MD5")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var read = fis.read(buffer)
            while (read != -1) {
                md.update(buffer, 0, read)
                read = fis.read(buffer)
            }
        }

        return md.digest().joinToString("") { byte ->
            val i = byte.toInt() and 0xFF
            HEX_CHARS[i ushr 4].toString() + HEX_CHARS[i and 0x0F]
        }
    }
}
