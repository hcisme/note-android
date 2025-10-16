package io.github.hcisme.note.enums

enum class CompletionStatusEnum(val status: Int, val desc: String) {
    INCOMPLETE(0, "未完成"),
    COMPLETED(1, "已完成");

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