package com.igdtuw.projectmatch.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.igdtuw.projectmatch.presentation.viewmodel.AuthViewModel
import com.igdtuw.projectmatch.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation
import com.igdtuw.projectmatch.presentation.imageutils.ImageUtils
import com.igdtuw.projectmatch.presentation.navigation.Routes


@Composable
fun ProfileDisplayScreen(navHostController: NavHostController,authViewModel: AuthViewModel){
    val userData by authViewModel.userData.observeAsState()

    LaunchedEffect(Unit) {
        authViewModel.fetchUserProfile()
    }

    Scaffold(topBar = {
        Box(modifier = Modifier.fillMaxWidth()){
            Column {
                Row{
                    Text(text="Profile Screen",
                        fontSize = 32.sp,
                        color = Color.Black,
                        fontWeight= FontWeight.Bold,
                        modifier = Modifier.padding(start = 5.dp, top = 25.dp)
                    )
                }
                HorizontalDivider()
            }
        }
    },  bottomBar = {
        BottomNavigation(navHostController = navHostController)
    }){paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            val bitmap = userData?.profileImage?.let {
                ImageUtils.base64ToBitmap(it)
            }

            if (bitmap != null){
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.user_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = userData?.name ?: "No Name")

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = userData?.email ?: "No Email")

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = userData?.skills ?: "No Skills")
        }
    }
}