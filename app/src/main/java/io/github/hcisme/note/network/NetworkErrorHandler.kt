package io.github.hcisme.note.network

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

object NetworkErrorHandler {
    fun handleException(e: Exception): NetworkError {
        return when (e) {
            is ConnectException -> NetworkError.CONNECTION_ERROR
            is SocketTimeoutException -> NetworkError.TIMEOUT_ERROR
            is UnknownHostException -> NetworkError.HOST_ERROR
            is SSLHandshakeException -> NetworkError.SSL_ERROR
            else -> NetworkError.UNKNOWN_ERROR
        }
    }

    sealed class NetworkError(val message: String) {
        object CONNECTION_ERROR : NetworkError("网络连接失败")
        object TIMEOUT_ERROR : NetworkError("请求超时")
        object HOST_ERROR : NetworkError("无法连接到服务器")
        object SSL_ERROR : NetworkError("安全连接失败")
        object UNKNOWN_ERROR : NetworkError("网络请求失败")
    }
}
