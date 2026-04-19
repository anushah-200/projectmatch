package com.igdtuw.projectmatch.models

data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timeStamp: Long = 0L
)
