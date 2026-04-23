package com.igdtuw.projectmatch.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    data object SplashScreen : Routes()

    @Serializable
    data object WelcomeScreen : Routes()

    @Serializable
    data object LoginScreen : Routes()

    @Serializable
    data object SignInScreen : Routes()

    @Serializable
    data object HomeScreen : Routes()

    @Serializable
    data object ExploreScreen : Routes()

    @Serializable
    data object UserProfileScreen : Routes()

    @Serializable
    data object ProfileDisplayScreen : Routes()

    @Serializable
    data object CollaborateScreen : Routes()

    @Serializable
    data object SettingScreen : Routes()

    @Serializable
    data object ChatScreen : Routes() {
        const val route = "chat_screen/{email}"
        fun createRoute(email: String): String {
            val encodedEmail = android.net.Uri.encode(email)
            return "chat_screen/$encodedEmail"
        }
    }
}