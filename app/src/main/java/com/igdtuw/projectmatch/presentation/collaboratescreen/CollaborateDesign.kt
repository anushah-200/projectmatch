/*package com.igdtuw.projectmatch.presentation.collaboratescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CollaborateDesign(
    collaboration: Collaboration,
    isJoined: Boolean,
    userNames: Map<String, String>,
    onJoinClick: () -> Unit
) {

    var showMembers by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(modifier = Modifier.padding(14.dp)) {

            Text(
                text = collaboration.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "${collaboration.members} members",
                fontSize = 14.sp
            )

            Spacer(Modifier.height(10.dp))

            // 🔽 DROPDOWN
            Text(
                text = if (showMembers) "▼ Members" else "▶ Members",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    showMembers = !showMembers
                }
            )

            if (showMembers) {

                Spacer(Modifier.height(6.dp))

                collaboration.joinedUsers.keys.forEach { uid ->
                    val name = userNames[uid] ?: "Loading..."

                    Text(
                        text = "• $name",
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

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
*/

//Previous code
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