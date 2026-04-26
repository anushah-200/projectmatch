package com.igdtuw.projectmatch.presentation.collaboratescreen

data class Collaboration(
    val id: String = "",
    val name: String = "",
    val members: Int = 0,
    val joinedUsers: Map<String, Boolean> = emptyMap()
)

//package com.igdtuw.projectmatch.presentation.collaboratescreen
//
//data class Collaboration(
//    val id: String = "",
//    val name: String = "",
//    val members: Int = 0,
//    val joinedUsers: Map<String, Boolean> = emptyMap()
//)