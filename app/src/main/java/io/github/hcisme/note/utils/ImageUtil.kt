package io.github.hcisme.note.utils

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

object ImageUtil {
    /**
     * 将 Base64 转为 Bitmap
     */
    fun base64ToImageBitmap(base64String: String): ImageBitmap {
        // 移除数据 URI 方案的部分
        val pureBase64Encoded = base64String.substring(base64String.indexOf(",") + 1)

        // 解码 Base64 字符串为字节数组
        val imageBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT)

        // 将字节数组转换为 Bitmap
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        // 将 Bitmap 转换为 ImageBitmap 并返回
        return bitmap.asImageBitmap()
    }
}