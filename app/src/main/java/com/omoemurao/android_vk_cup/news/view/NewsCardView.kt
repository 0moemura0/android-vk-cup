package com.omoemurao.android_vk_cup.news.view

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.omoemurao.android_vk_cup.R
import com.omoemurao.android_vk_cup.news.DisplayUtility
import com.omoemurao.android_vk_cup.news.RxBus
import com.omoemurao.android_vk_cup.news.TopCardMovedEvent
import com.omoemurao.android_vk_cup.news.models.BaseCardInfo
import com.squareup.picasso.Picasso


class NewsCardView : FrameLayout, View.OnTouchListener {
    companion object {
        private const val CARD_ROTATION_DEGREES = 40.0f
        private const val BADGE_ROTATION_DEGREES = 15.0f
        private const val DURATION = 300

    }

    private var oldX = 0f
    private var oldY = 0f
    private var newX = 0f
    private var newY = 0f
    private var dX = 0f
    private var dY = 0f
    private var rightBoundary = 0f
    private var leftBoundary = 0f
    private var screenWidth = 0
    private var padding = 0


    constructor(context: Context) :
            super(context)

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
            super(context, attrs, defStyle)

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val newsStackLayout: NewsStackLayout = view.parent as NewsStackLayout
        val topCard =
            newsStackLayout.getChildAt(newsStackLayout.childCount - 1) as NewsCardView
        return if (topCard == view) {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    oldX = motionEvent.x
                    oldY = motionEvent.y
                    view.clearAnimation()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    when {
                        isCardBeyondLeftBoundary(view) -> {
                            RxBus.send(TopCardMovedEvent((-screenWidth).toFloat()))
                            dismissCard(view, -(screenWidth * 2))
                        }
                        isCardBeyondRightBoundary(view) -> {
                            RxBus.send(TopCardMovedEvent(screenWidth.toFloat()))
                            dismissCard(view, screenWidth * 2)
                        }
                        else -> {
                            RxBus.send(TopCardMovedEvent(0f))
                            resetCard(view)
                        }
                    }
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    newX = motionEvent.x
                    newY = motionEvent.y
                    dX = newX - oldX
                    dY = newY - oldY
                    val posX = view.getX() + dX
                    RxBus.send(TopCardMovedEvent(posX))

                    // Set new position
                    view.setX(view.getX() + dX)
                    view.setY(view.getY() + dY)
                    setCardRotation(view, view.getX())
                    updateAlphaOfBadges(posX)
                    true
                }
                else -> super.onTouchEvent(motionEvent)
            }
        } else super.onTouchEvent(motionEvent)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnTouchListener(null)
    }

    // Check if card's middle is beyond the left boundary
    private fun isCardBeyondLeftBoundary(view: View): Boolean {
        return view.x + view.width / 2 < leftBoundary
    }

    // Check if card's middle is beyond the right boundary
    private fun isCardBeyondRightBoundary(view: View): Boolean {
        return view.x + view.width / 2 > rightBoundary
    }

    private fun dismissCard(view: View, xPos: Int) {
        view.animate()
            .x(xPos.toFloat())
            .y(0f)
            .setInterpolator(AccelerateInterpolator())
            .setDuration(DURATION.toLong())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    val viewGroup = view.parent as ViewGroup
                    viewGroup.removeView(view)
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
    }

    private fun resetCard(view: View) {
        view.animate()
            .x(0f)
            .y(0f)
            .rotation(0f)
            .setInterpolator(OvershootInterpolator()).duration = DURATION.toLong()
        //likeTextView!!.alpha = 0f
        //nopeTextView!!.alpha = 0f
    }

    private fun setCardRotation(view: View, posX: Float) {
        val rotation = CARD_ROTATION_DEGREES * posX / screenWidth
        val halfCardHeight = view.height / 2
        if (oldY < halfCardHeight - 2 * padding) {
            view.rotation = rotation
        } else {
            view.rotation = -rotation
        }
    }

    // set alpha of like and nope badges
    private fun updateAlphaOfBadges(posX: Float) {
        val alpha = (posX - padding) / (screenWidth * 0.50f)
        //TODO вот тут листенер для увеличения кнопок
        //likeTextView!!.alpha = alpha
        //nopeTextView!!.alpha = -alpha
    }

    fun bind(news: BaseCardInfo?) {
        if (news == null) return

        when (news) {
            is BaseCardInfo.TextCardInfo -> bindTextCard(news)
            is BaseCardInfo.SmallImageCardInfo -> bindSmallImage(news)
            is BaseCardInfo.BigImageCardInfo -> bindBigImage(news)
        }
    }

    private fun bindBigImage(card: BaseCardInfo.BigImageCardInfo) {
        inflate(context, R.layout.card_image_bg, this)
        val imageCreator: ImageView? = findViewById(R.id.iv_creator)
        val nameCreator: TextView? = findViewById(R.id.tw_creator)
        val timeCreator: TextView? = findViewById(R.id.tw_time)

        val bigImage: ImageView? = findViewById(R.id.iv_post)

        val textPost: TextView? = findViewById(R.id.tw_text)
        val countLike: TextView? = findViewById(R.id.tw_count_like)
        val countComment: TextView? = findViewById(R.id.tw_count_comment)

        Picasso.get().load(card.creator.getUrl()).into(imageCreator)
        Picasso.get().load(card.image).into(bigImage)

        nameCreator?.text = card.creator.getName().toString()

        timeCreator?.text = card.date.toString()

        textPost?.text = card.title.toString()
        countLike?.text = card.countLike.toString()
        countComment?.text = card.countComment.toString()

        //likeTextView = findViewById<View>(R.id.like_tv) as TextView
        //nopeTextView = findViewById<View>(R.id.nope_tv) as TextView
        //likeTextView!!.rotation = -BADGE_ROTATION_DEGREES
        //nopeTextView!!.rotation = BADGE_ROTATION_DEGREES
        screenWidth = DisplayUtility.getScreenWidth(context)
        leftBoundary = screenWidth * (1.0f / 6.0f) // Left 1/6 of screen
        rightBoundary = screenWidth * (5.0f / 6.0f) // Right 1/6 of screen
        padding = DisplayUtility.dp2px(context, 16)
        setOnTouchListener(this)

    }

    private fun bindSmallImage(card: BaseCardInfo.SmallImageCardInfo) {
        inflate(context, R.layout.card_image_sml, this)
        val imageCreator: ImageView? = findViewById(R.id.iv_creator)
        val nameCreator: TextView? = findViewById(R.id.tw_creator)
        val timeCreator: TextView? = findViewById(R.id.tw_time)

        val bigImage: ImageView? = findViewById(R.id.iv_post)

        val textPost: TextView? = findViewById(R.id.tw_text)
        val countLike: TextView? = findViewById(R.id.tw_count_like)
        val countComment: TextView? = findViewById(R.id.tw_count_comment)

        Picasso.get().load(card.creator.getUrl()).into(imageCreator)
        Picasso.get().load(card.image).into(bigImage)

        nameCreator?.text = card.creator.getName().toString()

        timeCreator?.text = card.date.toString()

        textPost?.text = card.title.toString()
        countLike?.text = card.countLike.toString()
        countComment?.text = card.countComment.toString()

        //likeTextView = findViewById<View>(R.id.like_tv) as TextView
        //nopeTextView = findViewById<View>(R.id.nope_tv) as TextView
        //likeTextView!!.rotation = -BADGE_ROTATION_DEGREES
        //nopeTextView!!.rotation = BADGE_ROTATION_DEGREES
        screenWidth = DisplayUtility.getScreenWidth(context)
        leftBoundary = screenWidth * (1.0f / 6.0f) // Left 1/6 of screen
        rightBoundary = screenWidth * (5.0f / 6.0f) // Right 1/6 of screen
        padding = DisplayUtility.dp2px(context, 16)
        setOnTouchListener(this)
    }

    private fun bindTextCard(card: BaseCardInfo.TextCardInfo) {
        inflate(context, R.layout.card_text, this)
        val imageCreator: ImageView? = findViewById(R.id.iv_creator)
        val nameCreator: TextView? = findViewById(R.id.tw_creator)
        val timeCreator: TextView? = findViewById(R.id.tw_time)

        val textPost: TextView? = findViewById(R.id.tw_text)
        val countLike: TextView? = findViewById(R.id.tw_count_like)
        val countComment: TextView? = findViewById(R.id.tw_count_comment)

        Picasso.get().load(card.creator.getUrl()).into(imageCreator)
        nameCreator?.text = card.creator.getName().toString()

        timeCreator?.text = card.date.toString()

        textPost?.text = card.title.toString()
        countLike?.text = card.countLike.toString()
        countComment?.text = card.countComment.toString()

        //likeTextView = findViewById<View>(R.id.like_tv) as TextView
        //nopeTextView = findViewById<View>(R.id.nope_tv) as TextView
        //likeTextView!!.rotation = -BADGE_ROTATION_DEGREES
        //nopeTextView!!.rotation = BADGE_ROTATION_DEGREES
        screenWidth = DisplayUtility.getScreenWidth(context)
        leftBoundary = screenWidth * (1.0f / 6.0f) // Left 1/6 of screen
        rightBoundary = screenWidth * (5.0f / 6.0f) // Right 1/6 of screen
        padding = DisplayUtility.dp2px(context, 16)
        setOnTouchListener(this)
    }

}