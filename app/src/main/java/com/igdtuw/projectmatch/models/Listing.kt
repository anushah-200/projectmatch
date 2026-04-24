package com.igdtuw.projectmatch.models


data class Listing(
    val listingId    : String? = null,
    val userId       : String? = null,
    val userName     : String? = null,
    val userEmail    : String? = null,
    val projectName  : String? = null,
    val skillsNeeded : String? = null,
    val role         : String? = null
) {
    constructor() : this(null, null, null, null, null, null, null)
}