package com.lightricks.feedexercise.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class FeedItemEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "thumbnail_uri")
    val thumbnailUrl: String,
    @ColumnInfo(name = "is_premium")
    val isPremium: Boolean
)