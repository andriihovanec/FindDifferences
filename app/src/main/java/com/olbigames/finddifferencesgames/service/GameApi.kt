package com.olbigames.finddifferencesgames.service

import com.olbigames.finddifferencesgames.db.diference.response.DifferencesResponse
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GameApi {

    @GET("/{gameLevel}.json")
    fun getAllGameDifferences(@Path("gameLevel") gameLevel: Int, @Query("auth") token: String): Deferred<Response<DifferencesResponse>>

    @GET("/{gameLevel}/differences/{differenceId}.json")
    fun getDifferenceById(@Path("gameLevel") gameLevel: Int, @Path("differenceId") differenceId: Int, @Query("auth") token: String): Deferred<Response<ResponseBody>>

    @POST("/.json")
    fun getAllGamesLevelsWithDifference(@Query("auth") token: String): Deferred<Response<ResponseBody>>
}