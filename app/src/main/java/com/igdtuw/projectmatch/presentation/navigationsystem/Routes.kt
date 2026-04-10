package com.igdtuw.projectmatch.presentation.navigationsystem

import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    data object SplashScreen: Routes()

    @Serializable
    data object WelcomeScreen: Routes()

    @Serializable
    data object UserRegistrationScreen: Routes()

    @Serializable
    data object HomeScreen: Routes()

//    @Serializable
//    data object ExploreScreen: Routes()
}