package com.igdtuw.projectmatch.presentation.collaboratescreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CollaborateDesign(
    collaboration: Collaboration,
    isJoined: Boolean,
    onJoinClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = collaboration.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${collaboration.members} members",
                    fontSize = 14.sp
                )
            }

            if (!isJoined) {
                Button(onClick = onJoinClick) {
                    Text("Join")
                }
            } else {
                Text(
                    text = "Joined",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}