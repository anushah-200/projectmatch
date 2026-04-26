package com.igdtuw.projectmatch.presentation.welcomescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.viewmodel.AuthViewModel
import com.igdtuw.projectmatch.presentation.navigation.Routes

@Composable
fun WelcomeScreen( navHostController: NavHostController,authViewModel: AuthViewModel){
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally){
        Image(painter = painterResource(id = R.drawable.welcome),
            null,
            modifier = Modifier.size(600.dp)
        )
        Text(text = "Welcome to ProjectMatch", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            Text(text = "Read our",color=Color.Gray,fontSize = 14.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = "Privacy Policy", color = colorResource(id =R.color.sapphire),fontSize = 14.sp)
            Text(text = "Tap 'Agree and Continue'", color =Color.Gray,fontSize = 14.sp)
            Spacer(modifier = Modifier.width(5.dp))
        }
        Row {
            Text(text = "accept the ",color=Color.Gray,fontSize = 14.sp)
            Text(text = "Terms of Service", color = colorResource(id =R.color.sapphire),fontSize = 14.sp)

        }
        Spacer(modifier= Modifier.height(24.dp))
        Button(onClick = {authViewModel.resetToUnauthenticated()
            navHostController.navigate(Routes.LoginScreen)},
            modifier= Modifier.size(280.dp,43.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id =R.color.sapphire)) )
        {
            Text("Agree and Continue", fontSize = 16.sp )
        }
    }


}