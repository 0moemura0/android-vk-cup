package com.omoemurao.android_vk_cup.news

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager


object DisplayUtility {

    fun dp2px(context: Context, dp: Int): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val displaymetrics = DisplayMetrics()
        display.getMetrics(displaymetrics)
        return (dp * displaymetrics.density + 0.5f).toInt()
    }

    fun getScreenWidth(context: Context): Int {
        val size = Point()
        (context as Activity).windowManager.defaultDisplay.getSize(size)
        return size.x
    }

}