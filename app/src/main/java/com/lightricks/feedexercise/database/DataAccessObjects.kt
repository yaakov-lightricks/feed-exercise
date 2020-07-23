package com.lightricks.feedexercise.database

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Completable

@Dao
interface FeedDao {
    @Query("SELECT * FROM items")
    fun getAll(): LiveData<List<FeedItemEntity>>

    @Query("SELECT COUNT(id) FROM items")
    fun getCount(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<FeedItemEntity>): Completable

    @Query("DELETE FROM items")
    fun deleteAll(): Completable
}