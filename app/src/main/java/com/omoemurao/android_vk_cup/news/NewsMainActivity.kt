package com.omoemurao.android_vk_cup.news

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.omoemurao.android_vk_cup.R
import com.omoemurao.android_vk_cup.news.models.BaseCardInfo
import com.omoemurao.android_vk_cup.news.models.Creator
import com.omoemurao.android_vk_cup.news.view.NewsCardView
import com.omoemurao.android_vk_cup.news.view.NewsStackLayout
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.sdk.api.groups.dto.GroupsGroupFull
import com.vk.sdk.api.newsfeed.dto.NewsfeedGetRecommendedResponse
import com.vk.sdk.api.newsfeed.dto.NewsfeedNewsfeedItem
import com.vk.sdk.api.users.dto.UsersUserFull
import com.vk.sdk.api.wall.dto.WallWallpostAttachmentType
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import java.util.*


class NewsMainActivity : AppCompatActivity() {

    companion object {
        private const val STACK_SIZE = 4
        const val TAG = "NewsMainActivity"
    }

    private var newsStackLayout: NewsStackLayout? = null

    private var index = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: NewsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_news)

        VK.login(this, arrayListOf(VKScope.WALL, VKScope.FRIENDS))

        newsStackLayout = findViewById(R.id.tsl)

        viewModel.getNewsfeedGetRecommendedResponse {
            Log.d(TAG, " size ${it.items?.size}")
            var tc: NewsCardView
            val i = index
            while (index < i + STACK_SIZE) {
                tc = NewsCardView(this)
                tc.bind(getPost(it, index))
                newsStackLayout!!.addCard(tc)
                index++
            }
        }


        newsStackLayout!!.getPublishSubject()
            ?.observeOn(AndroidSchedulers.mainThread()) // UI Thread
            ?.subscribe(object : Subscriber<Int?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable?) {}
                override fun onNext(t: Int?) {
                    if (t == 1) {
                        viewModel.getNewsfeedGetRecommendedResponse {
                            var newsCardView: NewsCardView
                            val thisIndex = index
                            while (index < thisIndex + (STACK_SIZE - 1)) {
                                newsCardView = NewsCardView(this@NewsMainActivity)
                                newsCardView.bind(getPost(it, index))
                                newsStackLayout!!.addCard(newsCardView)
                                index++
                            }
                        }

                    }
                }
            })

    }

    private fun getPost(response: NewsfeedGetRecommendedResponse, index: Int): BaseCardInfo? {
        val post = response.items?.get(index)
        var type = ""
        var profile: UsersUserFull? = null
        var group: GroupsGroupFull? = null
        var creator: Creator? = null
        when (post) {
            is NewsfeedNewsfeedItem.NewsfeedItemWallpost -> {
                Log.d(TAG, "NewsfeedItemWallpost ")
                type = "post"
                if (post.sourceId!! > 0) {
                    profile = response.profiles!![index]
                    creator = Creator.User(profile.id,
                        profile.firstName,
                        profile.lastName,
                        profile.photo,
                        profile.photoMediumRec,
                        profile.screenName)
                } else {
                    group = response.groups!![index]
                    creator = Creator.Group(group.id,
                        group.name,
                        group.isClosed,
                        group.photo50,
                        group.photo100,
                        group.photo200,
                        group.screenName)
                }
                post.attachments?.forEach {
                    if (it.type == WallWallpostAttachmentType.PHOTO) {
                        val res = BaseCardInfo.SmallImageCardInfo(post.text,
                            getDate(post.date),
                            creator!!,
                            it.photo?.images?.get(0)?.url)
                        res.countLike = post.likes?.count ?: 0
                        res.countComment = post.comments?.count ?: 0
                        return res
                    }
                    val res = BaseCardInfo.TextCardInfo(
                        post.text,
                        getDate(post.date),
                        creator!!)
                    res.countLike = post.likes?.count ?: 0
                    res.countComment = post.comments?.count ?: 0
                    return  res
                }
            }
            is NewsfeedNewsfeedItem.NewsfeedItemPhoto -> {
                Log.d(TAG, "NewsfeedItemPhoto ")
                type = "photo"
                if (post.sourceId!! > 0) {
                    profile = response.profiles!![index]
                    creator = Creator.User(profile.id,
                        profile.firstName,
                        profile.lastName,
                        profile.photo,
                        profile.photoMediumRec,
                        profile.screenName)
                } else {
                    group = response.groups!![index]
                    creator = Creator.Group(group.id,
                        group.name,
                        group.isClosed,
                        group.photo50,
                        group.photo100,
                        group.photo200,
                        group.screenName)
                }
                return BaseCardInfo.BigImageCardInfo(
                    type,
                    getDate(post.date),
                    creator,
                    post.photos?.items?.get(0)?.images?.get(0)?.url)
            }

/*           is NewsfeedNewsfeedItem.NewsfeedItemPhotoTag -> {
               type ="photo_tag"
               isProfile = post.sourceId!! > 0
           }
           iNewsfeedNewsfeedItem.NewsfeedItemFriend -> {
               type ="friend"
               isProfile = post.sourceId!! > 0
           }
           is NewsfeedNewsfeedItem.NewsfeedItemTopic -> {
               type ="topic"
               isProfile = post.sourceId!! > 0
           }
           is NewsfeedNewsfeedItem.NewsfeedItemDigest -> {
               type ="digest"
               isProfile = post.sourceId!! > 0
           }
           is NewsfeedNewsfeedItem.NewsfeedItemPromoButton -> {
               type ="promo_button"
               isProfile = post.sourceId!! > 0
           }
           is NewsfeedNewsfeedItem.NewsfeedItemFeedbackPoll -> {
               type ="feedback_poll"
               isProfile = post.sourceId!! > 0
           }
           is NewsfeedNewsfeedItem.NewsfeedItemAnimatedBlock -> {
               type ="animated_block"
               isProfile = post.sourceId!! > 0
           }
           is NewsfeedNewsfeedItem.NewsfeedItemRecommendedGroupsBlock -> {
               type ="recommended_groups"
               isProfile = post.sourceId!! > 0
           }
           is NewsfeedNewsfeedItem.NewsfeedItemRecognizeBlock -> {
               type ="tags_suggestions"
               isProfile = post.sourceId!! > 0
           }
           is NewsfeedNewsfeedItem.NewsfeedItemGamesCarousel -> {
               type ="games_carousel"
               isProfile = post.sourceId!! > 0
           }
           is NewsfeedNewsfeedItem.NewsfeedItemTextliveBlock -> {
               type ="textlive"
               isProfile = post.sourceId!! > 0
           }*/
            else -> return null
        }
        return null
    }

    private fun getDate(date: Int?): String {

        return if (date != null) {
            val res = Date(date.toLong() * 1000)
            val del = Date().time - date.toLong() * 1000
            when {
                del <= 3_600_000 -> {
                    "Менее часа назад"
                }
                del <= 7_200_000 -> {
                    "один час назад"
                }
                del <= 10_800_000 -> {
                    "два часа назад"
                }
                del <= 14_400_000 -> {
                    "три часа назад"
                }
                else -> "сегодня в ${res.hours} : ${res.minutes} "
            }
        } else{
            "только что"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {

            }

            override fun onLoginFailed(errorCode: Int) {
            }
        }
        if (!VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}