package com.omoemurao.android_vk_cup.news.models

import com.vk.sdk.api.groups.dto.GroupsGroupIsClosed

sealed class Creator() {
    fun getUrl(): String?{
        return when (this){
            is Creator.Group -> photo
            is Creator.User -> photo
        }
    }
    fun getName(): String?{
        return when (this){
            is Creator.Group -> groupName
            is Creator.User -> "$first_name $last_name"
        }
    }
    data class User(
        val id: Int?,
        val first_name: String?,
        val last_name: String?,
        val photo: String?,
        val photo_medium_rec: String?,
        val screen_name: String?,
    ) : Creator()
    data class Group(
        val id: Int?,
        val groupName: String?,
        val is_closed: GroupsGroupIsClosed?,
        val photo: String?,
        val photo_medium: String?,
        val photo_big: String?,
        val screen_name: String?,
    ) : Creator()
}


