package io.github.hcisme.note.enums

import androidx.compose.ui.graphics.Color

enum class CompletionStatusEnum(val status: Int, val desc: String, val color: Color) {
    INCOMPLETE(0, "未完成", Color(0xFFD32F2F)),
    COMPLETED(1, "已完成", Color(0xFF2E7D32));

    companion object {
        /**
         * 通过状态值获取对应枚举信息
         */
        fun getByStatus(status: Int): CompletionStatusEnum? {
            return entries.find { it.status == status }
        }
    }

    override fun toString(): String {
        return "CompletionStatusEnum(status=$status, desc='$desc')"
    }
}