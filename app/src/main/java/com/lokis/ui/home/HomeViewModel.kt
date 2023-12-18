package com.lokis.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lokis.api.RetrofitClient
import com.lokis.model.DataTravel
import com.lokis.model.HomeModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    val listLocation = MutableLiveData<ArrayList<DataTravel>>()

    fun setLocationRekomendasi(){
        RetrofitClient.instance
            .getAllLocation()
            .enqueue(object : Callback<HomeModel> {
                override fun onResponse(call: Call<HomeModel>, response: Response<HomeModel>) {
                    if (response.isSuccessful) {
                        listLocation.postValue(response.body()?.data)
                    }
                }

                override fun onFailure(call: Call<HomeModel>, t: Throwable) {
                    Log.d("onFailure: ", t.message.toString())
                }

            })
    }

    fun getLocationRekomendasi(): LiveData<ArrayList<DataTravel>> {
        return  listLocation
    }
}