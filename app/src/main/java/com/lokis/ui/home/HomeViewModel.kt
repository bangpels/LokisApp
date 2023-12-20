package com.lokis.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lokis.api.RetrofitClient
import com.lokis.model.DataTravel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    val listLocation = MutableLiveData<ArrayList<DataTravel>>()

    fun setLocationRekomendasi(){
        RetrofitClient.instance
            .getAllLocation()
            .enqueue(object : Callback<List<DataTravel>> {

                override fun onResponse(
                    call: Call<List<DataTravel>>,
                    response: Response<List<DataTravel>>,
                ) {
                    if (response.isSuccessful) {
                        listLocation.postValue((response.body() ?: emptyList()) as ArrayList<DataTravel>?)
                    }
                }

                override fun onFailure(call: Call<List<DataTravel>>, t: Throwable) {
                    Log.d("onFailure: ", t.message.toString())
                }

            })
    }

    fun getLocationRekomendasi(): LiveData<ArrayList<DataTravel>> {
        return  listLocation
    }
}