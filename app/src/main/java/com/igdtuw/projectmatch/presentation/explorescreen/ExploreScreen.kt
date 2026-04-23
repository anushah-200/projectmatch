package com.igdtuw.projectmatch.presentation.explorescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation
import com.igdtuw.projectmatch.presentation.homescreen.ChatListModel
import com.igdtuw.projectmatch.presentation.imageutils.ImageUtils
import com.igdtuw.projectmatch.presentation.navigation.Routes
import com.igdtuw.projectmatch.presentation.viewmodel.BaseViewModel

@Composable
fun ExploreScreen(
    navHostController: NavHostController
) {
    val baseViewModel: BaseViewModel = hiltViewModel()
    val allUsers by baseViewModel.allUsers.collectAsState()

    LaunchedEffect(Unit) {
        baseViewModel.fetchAllUsers()
    }

    Scaffold(
        bottomBar = { BottomNavigation(navHostController = navHostController) },
        topBar = { Topbar() }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Explore",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.sapphire),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
            )

            HorizontalDivider()

            if (allUsers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorResource(R.color.light_blue)
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(allUsers) { user ->
                        ExploreUserCard(
                            user = user,
                            onClick = {
                                baseViewModel.addChat(user)
                                navHostController.navigate(
                                    Routes.ChatScreen.createRoute(email = user.email ?: "")
                                )
                            }
                        )
                        HorizontalDivider(
                            color = Color.LightGray,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreUserCard(
    user: ChatListModel,
    onClick: () -> Unit
) {
    val bitmap = remember(user.profileImage) {
        user.profileImage?.let { ImageUtils.base64ToBitmap(it) }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Profile picture ───────────────────────────────────────────────────
        Image(
            painter = if (bitmap != null) rememberImagePainter(bitmap)
            else painterResource(R.drawable.user_placeholder),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(14.dp))

        // ── Name + Skills ─────────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name ?: "Unknown",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.message ?: "No skills listed",
                fontSize = 13.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }

        // ── Chat button ───────────────────────────────────────────────────────
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.light_blue)
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(34.dp)
        ) {
            Text(
                text = "Chat",
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}