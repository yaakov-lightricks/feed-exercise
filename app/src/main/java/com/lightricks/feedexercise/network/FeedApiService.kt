package com.lightricks.feedexercise.network

import com.lightricks.feedexercise.data.FeedItemAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


interface FeedApiService {
    @GET("feed.json")
    suspend fun getFeed(): DataTransferObjects

    companion object {
        private const val BASE_URL = "https://assets.swishvideoapp.com/Android/demo/"

        fun getFeedApiService(): FeedApiService {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .add(FeedItemAdapter())
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            return retrofit.create(FeedApiService::class.java)
        }
    }
}