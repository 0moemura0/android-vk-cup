package com.omoemurao.android_vk_cup.news.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.omoemurao.android_vk_cup.R


class DynamicHeightImageView : AppCompatImageView {

    var heightRatio = 0.0
    set(value) {
        if (value != field) {
            field = value
            requestLayout()
        }
    }

    constructor(context: Context) :
            super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
            super(context, attrs, defStyle) {
        init(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (heightRatio > 0.0) {
            // set the image views size
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (width * heightRatio).toInt()
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (!isInEditMode) {
            val attributeArray: TypedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.DynamicHeightImageView)
            try {
                val heightRatio =
                    attributeArray.getFloat(R.styleable.DynamicHeightImageView_heightRatio, 1.0f)
                this.heightRatio = heightRatio.toDouble()
            } finally {
                attributeArray.recycle()
            }
        }
    }
}