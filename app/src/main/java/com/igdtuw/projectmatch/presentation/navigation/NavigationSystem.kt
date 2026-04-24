package com.igdtuw.projectmatch.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.igdtuw.projectmatch.presentation.collaboratescreen.CollaborateScreen
import com.igdtuw.projectmatch.presentation.explorescreen.AddListingScreen
import com.igdtuw.projectmatch.presentation.explorescreen.ExploreScreen
import com.igdtuw.projectmatch.presentation.homescreen.HomeScreen
import com.igdtuw.projectmatch.presentation.loginscreen.LoginScreen
import com.igdtuw.projectmatch.presentation.signinscreen.SignInScreen
import com.igdtuw.projectmatch.presentation.splashscreen.SplashScreen
import com.igdtuw.projectmatch.presentation.welcomescreen.WelcomeScreen
import com.igdtuw.projectmatch.presentation.profile.UserProfileScreen
import com.igdtuw.projectmatch.presentation.chatscreen.ChatScreen
import com.igdtuw.projectmatch.presentation.profile.ProfileDisplayScreen
import com.igdtuw.projectmatch.presentation.viewmodel.AuthViewModel
import com.igdtuw.projectmatch.presentation.viewmodel.BaseViewModel

@Composable
fun NavigationSystem(modifier: Modifier) {

    val navcontroller = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        startDestination = Routes.SplashScreen,
        navController    = navcontroller
    ) {
        composable<Routes.SplashScreen> {
            SplashScreen(navcontroller)
        }
        composable<Routes.WelcomeScreen> {
            WelcomeScreen(navcontroller, authViewModel = authViewModel)
        }
        composable<Routes.LoginScreen> {
            LoginScreen(navcontroller, authViewModel = authViewModel)
        }
        composable<Routes.SignInScreen> {
            SignInScreen(navcontroller, authViewModel = authViewModel)
        }
        composable<Routes.HomeScreen> {
            val baseViewModel: BaseViewModel = hiltViewModel()
            HomeScreen(navcontroller, baseViewModel)
        }
        composable<Routes.ExploreScreen> {
            ExploreScreen(navcontroller)
        }
        composable<Routes.CollaborateScreen> {
            CollaborateScreen(navcontroller)
        }
        composable<Routes.UserProfileScreen> {
            UserProfileScreen(navcontroller, authViewModel = authViewModel)
        }
        composable<Routes.ProfileDisplayScreen> {
            ProfileDisplayScreen(navcontroller, authViewModel = authViewModel)
        }
        composable<Routes.AddListingScreen> {
            AddListingScreen(navcontroller)
        }
        composable(
            route     = Routes.ChatScreen.route,
            arguments = listOf(
                navArgument("email") {
                    type     = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val email         = backStackEntry.arguments?.getString("email") ?: ""
            val baseViewModel: BaseViewModel = hiltViewModel()
            ChatScreen(
                navHostController = navcontroller,
                email             = email,
                baseViewModel     = baseViewModel
            )
        }
    }
}