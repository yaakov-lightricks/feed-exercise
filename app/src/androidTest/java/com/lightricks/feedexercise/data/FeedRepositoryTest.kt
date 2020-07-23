package com.lightricks.feedexercise.data

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.lightricks.feedexercise.BaseTest
import com.lightricks.feedexercise.blockingObserve
import com.lightricks.feedexercise.network.MockFeedApiService
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class FeedRepositoryTest: BaseTest() {


    @Test
    fun test_when_refresh_call_feed_have_values() {
        //create the mock server service
        val mockFeedApiService = MockFeedApiService(getApplicationContext())
        val repository = FeedRepositoryImpl(mockFeedApiService, db.feedDao())
        //when calling refresh
        repository.refresh()
            .test()
            .assertComplete()
        //then the data is saved to the db and we can fetch it back
        val items = repository.getFeedItems().blockingObserve()
        Assert.assertThat(items, `is`(not(emptyList())))
        Assert.assertThat(items!!.size, `is`(10))
    }
}
