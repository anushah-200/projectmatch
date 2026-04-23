package com.igdtuw.projectmatch.presentation.homescreen

data class ChatListModel (
    val name: String? = null,
    val userId: String? = null,
    val email: String? = null,
    val image: Int? = null,
    val message: String? = null,
    val time: String? = null,
    val profileImage: String? = null
){
    constructor(): this(null,null,null,null,null,null,null)
}
