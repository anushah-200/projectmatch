package com.igdtuw.projectmatch.models

data class EmailAuthUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profileImage: String? = null,
    val skills: String = "",
)
