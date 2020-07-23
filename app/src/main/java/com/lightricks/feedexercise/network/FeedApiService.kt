package com.lightricks.feedexercise.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


interface FeedApiService {
    @GET("feed.json")
    fun getFeed(): Single<FeedResponse>

    companion object {
        private const val BASE_URL = "https://assets.swishvideoapp.com/Android/demo/"
        val feedApiService: FeedApiService by lazy {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(FeedItemAdapter())
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            retrofit.create(FeedApiService::class.java)
        }
    }
}

