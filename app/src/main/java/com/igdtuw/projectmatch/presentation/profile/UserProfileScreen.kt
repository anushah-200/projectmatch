package com.igdtuw.projectmatch.presentation.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.loginscreen.LoginScreen
import com.igdtuw.projectmatch.presentation.navigation.Routes




@Composable
fun UserProfileScreen(navHostController: NavHostController){
    var name by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmapImage by remember { mutableStateOf<Bitmap?>(null) }

    val auth = FirebaseAuth.getInstance()

    val firebaseAuth= Firebase.auth
    val email = firebaseAuth.currentUser?.email?:""
    //val userId = firebaseAuth.currentUser?.uid?:""

    val context = LocalContext.current

    val imagePickerLauncher= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {uri: Uri?->
            profileImageUri= uri
            uri?.let {
                bitmapImage = if(Build.VERSION.SDK_INT<28){
                    @Suppress("DEPRECATION")
                    android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver,it)
                }else{
                    val source = ImageDecoder.createSource(context.contentResolver,it)
                    ImageDecoder.decodeBitmap(source)
                }
            }
        }
    )
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(modifier = Modifier
            .size(128.dp)
            .clip(CircleShape)
            .border(2.dp, color = Color.Gray, shape = CircleShape)
            .clickable{imagePickerLauncher.launch("image/*")}){
            if (bitmapImage!=null){
                Image(bitmap = bitmapImage!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                    .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }else if (profileImageUri!=null){
                Image(painter = rememberAsyncImagePainter(profileImageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                    .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            else{
                Image(painter = painterResource(R.drawable.user_placeholder),
                    contentDescription = null,
                    modifier = Modifier
                    .fillMaxSize().align(Alignment.Center))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "$email")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = {name=it},
            label = {
                Text("Name")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(R.color.light_blue)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = skills,
            onValueChange = {name=it},
            label = {
                Text("Skills")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colorResource(R.color.light_blue)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

    }
}
