package com.example.credassignment.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.credassignment.data.models.DataClass
import com.example.credassignment.data.repo.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivityViewModel : ViewModel() {
    val result = MutableLiveData<DataClass>()
    fun fetchSuccessResult() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = withContext(Dispatchers.IO) {
                ApiRepository.getSuccess()
            }
            Log.d("MainActivityViewModel", "fetchFailureResult: $response")
            if (response.isSuccessful) {
                response.body()?.let {
                    result.postValue(it)
                }
            }
        }
    }
    fun fetchFailureResult() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = withContext(Dispatchers.IO) {
                ApiRepository.getFailure()
            }
            response.errorBody()?.let{
                result.postValue(DataClass(JSONObject(it.charStream().readText()).getBoolean("success")))
            }
        }
    }
}