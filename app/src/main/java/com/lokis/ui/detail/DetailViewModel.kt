package com.lokis.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lokis.api.RetrofitClient
import com.lokis.model.DataDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel: ViewModel() {
    val detail = MutableLiveData<DataDetail>()

    fun setLocationDetail() {
        RetrofitClient.instance
            .getLocationDetail()
            .enqueue(object : Callback<DataDetail> {
                override fun onResponse(
                    call: Call<DataDetail>,
                    response: Response<DataDetail>,
                ) {
                    if (response.isSuccessful) {
                        detail.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<DataDetail>, t: Throwable) {
                    Log.d("onFailure: ", t.message.toString())
                }
            })
    }

    fun getDetail(): LiveData<DataDetail> {
        return detail
    }
}