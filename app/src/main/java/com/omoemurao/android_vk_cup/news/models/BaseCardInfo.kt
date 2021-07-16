package com.omoemurao.android_vk_cup.news.models

sealed class BaseCardInfo {

    var countLike: Int = 0
    var countComment: Int = 0

    data class BigImageCardInfo(
        val title: String? = null,
        val date: String,
        val creator: Creator,
        val image: String?,
    ) :
        BaseCardInfo()

    data class SmallImageCardInfo(
        val title: String?,
        val date: String,
        val creator: Creator,
        val image: String?,
    ) :
        BaseCardInfo()

    data class TextCardInfo(
        val title: String?,
        val date: String,
        val creator: Creator,
    ) :
        BaseCardInfo()
}