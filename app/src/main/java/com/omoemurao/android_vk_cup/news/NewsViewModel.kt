package com.omoemurao.android_vk_cup.news

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vk.api.sdk.VK
import com.vk.sdk.api.newsfeed.NewsfeedService
import com.vk.sdk.api.newsfeed.dto.NewsfeedGetRecommendedResponse
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class NewsViewModel : ViewModel() {
    private val nextStart = MutableLiveData("")
    fun getNewsfeedGetRecommendedResponse(
        list: (NewsfeedGetRecommendedResponse) -> Unit,
    ) {
        Observable.fromCallable {
            VK.executeSync(NewsfeedService().newsfeedGetRecommended(count = 10))
        }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                nextStart.postValue(it.nextFrom!!)
                list(it)
            }, {
                Log.d("NewsViewModel", it.cause.toString())
                Log.d("NewsViewModel", it.message.toString())
                Log.d("NewsViewModel", it.stackTrace.toString())
            })
    }
}