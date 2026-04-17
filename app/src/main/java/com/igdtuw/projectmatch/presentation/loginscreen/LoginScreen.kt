package com.igdtuw.projectmatch.presentation.loginscreen

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.igdtuw.projectmatch.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.FirebaseUser
import com.igdtuw.projectmatch.presentation.navigation.Routes




@Composable
fun LoginScreen(navHostController: NavHostController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Create Account", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("IGDTUW Email") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {

            if (!email.endsWith("@igdtuw.ac.in")) {
                error = "Use IGDTUW email only"
                return@Button
            }

            if (password.length < 6) {
                error = "Password must be at least 6 characters"
                return@Button
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        navHostController.navigate(Routes.HomeScreen)
                    } else {
                        error = it.exception?.message ?: "Signup failed"
                    }
                }

        }) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Already have an account? Login",
            color = Color.Blue,
            modifier = Modifier.clickable {
                navHostController.navigate(Routes.UserRegistrationScreen)
            }
        )

        if (error.isNotEmpty()) {
            Text(error, color = Color.Red)
        }
    }
}
