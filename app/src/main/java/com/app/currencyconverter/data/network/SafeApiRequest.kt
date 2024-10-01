package com.app.currencyconverter.data.network

import com.app.currencyconverter.utils.ApiException
import com.app.currencyconverter.utils.ResultWrapper
import com.app.currencyconverter.utils.UnAuthorizedException
import com.app.currencyconverter.utils.localizedException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class SafeApiRequest {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): ResultWrapper<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful)
                ResultWrapper.Success(response.body()!!)
            else
                ResultWrapper.Error(response.getApiException().localizedException)
        } catch (e: Exception) {
            ResultWrapper.Error(e.localizedException)
        }
    }

    @Throws(Exception::class)
    suspend fun <T : Any> apiRequestWithException(call: suspend () -> Response<T>): T {
        return try {
            val response = call.invoke()
            if (response.isSuccessful)
                response.body()!!
            else
                throw response.getApiException()
        } catch (e: Exception) {
            throw e.localizedException
        }
    }

    /**
     * This function wrap your request response in Result class whether is it a response body or
     * And Exception
     */
    suspend fun <T : Any> wrapResponseAndExceptionWithResult(call: suspend () -> Response<T>): com.app.currencyconverter.utils.ResultWrapper<T> =
        suspendCoroutine { suspend ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = call.invoke()
                    suspend.resume(
                        if (response.isSuccessful) {
                            ResultWrapper.Success(response.body()!!)
                        } else
                            ResultWrapper.Error(response.getApiException().localizedException)
                    )
                } catch (e: Exception) {
                    suspend.resume(ResultWrapper.Error(e.localizedException))
                }
            }
        }

    @Throws(Exception::class)
    fun <T : Any> Response<T>.getApiException(): Exception {
        val error = errorBody()?.string()
        val errorObj = JSONObject(error ?: EMPTY_OBJECT)
        if (errorObj.getInt(CODE) == 401 || errorObj.getInt(CODE) == 403) {
            return UnAuthorizedException()
        }
        return ApiException(errorObj.getJSONObject(ERROR).getJSONArray(MESSAGE).getString(0))
    }

    companion object {
        private const val EMPTY_OBJECT = "{}"
        private const val CODE = "code"
        private const val ERROR = "error"
        private const val MESSAGE = "message"
    }
}
