package com.omoemurao.android_vk_cup.news

class TopCardMovedEvent(private  val posX: Float = 0f) {

    fun getPosX(): Float {
        return posX
    }
  }