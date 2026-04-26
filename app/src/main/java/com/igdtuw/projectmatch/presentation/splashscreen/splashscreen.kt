package com.igdtuw.projectmatch.presentation.splashscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen( navHostController: NavHostController) {

    LaunchedEffect(Unit) {
        delay(1000)
        navHostController.navigate(Routes.WelcomeScreen){
            popUpTo<Routes.SplashScreen> { inclusive=true }
        }
    }

    Box(modifier = Modifier.fillMaxSize())
    {
        Image(painter = painterResource(id=R.drawable.logo),
            null,
            modifier = Modifier.size(250.dp)
                .align(Alignment.Center)
        )

    }

}


