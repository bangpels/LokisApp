package com.lokis.api

import com.lokis.model.DataTravel
import com.lokis.model.HomeModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiEndpoint {

    @GET("/location")
    fun getAllLocation(): Call<List<DataTravel>>

    @GET("/location/search/name")
    fun getSearchLocationbyName(
        @Query("name") query: String
    ): Call<HomeModel>
}