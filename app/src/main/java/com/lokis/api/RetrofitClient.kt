package com.lokis.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://lokis-app-tourism-ghggrfpakq-uc.a.run.app"

    val instance: ApiEndpoint by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiEndpoint::class.java)
    }
}