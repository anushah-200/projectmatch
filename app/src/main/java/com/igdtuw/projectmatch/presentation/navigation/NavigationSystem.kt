package com.igdtuw.projectmatch.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.igdtuw.projectmatch.presentation.explorescreen.ExploreScreen
import com.igdtuw.projectmatch.presentation.homescreen.HomeScreen
import com.igdtuw.projectmatch.presentation.splashscreen.SplashScreen
import com.igdtuw.projectmatch.presentation.userregistrationscreen.UserRegistrationScreen
import com.igdtuw.projectmatch.presentation.welcomescreen.WelcomeScreen


@Composable
fun NavigationSystem(){
    val navcontroller= rememberNavController()

    NavHost(startDestination = Routes.SplashScreen, navController = navcontroller){
        composable<Routes.SplashScreen> {
            SplashScreen()
        }

        composable<Routes.WelcomeScreen> {
            WelcomeScreen()
        }

        composable<Routes.UserRegistrationScreen> {
            UserRegistrationScreen()
        }

        composable<Routes.HomeScreen> {
            HomeScreen()
        }

        composable<Routes.ExploreScreen> {
            ExploreScreen()
        }



    }
}