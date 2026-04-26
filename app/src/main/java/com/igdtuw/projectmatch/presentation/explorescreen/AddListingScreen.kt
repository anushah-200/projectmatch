package com.igdtuw.projectmatch.presentation.explorescreen


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.viewmodel.BaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListingScreen(
    navHostController: NavHostController
) {
    val baseViewModel: BaseViewModel = hiltViewModel()

    var projectName  by remember { mutableStateOf("") }
    var skillsNeeded by remember { mutableStateOf("") }
    var role         by remember { mutableStateOf("") }
    var isLoading    by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Add Listing",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp,
                        color      = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.light_blue)
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text       = "Post a Project",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = colorResource(R.color.sapphire)
            )

            Text(
                text     = "Fill in the details below to find collaborators",
                fontSize = 14.sp,
                color    = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Project Name ──────────────────────────────────────────────────
            Text(
                text       = "Project Name",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp
            )
            OutlinedTextField(
                value         = projectName,
                onValueChange = { projectName = it },
                placeholder   = { Text("e.g. AI Study Buddy") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = colorResource(R.color.light_blue),
                    unfocusedBorderColor = Color.LightGray
                )
            )

            // ── Skills Needed ─────────────────────────────────────────────────
            Text(
                text       = "Skills Needed",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp
            )
            OutlinedTextField(
                value         = skillsNeeded,
                onValueChange = { skillsNeeded = it },
                placeholder   = { Text("e.g. #flutter #firebase #ui") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = colorResource(R.color.light_blue),
                    unfocusedBorderColor = Color.LightGray
                )
            )

            // ── Role Required ─────────────────────────────────────────────────
            Text(
                text       = "Role Required",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp
            )
            OutlinedTextField(
                value         = role,
                onValueChange = { role = it },
                placeholder   = { Text("e.g. Backend Developer") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = colorResource(R.color.light_blue),
                    unfocusedBorderColor = Color.LightGray
                )
            )

            // ── Error message ─────────────────────────────────────────────────
            errorMessage?.let {
                Text(text = it, color = Color.Red, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Submit button ─────────────────────────────────────────────────
            Button(
                onClick = {
                    when {
                        projectName.isBlank()  -> errorMessage = "Please enter a project name"
                        skillsNeeded.isBlank() -> errorMessage = "Please enter skills needed"
                        role.isBlank()         -> errorMessage = "Please enter a role"
                        else -> {
                            errorMessage = null
                            isLoading    = true
                            baseViewModel.addListing(
                                projectName   = projectName.trim(),
                                skillsNeeded  = skillsNeeded.trim(),
                                role          = role.trim(),
                                onSuccess     = {
                                    isLoading = false
                                    navHostController.popBackStack()
                                },
                                onError = { error ->
                                    isLoading    = false
                                    errorMessage = error
                                }
                            )
                        }
                    }
                },
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.light_blue)
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color    = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text     = "Post Listing",
                        fontSize = 16.sp,
                        color    = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}