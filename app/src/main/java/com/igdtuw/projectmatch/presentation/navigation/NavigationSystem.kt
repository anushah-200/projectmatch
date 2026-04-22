package com.igdtuw.projectmatch.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.igdtuw.projectmatch.presentation.collaboratescreen.CollaborateScreen
import com.igdtuw.projectmatch.presentation.viewmodel.AuthViewModel
import com.igdtuw.projectmatch.presentation.explorescreen.ExploreScreen
import com.igdtuw.projectmatch.presentation.homescreen.HomeScreen
import com.igdtuw.projectmatch.presentation.loginscreen.LoginScreen
import com.igdtuw.projectmatch.presentation.signinscreen.SignInScreen
import com.igdtuw.projectmatch.presentation.splashscreen.SplashScreen
import com.igdtuw.projectmatch.presentation.welcomescreen.WelcomeScreen
import com.igdtuw.projectmatch.presentation.profile.UserProfileScreen
import com.igdtuw.projectmatch.presentation.viewmodel.BaseViewModel

@Composable
fun NavigationSystem(modifier: Modifier) {
    val navcontroller= rememberNavController()
    val authViewModel: AuthViewModel= viewModel()
    NavHost(startDestination = Routes.SplashScreen, navController = navcontroller){
        composable<Routes.SplashScreen> {
            SplashScreen(navcontroller)
        }

        composable<Routes.WelcomeScreen> {
            WelcomeScreen(navcontroller,authViewModel = authViewModel)
        }

        composable<Routes.LoginScreen> {
            LoginScreen(navcontroller, authViewModel = authViewModel)
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
        composable<Routes.SignInScreen> {
            SignInScreen(navcontroller, authViewModel = authViewModel)
        }
        composable<Routes.UserProfileScreen> {
            UserProfileScreen(navcontroller, authViewModel = authViewModel)
        }







    }
}