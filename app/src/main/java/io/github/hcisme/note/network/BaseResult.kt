package io.github.hcisme.note.network

/**
 * 网络请求第一层返回的结果
 */
data class BaseResult<out T>(
    val status: String,
    val code: Int,
    val info: String,
    val data: T
)
