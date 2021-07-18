package com.omoemurao.android_vk_cup.news.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.FrameLayout
import com.omoemurao.android_vk_cup.news.DisplayUtility
import com.omoemurao.android_vk_cup.news.RxBus
import com.omoemurao.android_vk_cup.news.TopCardMovedEvent

import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription
import kotlin.math.abs


class NewsStackLayout : FrameLayout {

    companion object {
        private const val DURATION = 300
    }

    val publishSubject: PublishSubject<Int>? = PublishSubject.create()
    val publishSubjectAlpha: PublishSubject<Float>? = PublishSubject.create()
    private var compositeSubscription: CompositeSubscription? = null
    private var screenWidth = 0
    private var yMultiplier = 0
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) :
            super(context, attrs, defStyle)


    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        publishSubject?.onNext(childCount)
    }

    override fun removeView(view: View?) {
        super.removeView(view)
        publishSubject?.onNext(childCount)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        compositeSubscription?.unsubscribe()
    }


    init {
        clipChildren = false
        screenWidth = DisplayUtility.getScreenWidth(context)
        yMultiplier = DisplayUtility.dp2px(context, 8)
        compositeSubscription = CompositeSubscription()
        setUpRxBusSubscription()
    }

    private fun setUpRxBusSubscription() {
        val rxBusSubscription: Subscription = RxBus.toObserverable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Action1<Any> {
                override fun call(event: Any?) {
                    if (event == null) {
                        return
                    }
                    if (event is TopCardMovedEvent) {
                        val posX: Float = event.getPosX()
                        val childCount = childCount
                        for (i in childCount - 2 downTo 0) {
                            val newsCardView = getChildAt(i) as NewsCardView
                            if (abs(posX) == screenWidth.toFloat()) {
                                newsCardView.animate()
                                    .x(0f)
                                    .y(((childCount - 2 - i) * yMultiplier).toFloat())
                                    .rotation(0f)
                                    .setInterpolator(AnticipateOvershootInterpolator()).duration =
                                    DURATION.toLong()
                            }
                        }
                    }
                }
            })
        compositeSubscription?.add(rxBusSubscription)
    }

    fun addCard(tc: NewsCardView) {
        val layoutParams: ViewGroup.LayoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        val childCount = childCount
        addView(tc, 0, layoutParams)
        tc.animate()
            .x(0f)
            .y((childCount * yMultiplier).toFloat())
            .setInterpolator(AnticipateOvershootInterpolator()).duration = DURATION.toLong()
    }

    fun setAlphaBtn(alpha: Float){
        publishSubjectAlpha?.onNext(alpha)
    }
}