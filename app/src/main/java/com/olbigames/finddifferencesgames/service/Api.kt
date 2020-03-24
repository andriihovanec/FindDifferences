package com.olbigames.finddifferencesgames.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.olbigames.finddifferencesgames.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Api {

    private val client = OkHttpClient().newBuilder()
        .connectTimeout(5,TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .build()

    private fun retrofit() : Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(Constants.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val games : GameApi = retrofit().create(GameApi::class.java)
}