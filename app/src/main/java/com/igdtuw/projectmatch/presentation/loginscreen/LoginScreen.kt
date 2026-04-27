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
import com.igdtuw.projectmatch.presentation.viewmodel.AuthState
import com.igdtuw.projectmatch.presentation.viewmodel.AuthViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.navigation.Routes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.size



@Composable
fun LoginScreen(navHostController: NavHostController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    var hasAttemptedLogin by remember { mutableStateOf(false) }

    // Reset state when LoginScreen first opens
    LaunchedEffect(Unit) {
        authViewModel.resetToUnauthenticated()
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> {
                if (hasAttemptedLogin) {  // only navigate after user actually clicks login
                    navHostController.navigate(Routes.HomeScreen) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message,
                Toast.LENGTH_LONG
            ).show()
            else -> Unit
        }
    }

    LoginScreenContent(
        authState = authState.value,
        onLogin = { email, password ->
            hasAttemptedLogin = true
            authViewModel.login(email, password)
        },
        onNavigateToSignUp = { navHostController.navigate(Routes.SignInScreen) }
    )
}

@Composable
fun LoginScreenContent(
    authState: AuthState?,
    onLogin: (String, String) -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.wallpaper),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.3f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Login",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.sapphire),
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .background(Color.White.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; error = "" },
                label = { Text("Email", color = colorResource(R.color.sapphire)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.sapphire),
                    unfocusedBorderColor = colorResource(R.color.sapphire),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(

                value = password,
                onValueChange = { password = it; error = "" },
                label = { Text("Password", color=colorResource(R.color.sapphire)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.sapphire),
                    unfocusedBorderColor = colorResource(R.color.sapphire),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                )
            )
            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = { if (!email.endsWith("@igdtuw.ac.in")) {
                    error = "Use your IGDTUW email only"
                } else {
                    onLogin(email, password)
                } },
                enabled = authState != AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.sapphire),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (authState == AuthState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Don't have an account? Sign Up",
                    color = colorResource(R.color.sapphire),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreenContent(
        authState = null,
        onLogin = { _, _ -> },
        onNavigateToSignUp = {}
    )
}
