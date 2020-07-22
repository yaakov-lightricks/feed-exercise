package com.lightricks.feedexercise

import android.app.Application
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.data.FeedRepositoryImpl
import com.lightricks.feedexercise.database.AppDatabase
import com.lightricks.feedexercise.network.FeedApiService

class FeedApplication : Application() {

    private lateinit var appDatabase : AppDatabase
    private lateinit var feedApiService: FeedApiService
    lateinit var feedRepository: FeedRepository

    override fun onCreate() {
        super.onCreate()
        appDatabase = AppDatabase.getInstance(this)
        feedApiService = FeedApiService.feedApiService
        feedRepository = FeedRepositoryImpl(feedApiService, appDatabase.feedDao())
    }
}

fun Application.getFeedRepository(): FeedRepository = (this as FeedApplication).feedRepository