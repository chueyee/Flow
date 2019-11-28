package com.chchewy.flow

import com.chchewy.flow.models.PlannedEvent
import retrofit2.Call
import retrofit2.http.GET

interface JsonEventPostApi {

    @GET("events")
    fun getPosts(): Call<List<PlannedEvent>>
}