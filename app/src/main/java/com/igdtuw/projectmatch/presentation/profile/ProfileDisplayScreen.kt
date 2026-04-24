package com.igdtuw.projectmatch.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation
import com.igdtuw.projectmatch.presentation.imageutils.ImageUtils
import com.igdtuw.projectmatch.presentation.viewmodel.AuthViewModel

@Composable
fun ProfileDisplayScreen(
    navHostController: NavHostController,
    authViewModel: AuthViewModel
) {
    val userData by authViewModel.userData.observeAsState()

    LaunchedEffect(Unit) {
        authViewModel.fetchUserProfile()
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Profile",
                        fontSize = 32.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 25.dp, bottom = 12.dp)
                    )
                    HorizontalDivider()
                }
            }
        },
        bottomBar = {
            BottomNavigation(navHostController = navHostController)
        },
        containerColor = Color(0xFFF0F0F8) // light lavender-grey background matching the app
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- Profile Image ---
            val bitmap = userData?.profileImage?.let {
                ImageUtils.base64ToBitmap(it)
            }

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.user_placeholder),
                        contentDescription = "Profile photo placeholder",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Info Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {

                    ProfileField(label = "Name", value = userData?.name ?: "—")

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFFE8E8F0)
                    )

                    ProfileField(label = "Email", value = userData?.email ?: "—")

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFFE8E8F0)
                    )

                    ProfileField(label = "Skills", value = userData?.skills ?: "—")
                }
            }
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6B6B8F), // muted purple-grey for labels
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF1A1A2E) // dark navy for values
        )
    }
}