package com.igdtuw.projectmatch.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.igdtuw.projectmatch.presentation.explorescreen.ExploreScreen
import com.igdtuw.projectmatch.presentation.homescreen.HomeScreen
import com.igdtuw.projectmatch.presentation.loginscreen.LoginScreen
import com.igdtuw.projectmatch.presentation.splashscreen.SplashScreen
import com.igdtuw.projectmatch.presentation.welcomescreen.WelcomeScreen

@Composable
fun NavigationSystem(modifier: Modifier) {
    val navcontroller= rememberNavController()

    NavHost(startDestination = Routes.SplashScreen, navController = navcontroller){
        composable<Routes.SplashScreen> {
            SplashScreen(navcontroller)
        }

        composable<Routes.WelcomeScreen> {
            WelcomeScreen(navcontroller)
        }

        composable<Routes.UserRegistrationScreen> {
            LoginScreen(navcontroller)
        }

        composable<Routes.HomeScreen> {
            HomeScreen()
        }

        composable<Routes.ExploreScreen> {
            ExploreScreen()
        }






    }
}