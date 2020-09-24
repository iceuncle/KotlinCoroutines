package com.tianyang.myapplication.arch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.tianyang.myapplication.Api
import com.tianyang.myapplication.model.Repo
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 界面描述：
 * <p>
 * Created by tianyang on 2020/9/24.
 */
class RengViewModel : ViewModel() {
    init {
        viewModelScope.launch {

        }
    }

    val repos = liveData {
        emit(loadUsers())
    }

    private suspend fun loadUsers(): List<Repo> {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        val api = retrofit.create(Api::class.java)
        return api.listReposKt("rengwuxian")
    }
}