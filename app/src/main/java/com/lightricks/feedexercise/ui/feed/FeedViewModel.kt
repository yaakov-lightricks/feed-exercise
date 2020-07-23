package com.lightricks.feedexercise.ui.feed

import androidx.lifecycle.*
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.util.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * This view model manages the data for [FeedFragment].
 */
private const val TAG = "FeedViewModel"


open class FeedViewModel(private val feedRepository: FeedRepository) : ViewModel() {

    //holds all RxJava sources in one container
    private val disposable = CompositeDisposable()
    //holds current selected filter index
    private var selectedFilterIndex = 0
    private val isLoading = MutableLiveData<Boolean>(false)

    private val isEmpty = MutableLiveData<Boolean>()
    //capture current feed items
    private val internalItems = mutableListOf<FeedItem>()
    private val items = MediatorLiveData<List<FeedItem>>().apply {
        this.addSource(feedRepository.getFeedItems()) { items ->
            internalItems.let {
                it.clear()
                it.addAll(items)
            }
            updateItemsFeed(selectedFilterIndex)
        }
    }
    private val networkErrorEvent = MutableLiveData<Event<String>>()


    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getIsEmpty(): LiveData<Boolean> = isEmpty
    fun getFeedItems(): LiveData<List<FeedItem>> = items
    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    init {
        //todo add mechanism to avoid refetching data in short intervals
        refresh()
    }

    /**
     * initiate call to fetch items from [FeedRepository]
     */
    fun refresh() {
        isLoading.value = true
        disposable.add(
            feedRepository.refresh()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { isLoading.value = false },
                    { error ->
                        error?.message?.let {
                            networkErrorEvent.value = Event(it)
                        } ?: networkErrorEvent.let { it.value = Event("There was an error") }
                        isLoading.value = false
                    })
        )
    }

    /**
     * handle event of user filter selection
      */
    fun onFilterSelected(index: Int) {
        //index order according filter_array under res/values/string.xml
        //todo save selected filter to storage
        selectedFilterIndex = index
        updateItemsFeed(selectedFilterIndex)
    }

    /**
     * update current feed according current filter
     * source of true for updating current UI state
     */
    private fun updateItemsFeed(selectedFilter: Int){
        //todo use saeled class/enum
        when (selectedFilter) {
            //all items, no filter
            0 -> internalItems.let { items.value = it }
            //shuffle
            1 -> internalItems.let { items.value = it.shuffled() }
            //premium
            2 -> internalItems.let {
                items.value = it.filter { feedItem -> feedItem.isPremium }
            }
        }
        //update isEmpty
        isEmpty.postValue(items.value?.isEmpty() ?: false)

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
