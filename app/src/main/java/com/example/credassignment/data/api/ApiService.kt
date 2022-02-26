package com.example.credassignment.data.api

import com.example.credassignment.data.models.DataClass
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("p68348/success_case")
    suspend fun getSuccess(): Response<DataClass>
    @GET("p68348/failure_case")
    suspend fun getFailure(): Response<DataClass>
}