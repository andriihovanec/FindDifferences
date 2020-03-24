package com.olbigames.finddifferencesgames.repository

import android.util.Log
import retrofit2.Response
import java.io.IOException

open class BaseRepository {
    suspend fun <T : Any> apiCall(call: suspend () -> Response<T>, errorMessage: String): T? {
        val result : Result<T> = apiResult(call, errorMessage)
        var data : T? = null
        when(result) {
            is Result.Success ->
                data = result.data
            is Result.Error ->
                Log.d("1.DataRepository", "$errorMessage & Exception - ${result.exception}")
        }
        return data
    }

    private suspend fun <T: Any> apiResult(call: suspend ()-> Response<T>, errorMessage: String) : Result<T> {
        val response = call.invoke()
        if(response.isSuccessful) return Result.Success(response.body()!!)
        return Result.Error(IOException("Error Occurred during getting safe Api result, Custom ERROR - $errorMessage"))
    }
}