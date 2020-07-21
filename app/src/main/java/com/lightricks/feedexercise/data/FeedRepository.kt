package com.lightricks.feedexercise.data


import androidx.lifecycle.LiveData
import com.lightricks.feedexercise.database.FeedDao
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.FeedApiService

/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
interface FeedRepository {

    suspend fun refresh(): Result<Boolean>

    fun getFeedItems() : LiveData<List<FeedItemEntity>>
}

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

class RealFeedRepository(private val feedApiService: FeedApiService, private val feedDao: FeedDao): FeedRepository {

    override suspend fun refresh(): Result<Boolean> {
        try {
            val items = feedApiService.getFeed().templatesMetadata
            feedDao.insertAll(items.toFeedEntities())
            return Result.Success(true)
        } catch (e: Exception) {
            return Result.Error(Exception("Network request failed"))
        }
    }

    override fun getFeedItems(): LiveData<List<FeedItemEntity>> {
        return feedDao.getAll()
    }

}


fun List<FeedItemEntity>.toFeedItems(): List<FeedItem> { return map {
    FeedItem(it.id, it.thumbnailUrl, it.isPremium) }
}
fun List<FeedItem>.toFeedEntities(): List<FeedItemEntity> { return map {
    FeedItemEntity(it.id, it.thumbnailUrl, it.isPremium) }
}