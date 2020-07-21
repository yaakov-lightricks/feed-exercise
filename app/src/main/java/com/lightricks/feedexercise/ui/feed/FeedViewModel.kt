package com.lightricks.feedexercise.ui.feed

import androidx.lifecycle.*
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.data.Result
import com.lightricks.feedexercise.data.toFeedItems
import com.lightricks.feedexercise.util.Event
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

/**
 * This view model manages the data for [FeedFragment].
 */
open class FeedViewModel(private val feedRepository: FeedRepository) : ViewModel() {
    private val isLoading = MutableLiveData<Boolean>()
    private val isEmpty = MediatorLiveData<Boolean>().apply {
        this.addSource(feedRepository.getFeedItems()) { items ->
            this.postValue(items.isEmpty())
        }
    }
    private val feedItems = Transformations.map(feedRepository.getFeedItems()) {
        it.toFeedItems()
    }

    private val networkErrorEvent = MutableLiveData<Event<String>>()

    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getIsEmpty(): LiveData<Boolean> = isEmpty
    fun getFeedItems(): LiveData<List<FeedItem>> = feedItems
    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    init {
        refresh()
    }

    fun refresh() {
        isLoading.value = true
        // launch a coroutine in viewModelScope
        viewModelScope.launch  {
            val result = feedRepository.refresh()
            isLoading.value = false
            if (result is Result.Error){
                result.exception.message?.let { networkErrorEvent.value = Event(it) }
            }
        }
    }
}

/**
 * This class creates instances of [FeedViewModel].
 * It's not necessary to use this factory at this stage. But if we will need to inject
 * dependencies into [FeedViewModel] in the future, then this is the place to do it.
 */
class FeedViewModelFactory(private val feedRepository: FeedRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            throw IllegalArgumentException("factory used with a wrong class")
        }
        @Suppress("UNCHECKED_CAST")
        return FeedViewModel(feedRepository) as T
    }
}