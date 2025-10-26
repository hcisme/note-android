package io.github.hcisme.note.utils

import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest

object FileUtil {
    /**
     * 计算文件的 MD5 值，返回小写十六进制字符串
     * 使用 DigestInputStream 来在读文件的同时更新摘要，适合大文件。
     */
    fun calculateMd5(file: File): String {
        val md = MessageDigest.getInstance("MD5")
        FileInputStream(file).use { fis ->
            DigestInputStream(fis, md).use { dis ->
                // 8KB缓冲区
                val buffer = ByteArray(8 * 1024)
                while (dis.read(buffer) != -1) {
                    // 正常读取即可，DigestInputStream会自动处理哈希计算
                }
            }
        }
        val digest = md.digest()
        // 转为十六进制字符串
        val sb = StringBuilder(digest.size * 2)
        for (b in digest) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
}
