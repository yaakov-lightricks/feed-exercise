package com.lightricks.feedexercise.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lightricks.feedexercise.database.FeedDao
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.util.toFeedEntities
import com.lightricks.feedexercise.util.toFeedItems
import io.reactivex.Completable

/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
interface FeedRepository {

    fun refresh() : Completable

    fun getFeedItems() : LiveData<List<FeedItem>>
}


class FeedRepositoryImpl(private val feedApiService: FeedApiService, private val feedDao: FeedDao): FeedRepository {

    override fun refresh() = feedApiService.getFeed().flatMapCompletable { response ->
        feedDao.insertAll(response.templatesMetadata.toFeedEntities())
    }

    override fun getFeedItems() = Transformations.map(feedDao.getAll()) {
        it.toFeedItems()
    }
}
