package com.lightricks.feedexercise.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.lightricks.feedexercise.BaseTest
import com.lightricks.feedexercise.blockingObserve
import com.lightricks.feedexercise.database.AppDatabase
import com.lightricks.feedexercise.database.FeedItemEntity
import com.lightricks.feedexercise.network.MockFeedApiService
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@MediumTest
class FeedRepositoryTest: BaseTest() {


    @Test
    fun test_when_refresh_call_feed_has_values() {
        //create the mock server service
        val mockFeedApiService = MockFeedApiService(getApplicationContext())
        val repository = RealFeedRepository(mockFeedApiService, db.feedDao())
        //when calling refresh
        repository.refresh()
            .test()
            .assertComplete()
        //then the data is saved to the db and we can fetch it back
        val items = repository.getFeedItems().blockingObserve()
        Assert.assertThat(items, `is`(not(emptyList())))
    }
}
