package com.igdtuw.projectmatch.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    data object SplashScreen: Routes()

    @Serializable
    data object WelcomeScreen: Routes()

    @Serializable
    data object LoginScreen: Routes()

    @Serializable
    data object SignInScreen: Routes()

    @Serializable
    data object HomeScreen: Routes()

    @Serializable
    data object ExploreScreen: Routes()

    @Serializable
    data object UserProfileScreen: Routes()

    @Serializable
    data object CollaborateScreen: Routes()


}