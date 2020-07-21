package com.lightricks.feedexercise.ui.feed

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.lightricks.feedexercise.BaseTest
import com.lightricks.feedexercise.blockingObserve
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.database.FeedItemEntity
import io.reactivex.Completable
import junit.framework.Assert.assertNotNull
import org.hamcrest.CoreMatchers.not
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
@MediumTest
class FeedViewModelTest : BaseTest() {

    private lateinit var mockRepo: FeedRepository

    @Before
    override fun setUp() {
        super.setUp()
        mockRepo = mock(FeedRepository::class.java)
    }

    @Test
    fun when_created_feed_is_empty() {
        `when`(mockRepo.getFeedItems()).thenReturn(MutableLiveData<List<FeedItemEntity>>(emptyList()))
        val fakeCompletable = Completable.fromCallable {}
        `when`(mockRepo.refresh()).thenReturn(fakeCompletable)
        val feedViewModel = FeedViewModel(mockRepo)
        fakeCompletable.test().assertComplete()
        val emptyLiveData = feedViewModel.getIsEmpty().blockingObserve()
        assertThat(emptyLiveData, `is`(true))
    }

    @Test
    fun when_created_loading_is_true() {
        `when`(mockRepo.getFeedItems()).thenReturn(MutableLiveData<List<FeedItemEntity>>(emptyList()))
        val fakeCompletable = Completable.fromCallable {
        }
        `when`(mockRepo.refresh()).thenReturn(fakeCompletable)
        val feedViewModel = FeedViewModel(mockRepo)
        val isLoading = feedViewModel.getIsLoading().blockingObserve()
        assertThat(isLoading, `is`(true))
    }

    @Test
    fun when_feed_is_not_empty_live_data_updated() {
        val element = FeedItemEntity("1", "", false)
        `when`(mockRepo.getFeedItems()).thenReturn(MutableLiveData<List<FeedItemEntity>>(listOf(
            element
        )))
        val fakeCompletable = Completable.fromCallable { }
        `when`(mockRepo.refresh()).thenReturn(fakeCompletable)
        val feedViewModel = FeedViewModel(mockRepo)
        val items = feedViewModel.getFeedItems().blockingObserve()
        assertThat(items, `is`(not(emptyList())))
        assertThat(items!![0].id, `is`("1"))
    }

    @Test
    fun when_error_thrown_error_is_emitted() {
        `when`(mockRepo.getFeedItems()).thenReturn(MutableLiveData<List<FeedItemEntity>>(emptyList()))
        val fakeCompletable = Completable.error { Throwable("test error!") }
        `when`(mockRepo.refresh()).thenReturn(fakeCompletable)
        fakeCompletable.test().assertErrorMessage("test error!")
        val feedViewModel = FeedViewModel(mockRepo)
        val error = feedViewModel.getNetworkErrorEvent().blockingObserve()
        assertNotNull(error)
        assertThat(error!!.getContentIfNotHandled(), `is`("test error!"))
    }
}