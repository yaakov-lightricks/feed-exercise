package com.lightricks.feedexercise.ui.feed

import android.util.Log
import androidx.lifecycle.*
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.data.toFeedItems
import com.lightricks.feedexercise.util.Event
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalArgumentException

/**
 * This view model manages the data for [FeedFragment].
 */
private const val TAG = "FeedViewModel"


open class FeedViewModel(private val feedRepository: FeedRepository) : ViewModel() {


    private val disposable = CompositeDisposable()

    private val isLoading = MutableLiveData<Boolean>(false)
    private val isEmpty = MediatorLiveData<Boolean>().apply {
        this.addSource(feedRepository.getFeedItems()) { items ->
            this.postValue(items.isEmpty())
        }
    }
    private val items = MediatorLiveData<List<FeedItem>>().apply {
        this.addSource(feedRepository.getFeedItems()) { items ->
            this.postValue(items.toFeedItems())
        }
    }

//
//    private val feedItems =
//        Transformations.map(feedRepository.getFeedItems()) {
//            it.toFeedItems()
//        }

    private val networkErrorEvent = MutableLiveData<Event<String>>()

    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getIsEmpty(): LiveData<Boolean> = isEmpty
    fun getFeedItems(): LiveData<List<FeedItem>> = items

    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    init {
        refresh()
    }

    fun refresh() {
        Log.d(TAG, "refresh: called")
        isLoading.value = true
        disposable.add(
            feedRepository.refresh().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    {
                        isLoading.postValue(false)
                    },
                    { error ->
                        error?.message?.let {
                            networkErrorEvent.postValue(Event(it))
                        }
                        isLoading.postValue(false)
                    })
        )
    }

    /**
     * Shuffle list
     * @return a [Completable] that completes when the list is shuffled
     */
    fun shuffleDisplay() {
        items.value?.let {
            items.value = it.shuffled()
        }

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
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