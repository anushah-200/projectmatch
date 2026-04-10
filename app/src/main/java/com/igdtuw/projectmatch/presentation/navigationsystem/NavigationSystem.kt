package com.igdtuw.projectmatch.presentation.navigationsystem

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.igdtuw.projectmatch.presentation.homescreen.HomeScreen
import com.igdtuw.projectmatch.presentation.splashscreen.SplashScreen
import com.igdtuw.projectmatch.presentation.userregistrationscreen.UserRegistrationScreen
import com.igdtuw.projectmatch.presentation.welcomescreen.WelcomeScreen

@Composable
fun NavigationSystem(){
    val navcontroller= rememberNavController()

    NavHost(startDestination = Routes.SplashScreen, navController = navcontroller){
        composable<Routes.SplashScreen> {
            SplashScreen(navcontroller)
        }

        composable<Routes.WelcomeScreen> {
            WelcomeScreen(navcontroller)
        }

        composable<Routes.UserRegistrationScreen> {
            UserRegistrationScreen()
        }

        composable<Routes.HomeScreen> {
            HomeScreen()
        }


    }
}