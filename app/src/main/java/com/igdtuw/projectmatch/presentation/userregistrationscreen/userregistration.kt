package com.igdtuw.projectmatch.presentation.userregistrationscreen

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.colorspace.ColorSpace

@Composable
@Preview(showSystemUi = true)
fun UserRegistrationScreen(){
    var email by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf<Boolean?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment =Alignment.CenterHorizontally ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text="Enter your IGDTUW Email ID",fontSize=22.sp, color = colorResource(id = com.igdtuw.projectmatch.R.color.sapphire),
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))
        Row {
            Text(text = "ProjectMatch will need to verify your email ID")
            Spacer(modifier = Modifier.width(4.dp))

            Text(text = "What's",color = colorResource(id = com.igdtuw.projectmatch.R.color.sapphire))

        }
        Text(text = "my email ID",color = colorResource(id = com.igdtuw.projectmatch.R.color.sapphire))

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email=it
                isValid=null
            })
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {isValid=isValidEmail(email)},shape=RoundedCornerShape(6.dp), colors = ButtonDefaults.buttonColors(containerColor = colorResource(
            R.color.sapphire))) {
            Text("Next", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (isValid != null) {
            if (isValid == true) {
                Text("Valid IGDTUW Email")
            } else {
                Text("Invalid Email ")
            }
        }

    }
}
fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            email.endsWith("@igdtuw.ac.in")
}