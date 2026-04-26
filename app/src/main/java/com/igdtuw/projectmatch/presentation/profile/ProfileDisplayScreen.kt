package com.igdtuw.projectmatch.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation
import com.igdtuw.projectmatch.presentation.imageutils.ImageUtils
import com.igdtuw.projectmatch.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDisplayScreen(
    navHostController : NavHostController,
    authViewModel     : AuthViewModel
) {
    val userData by authViewModel.userData.observeAsState()


    var isEditing    by remember { mutableStateOf(false) }
    var editedName   by remember { mutableStateOf("") }
    var editedSkills by remember { mutableStateOf("") }
    var isSaving     by remember { mutableStateOf(false) }
    var saveMessage  by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        authViewModel.fetchUserProfile()
    }


    LaunchedEffect(userData) {
        editedName   = userData?.name   ?: ""
        editedSkills = userData?.skills ?: ""
    }


    Scaffold(
        bottomBar = {
            BottomNavigation(navHostController = navHostController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {


            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text       = "Profile",
                    fontSize   = 28.sp,
                    color      = colorResource(R.color.sapphire),
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                )

                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    if (isEditing) {
                        Button(
                            onClick = {
                                isSaving = true
                                saveMessage = null
                                authViewModel.updateUserProfile(
                                    name      = editedName.trim(),
                                    skills    = editedSkills.trim(),
                                    onSuccess = {
                                        isSaving    = false
                                        isEditing   = false
                                        saveMessage = "Profile updated!"
                                        authViewModel.fetchUserProfile()
                                    },
                                    onError = { error ->
                                        isSaving    = false
                                        saveMessage = error
                                    }
                                )
                            },
                            colors  = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.light_blue)
                            ),
                            enabled = !isSaving,
                            shape   = RoundedCornerShape(8.dp)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    color    = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(text = "Save", color = Color.White)
                            }
                        }

                        TextButton(onClick = {
                            isEditing    = false
                            editedName   = userData?.name   ?: ""
                            editedSkills = userData?.skills ?: ""
                            saveMessage  = null
                        }) {
                            Text(text = "Cancel", color = Color.Gray)
                        }

                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                imageVector        = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint               = colorResource(R.color.sapphire),
                                modifier           = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Profile image
                val bitmap = userData?.profileImage?.let {
                    ImageUtils.base64ToBitmap(it)
                }

                if (bitmap != null) {
                    Image(
                        bitmap             = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier           = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter            = painterResource(R.drawable.user_placeholder),
                        contentDescription = null,
                        modifier           = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save success/error message
                saveMessage?.let {
                    Text(
                        text     = it,
                        color    = if (it == "Profile updated!") Color(0xFF2E7D32) else Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                if (isEditing) {
                    Text(
                        text       = "Email",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        color      = Color.Gray,
                        modifier   = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value         = userData?.email ?: "",
                        onValueChange = {},
                        enabled       = false,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(10.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor    = Color.LightGray,
                            disabledTextColor      = Color.Gray,
                            disabledContainerColor = Color(0xFFF5F5F5)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text       = "Name",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        modifier   = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value         = editedName,
                        onValueChange = { editedName = it },
                        placeholder   = { Text("Enter your name") },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(10.dp),
                        singleLine    = true,
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = colorResource(R.color.light_blue),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text       = "Skills",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        modifier   = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value         = editedSkills,
                        onValueChange = { editedSkills = it },
                        placeholder   = { Text("e.g. #flutter #firebase #kotlin") },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(10.dp),
                        singleLine    = true,
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = colorResource(R.color.light_blue),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                } else {
                    ProfileInfoRow(label = "Name",   value = userData?.name   ?: "No Name")
                    ProfileInfoRow(label = "Email",  value = userData?.email  ?: "No Email")
                    ProfileInfoRow(label = "Skills", value = userData?.skills ?: "No Skills")
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text       = label,
            fontSize   = 13.sp,
            color      = Color.Gray,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text       = value,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Medium,
            color      = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
    }
}