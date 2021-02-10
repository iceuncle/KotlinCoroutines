package com.tianyang.myapplication

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.tianyang.myapplication.arch.RengViewModel
import com.tianyang.myapplication.model.Repo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val disposable = CompositeDisposable()
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println("Thread name ${Thread.currentThread().name}")

        GlobalScope.launch {
            println("Thread name1 ${Thread.currentThread().name}")
        }

        lifecycleScope.launch {
            println("Thread name2 ${Thread.currentThread().name}")
        }

        lifecycleScope.launch(Dispatchers.Main) {
            ioCode1()
            uiCode1()
            ioCode2()
            uiCode2()
            ioCode3()
            uiCode3()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        val api = retrofit.create(Api::class.java)
//        api.listRepos("rengwuxian")
//            .enqueue(object : Callback<List<Repo>?> {
//                override fun onFailure(call: Call<List<Repo>?>, t: Throwable) {
//                }
//
//                override fun onResponse(call: Call<List<Repo>?>, response: Response<List<Repo>?>) {
//                    textView.text = response.body()?.get(0)?.name
//                }
//            })

        lifecycleScope.launch(Dispatchers.Main) {
            delay(1000)
            val repos = api.listReposKt("iceuncle")
            textView.text = repos[0].name + "Kt"
        }

//        api.listReposRx("iceuncle")
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : SingleObserver<List<Repo>?> {
//                override fun onSuccess(repos: List<Repo>?) {
//                    textView.text = repos?.get(0)?.name + "Rx"
//                }
//
//                override fun onSubscribe(d: Disposable?) {
//                    disposable.add(d)
//                }
//
//                override fun onError(e: Throwable?) {
//                }
//            })

//        Single.zip<List<Repo>, List<Repo>, String>(
//            api.listReposRx("rengwuxian"),
//            api.listReposRx("google"),
//            BiFunction { repos1, repos2 -> "${repos1[0].name} - ${repos2[0].name}" }
//        ).observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : SingleObserver<String> {
//                override fun onSuccess(combined: String) {
//                    textView.text = combined
//                }
//
//                override fun onSubscribe(d: Disposable) {
//
//                }
//
//                override fun onError(e: Throwable) {
//                    textView.text = e.message
//                }
//            })

        scope.launch(Dispatchers.Main) {
            val rengwuxian = async { api.listReposKt("rengwuxian") }
            val google = async { api.listReposKt("google") }
            textView.text = "${rengwuxian.await()[0].name} + ${google.await()[0].name}"
        }

        val model: RengViewModel by viewModels()
        model.repos.observe(this, Observer { repos ->
            textView.text = repos[0].name
        })

//        scope.launch {
//            coroutineScope {
//                launch {
//                    a()
//                }
//                launch {
//                    b()
//                }
//            }
//            c()
//        }
    }

    override fun onDestroy() {
        disposable.dispose()
        scope.cancel()
        super.onDestroy()
    }

    private suspend fun ioCode1() {
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
            println("Thread name io1 ${Thread.currentThread().name}")
        }
    }

    private suspend fun ioCode2() {
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
            println("Thread name io2 ${Thread.currentThread().name}")
        }
    }

    private suspend fun ioCode3() {
        withContext(Dispatchers.IO) {
            Thread.sleep(1000)
            println("Thread name io3 ${Thread.currentThread().name}")
        }
    }

    private fun uiCode1() {
        println("Thread name ui1 ${Thread.currentThread().name}")
    }

    private fun uiCode2() {
        println("Thread name ui2 ${Thread.currentThread().name}")
    }

    private fun uiCode3() {
        println("Thread name ui3 ${Thread.currentThread().name}")
    }
}