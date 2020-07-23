package com.lightricks.feedexercise.util

import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.FeedItemResponse

//taken from here: https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e
open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

fun List<FeedItemEntity>.toFeedItems(): List<FeedItem> { return map {
    FeedItem(it.id, it.thumbnailUrl, it.isPremium) }
}
fun List<FeedItemResponse>.toFeedEntities(): List<FeedItemEntity> { return map {
    FeedItemEntity(it.id, it.thumbnailUrl, it.isPremium) }
}