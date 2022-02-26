package com.example.credassignment.data.repo

import com.example.credassignment.data.api.Client

object ApiRepository {
    suspend fun getSuccess()= Client.api.getSuccess()
    suspend fun getFailure()= Client.api.getFailure()
}