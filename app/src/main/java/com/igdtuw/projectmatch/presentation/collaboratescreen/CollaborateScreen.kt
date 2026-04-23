package com.igdtuw.projectmatch.presentation.collaboratescreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation
import com.igdtuw.projectmatch.presentation.navigation.Routes

@Composable
fun CollaborateScreen(
    navHostController: NavHostController
) {

    val dbRef = FirebaseDatabase.getInstance().getReference("collaborations")
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

    var collaborations by remember { mutableStateOf(listOf<Collaboration>()) }

    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    // 🔄 REAL-TIME UPDATES
    LaunchedEffect(Unit) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val list = mutableListOf<Collaboration>()

                for (child in snapshot.children) {
                    val collab = child.getValue(Collaboration::class.java)

                    if (collab != null) {
                        list.add(
                            collab.copy(id = child.key ?: "")
                        )
                    }
                }

                collaborations = list
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Scaffold(
        topBar = {
            Column {
                Text(
                    text = "Collaborate",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
                HorizontalDivider()
            }
        },
        bottomBar = {
            BottomNavigation(
                navHostController = navHostController,
                selectedItem = 2,
                onClick = { index ->
                    when (index) {
                        0 -> navHostController.navigate(Routes.HomeScreen)
                        1 -> navHostController.navigate(Routes.ExploreScreen)
                        2 -> navHostController.navigate(Routes.CollaborateScreen)
                        3 -> navHostController.navigate(Routes.UserProfileScreen)
                    }
                }
            )
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {

            // ➕ CREATE BUTTON
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.sapphire)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Create", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "Collaborations",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )

            LazyColumn {
                items(collaborations) { collab ->

                    val isJoined = collab.joinedUsers.containsKey(userId)

                    CollaborateDesign(
                        collaboration = collab,
                        isJoined = isJoined,
                        onJoinClick = {

                            val updates = hashMapOf<String, Any>(
                                "joinedUsers/$userId" to true,
                                "members" to (collab.members + 1)
                            )

                            dbRef.child(collab.id).updateChildren(updates)
                        }
                    )
                }
            }
        }
    }

    // ➕ CREATE DIALOG
    if (showDialog) {

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {

                    val id = dbRef.push().key ?: return@Button

                    val newCollab = Collaboration(
                        id = id,
                        name = name,
                        members = 1,
                        joinedUsers = mapOf(userId to true)
                    )

                    dbRef.child(id).setValue(newCollab)

                    name = ""
                    showDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Create Collaboration") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Collaboration Name") }
                )
            }
        )
    }
}