package io.github.hcisme.note.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.hcisme.note.components.NotificationManager
import io.github.hcisme.note.enums.ResponseCodeEnum
import io.github.hcisme.note.pages.AuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Request {
    lateinit var retrofit: Retrofit
    private var tokenProvider: (() -> String?)? = null

    /**
     * 初始化方法
     * @param baseUrl 基础地址
     */
    fun init(
        baseUrl: String,
        authManager: AuthManager,
        tokenProvider: (() -> String?)? = null
    ) {
        this.tokenProvider = tokenProvider

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(NetworkConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NetworkConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(createRequestInterceptor())
            .addInterceptor(createResponseInterceptor(authManager))
            .addInterceptor(createNetworkErrorInterceptor())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 创建Service接口实例
     */
    inline fun <reified T> createService(): T = retrofit.create(T::class.java)

    /**
     * 请求拦截器（添加公共参数/头部）
     */
    private fun createRequestInterceptor() = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Content-Type", "application/json")

        tokenProvider?.invoke()?.let {
            if (it.isNotEmpty()) {
                requestBuilder.addHeader("token", it)
            }
        }

        val newRequest = requestBuilder.build()
        chain.proceed(newRequest)
    }

    /**
     * 响应拦截器
     */
    private fun createResponseInterceptor(authManager: AuthManager) = Interceptor { chain ->
        val response = chain.proceed(chain.request())
        val responseBody = response.body

        responseBody?.let {
            val contentType = responseBody.contentType()
            val isStream = contentType?.type == "application" && (
                    contentType.subtype == "octet-stream" ||
                            contentType.subtype.contains("zip") ||
                            contentType.subtype.contains("pdf") ||
                            contentType.subtype.contains("image") ||
                            response.request.url.toString().contains("/download/") // 特定下载端点
                    )

            if (isStream) {
                // 如果是流数据，直接返回原始响应
                response
            } else {
                val source = it.source()
                source.request(Long.MAX_VALUE)
                val buffer = source.buffer.clone()
                val responseString = buffer.readUtf8()

                try {
                    val type = object : TypeToken<BaseResult<*>>() {}.type
                    val baseResult = Gson().fromJson<BaseResult<*>>(responseString, type)
                    if (baseResult.code == ResponseCodeEnum.CODE_401.code) {
                        authManager.showLoginDialog()
                    }
                } catch (e: Exception) {
                    Log.e("Note FormatError", "${e.message}", e)
                }

                // 重建response供后续处理
                response.newBuilder()
                    .body(responseString.toResponseBody(it.contentType()))
                    .build()
            }
        } ?: response
    }

    /**
     * 网络错误拦截器
     */
    private fun createNetworkErrorInterceptor() = Interceptor { chain ->
        try {
            chain.proceed(chain.request())
        } catch (e: Exception) {
            val networkError = NetworkErrorHandler.handleException(e)
            Log.e("Note InterceptorError", networkError.message, e)
            throw e
        }
    }
}

suspend fun <T> safeRequestCall(
    isShowErrorInfo: Boolean = true,
    call: suspend () -> BaseResult<T>,
    onError: () -> Unit = {},
    onStatusCodeError: (result: BaseResult<T>) -> Unit = {},
    onFinally: () -> Unit = {},
    onSuccess: (result: BaseResult<T>) -> Unit = {}
) {
    return try {
        val result = withContext(Dispatchers.IO) { call() }
        if (result.code == ResponseCodeEnum.CODE_200.code) {
            onSuccess(result)
        } else {
            if (isShowErrorInfo) {
                NotificationManager.showNotification(ResponseCodeEnum.CODE_501.msg)
            }
            onStatusCodeError(result)
        }
    } catch (e: Exception) {
        val networkError = NetworkErrorHandler.handleException(e)
        if (isShowErrorInfo) {
            NotificationManager.showNotification(networkError.message)
        }
        Log.e("Note Request Error", networkError.message, e)
        onError()
    } finally {
        onFinally()
    }
}
