package com.lightricks.feedexercise.data

import androidx.lifecycle.LiveData
import com.lightricks.feedexercise.database.FeedDao
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.FeedApiService
import io.reactivex.Completable

/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
interface FeedRepository {

    fun refresh() : Completable

    fun getFeedItems() : LiveData<List<FeedItemEntity>>
}


class RealFeedRepository(private val feedApiService: FeedApiService, private val feedDao: FeedDao): FeedRepository {

    override fun refresh() = feedApiService.getFeed().flatMapCompletable { response ->
        feedDao.insertAll(response.templatesMetadata.toFeedEntities())
    }

    override fun getFeedItems() = feedDao.getAll()
}

fun List<FeedItemEntity>.toFeedItems(): List<FeedItem> { return map {
    FeedItem(it.id, it.thumbnailUrl, it.isPremium) }
}
fun List<FeedItem>.toFeedEntities(): List<FeedItemEntity> { return map {
    FeedItemEntity(it.id, it.thumbnailUrl, it.isPremium) }
}