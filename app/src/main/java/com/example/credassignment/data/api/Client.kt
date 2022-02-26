package com.example.credassignment.data.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Client {
    private val gson= GsonBuilder()
        .create()
    private val retrofit= Retrofit.Builder()
        .baseUrl("https://api.mocklets.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val api: ApiService = retrofit.create(ApiService::class.java)
}