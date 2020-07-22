package com.lightricks.feedexercise.network

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single


class MockFeedApiService(private val context: Context) : FeedApiService {

    override fun getFeed(): Single<FeedResponse> {
        val file = "get_feed_response.json"
        val json = context.assets.open(file).bufferedReader().use { it.readText() }
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(FeedItemAdapter())
            .build()
        val jsonAdapter: JsonAdapter<FeedResponse> =
            moshi.adapter<FeedResponse>(FeedResponse::class.java)
        return Single.just(jsonAdapter.fromJson(json))
    }
}