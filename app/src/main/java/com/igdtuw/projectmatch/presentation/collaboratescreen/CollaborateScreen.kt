package com.igdtuw.projectmatch.presentation.collaboratescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation

@Composable
fun CollaborateScreen(
    navHostController: NavHostController
) {
    val sampleCollaboration = listOf(
        Collaboration(name = "AI Hackathon", memberCount = "15 members"),
        Collaboration(name = "Mobile App Group", memberCount = "11 members"),
        Collaboration(name = "Innovate and Learn", memberCount = "9 members"),
        Collaboration(name = "Machine learning with AI", memberCount = "13 members")
    )

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row {
                        Text(
                            text = "Collaborate",
                            fontSize = 32.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 5.dp, top = 25.dp)
                        )
                    }
                    HorizontalDivider()
                }
            }
        },
        bottomBar = {
            BottomNavigation(navHostController = navHostController)
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.sapphire)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Create", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Collaborations",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyColumn {
                items(sampleCollaboration) { collaboration ->
                    CollaborateDesign(collaboration = collaboration)
                }
            }
        }
    }
}