package com.igdtuw.projectmatch.presentation.loginscreen

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.igdtuw.projectmatch.presentation.authviewmodel.AuthState
import com.igdtuw.projectmatch.presentation.authviewmodel.AuthViewModel

import com.igdtuw.projectmatch.presentation.navigation.Routes


@Composable
fun LoginScreen(navHostController: NavHostController,authViewModel: AuthViewModel) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

//    val auth = FirebaseAuth.getInstance()
    val authState=authViewModel.authState.observeAsState()
    val context= LocalContext.current
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navHostController.navigate(Routes.HomeScreen)
            is AuthState.Error -> Toast.makeText(context,(authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            authViewModel.login(email,password)
//            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        navHostController.navigate(Routes.HomeScreen)
//                    } else {
//                        error = it.exception?.message ?: "Login failed"
//                    }
//                }
        },
            enabled = authState.value!= AuthState.Loading
            ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Don't have an account? Sign Up",
            color = Color.Blue,
            modifier = Modifier.clickable {
                navHostController.navigate(Routes.SignInScreen)
            }
        )

        if (error.isNotEmpty()) {
            Text(error, color = Color.Red)
        }
    }
}

fun isValidEmail(email: String): Boolean
{ return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@igdtuw.ac.in") }