package com.igdtuw.projectmatch.presentation.collaboratescreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
    val usersRef = FirebaseDatabase.getInstance().getReference("users")

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: "guest"

    var collaborations by remember { mutableStateOf(listOf<Collaboration>()) }

    // uid → name map
    var userNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }

    val filteredCollaborations = remember(collaborations, searchText, isSearching) {
        if (isSearching && searchText.isNotBlank()) {
            collaborations.filter {
                it.name.contains(searchText, ignoreCase = true)
            }
        } else collaborations
    }

    // 🔄 FETCH COLLABORATIONS
    LaunchedEffect(Unit) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull {
                    it.getValue(Collaboration::class.java)?.copy(id = it.key ?: "")
                }
                collaborations = list
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 🔥 FETCH USER NAMES
    LaunchedEffect(collaborations) {
        val tempMap = mutableMapOf<String, String>()

        collaborations.forEach { collab ->
            collab.joinedUsers.keys.forEach { uid ->
                usersRef.child(uid).child("name")
                    .get()
                    .addOnSuccessListener { snap ->
                        val name = snap.getValue(String::class.java) ?: "Unknown"
                        tempMap[uid] = name
                        userNames = tempMap.toMap()
                    }
            }
        }
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
        ) {

            // 🔥 YOUR TOP BAR (UNCHANGED EXACTLY)
            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {

                if (isSearching) {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search") },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(0.8f),
                        singleLine = true
                    )
                } else {
                    Text(
                        text = "Community",
                        fontSize = 28.sp,
                        color = colorResource(R.color.sapphire),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp)
                    )
                }

                if (isSearching) {
                    IconButton(
                        onClick = {
                            isSearching = false
                            searchText = ""
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.cross),
                            contentDescription = null,
                            modifier           = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Row(modifier = Modifier.align(Alignment.CenterEnd)) {

                        IconButton(onClick = { isSearching = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.search_icon),
                                contentDescription = null,
                                modifier           = Modifier.size(24.dp)
                            )
                        }

                        Box {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.menu_icon),
                                    contentDescription = null,
                                    modifier           = Modifier.size(24.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Sign Out") },
                                    onClick = {
                                        showMenu = false
                                        navHostController.navigate(Routes.WelcomeScreen) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()

            // ➕ CREATE
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Create")
            }

            LazyColumn {
                items(filteredCollaborations) { collab ->

                    val isJoined = collab.joinedUsers.containsKey(userId)

                    CollaborateDesign(
                        collaboration = collab,
                        isJoined = isJoined,
                        userNames = userNames,
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

    // ➕ CREATE DIALOG (UNCHANGED)
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
                }) { Text("Save") }
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
//Previous Code:
//package com.igdtuw.projectmatch.presentation.collaboratescreen
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//import com.igdtuw.projectmatch.R
//import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation
//import com.igdtuw.projectmatch.presentation.navigation.Routes
//
//@Composable
//fun CollaborateScreen(
//    navHostController: NavHostController
//) {
//    val dbRef  = FirebaseDatabase.getInstance().getReference("collaborations")
//    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
//
//    var collaborations by remember { mutableStateOf(listOf<Collaboration>()) }
//    var showDialog     by remember { mutableStateOf(false) }
//    var name           by remember { mutableStateOf("") }
//
//    // ── Search / menu state (mirrors HomeScreen) ──────────────────────────
//    var isSearching by remember { mutableStateOf(false) }
//    var searchText  by remember { mutableStateOf("") }
//    var showMenu    by remember { mutableStateOf(false) }
//
//    // ── Filtered list ─────────────────────────────────────────────────────
//    val filteredCollaborations = remember(collaborations, searchText, isSearching) {
//        if (isSearching && searchText.isNotBlank())
//            collaborations.filter {
//                it.name.contains(searchText, ignoreCase = true)
//            }
//        else collaborations
//    }
//
//    LaunchedEffect(Unit) {
//        dbRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val list = mutableListOf<Collaboration>()
//                for (child in snapshot.children) {
//                    val collab = child.getValue(Collaboration::class.java)
//                    if (collab != null) list.add(collab.copy(id = child.key ?: ""))
//                }
//                collaborations = list
//            }
//            override fun onCancelled(error: DatabaseError) {}
//        })
//    }
//
//    Scaffold(
//        bottomBar = {
//            BottomNavigation(navHostController = navHostController)
//        }
//    ) { paddingValues ->
//
//        Column(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//        ) {
//
//            // ── Top Bar ───────────────────────────────────────────────────
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Box(modifier = Modifier.fillMaxWidth()) {
//
//                if (isSearching) {
//                    // Search text field
//                    TextField(
//                        value         = searchText,
//                        onValueChange = { searchText = it },
//                        placeholder   = { Text(text = "Search") },
//                        colors        = TextFieldDefaults.colors(
//                            unfocusedContainerColor  = Color.Transparent,
//                            focusedContainerColor    = Color.Transparent,
//                            focusedIndicatorColor    = Color.Transparent,
//                            unfocusedIndicatorColor  = Color.Transparent
//                        ),
//                        modifier  = Modifier
//                            .padding(start = 12.dp)
//                            .align(Alignment.CenterStart)
//                            .fillMaxWidth(0.8f),
//                        singleLine = true
//                    )
//                } else {
//                    // Title
//                    Text(
//                        text       = "Community",
//                        fontSize   = 28.sp,
//                        color      = colorResource(R.color.sapphire),
//                        fontWeight = FontWeight.Bold,
//                        modifier   = Modifier
//                            .align(Alignment.CenterStart)
//                            .padding(start = 16.dp)
//                    )
//                }
//
//                if (isSearching) {
//                    // Close search
//                    IconButton(
//                        onClick  = {
//                            isSearching = false
//                            searchText  = ""
//                        },
//                        modifier = Modifier.align(Alignment.CenterEnd)
//                    ) {
//                        Icon(
//                            painter            = painterResource(id = R.drawable.cross),
//                            contentDescription = null,
//                            modifier           = Modifier.size(24.dp)
//                        )
//                    }
//                } else {
//                    Row(modifier = Modifier.align(Alignment.CenterEnd)) {
//                        // Search icon
//                        IconButton(onClick = { isSearching = true }) {
//                            Icon(
//                                painter            = painterResource(id = R.drawable.search_icon),
//                                contentDescription = null,
//                                modifier           = Modifier.size(24.dp)
//                            )
//                        }
//
//                        // Menu icon + dropdown
//                        Box {
//                            IconButton(onClick = { showMenu = !showMenu }) {
//                                Icon(
//                                    painter            = painterResource(id = R.drawable.menu_icon),
//                                    contentDescription = null,
//                                    modifier           = Modifier.size(24.dp)
//                                )
//                            }
//
//                            DropdownMenu(
//                                expanded          = showMenu,
//                                onDismissRequest  = { showMenu = false }
//                            ) {
//                                DropdownMenuItem(
//                                    text = { Text("Sign Out") },
//                                    leadingIcon = {
//                                        Icon(
//                                            painter            = painterResource(id = R.drawable.baseline_logout_24),
//                                            contentDescription = null,
//                                            modifier           = Modifier.size(20.dp)
//                                        )
//                                    },
//                                    onClick = {
//                                        showMenu = false
//                                        navHostController.navigate(Routes.WelcomeScreen) {
//                                            popUpTo(0) { inclusive = true }
//                                        }
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//            HorizontalDivider()
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // ── Body ──────────────────────────────────────────────────────
//            Button(
//                onClick  = { showDialog = true },
//                colors   = ButtonDefaults.buttonColors(
//                    containerColor = colorResource(id = R.color.sapphire)
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//            ) {
//                Text("Create", fontSize = 18.sp, fontWeight = FontWeight.Bold)
//            }
//
//            Text(
//                text       = "Collaborations",
//                fontSize   = 20.sp,
//                fontWeight = FontWeight.Bold,
//                modifier   = Modifier.padding(12.dp)
//            )
//
//            LazyColumn {
//                items(filteredCollaborations) { collab ->
//                    val isJoined = collab.joinedUsers.containsKey(userId)
//                    CollaborateDesign(
//                        collaboration = collab,
//                        isJoined      = isJoined,
//                        onJoinClick   = {
//                            val updates = hashMapOf<String, Any>(
//                                "joinedUsers/$userId" to true,
//                                "members"             to (collab.members + 1)
//                            )
//                            dbRef.child(collab.id).updateChildren(updates)
//                        }
//                    )
//                }
//            }
//        }
//    }
//
//    // ── Create Dialog ─────────────────────────────────────────────────────
//    if (showDialog) {
//        AlertDialog(
//            onDismissRequest = { showDialog = false },
//            confirmButton    = {
//                Button(onClick = {
//                    val id = dbRef.push().key ?: return@Button
//                    val newCollab = Collaboration(
//                        id          = id,
//                        name        = name,
//                        members     = 1,
//                        joinedUsers = mapOf(userId to true)
//                    )
//                    dbRef.child(id).setValue(newCollab)
//                    name       = ""
//                    showDialog = false
//                }) { Text("Save") }
//            },
//            dismissButton = {
//                Button(onClick = { showDialog = false }) { Text("Cancel") }
//            },
//            title = { Text("Create Collaboration") },
//            text  = {
//                OutlinedTextField(
//                    value         = name,
//                    onValueChange = { name = it },
//                    label         = { Text("Collaboration Name") }
//                )
//            }
//        )
//    }
//}

//package com.igdtuw.projectmatch.presentation.collaboratescreen
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//import com.igdtuw.projectmatch.R
//import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation
//import com.igdtuw.projectmatch.presentation.navigation.Routes
//
//@Composable
//fun CollaborateScreen(
//    navHostController: NavHostController
//) {
//
//    val dbRef = FirebaseDatabase.getInstance().getReference("collaborations")
//    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
//
//    var collaborations by remember { mutableStateOf(listOf<Collaboration>()) }
//
//    var showDialog by remember { mutableStateOf(false) }
//    var name by remember { mutableStateOf("") }
//
//    // 🔄 REAL-TIME UPDATES
//    LaunchedEffect(Unit) {
//        dbRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                val list = mutableListOf<Collaboration>()
//
//                for (child in snapshot.children) {
//                    val collab = child.getValue(Collaboration::class.java)
//
//                    if (collab != null) {
//                        list.add(
//                            collab.copy(id = child.key ?: "")
//                        )
//                    }
//                }
//
//                collaborations = list
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        })
//    }
//
//    Scaffold(
//        topBar = {
//            Column {
//                Text(
//                    text = "Collaborate",
//                    fontSize = 30.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(12.dp)
//                )
//                HorizontalDivider()
//            }
//        },
//        bottomBar = {
//            BottomNavigation(
//                navHostController = navHostController,
//
//            )
//        }
//    ) { padding ->
//
//        Column(modifier = Modifier.padding(padding)) {
//
//            // ➕ CREATE BUTTON
//            Button(
//                onClick = { showDialog = true },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = colorResource(id = R.color.sapphire)
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
//                Text("Create", fontSize = 18.sp, fontWeight = FontWeight.Bold)
//            }
//
//            Text(
//                text = "Collaborations",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(12.dp)
//            )
//
//            LazyColumn {
//                items(collaborations) { collab ->
//
//                    val isJoined = collab.joinedUsers.containsKey(userId)
//
//                    CollaborateDesign(
//                        collaboration = collab,
//                        isJoined = isJoined,
//                        onJoinClick = {
//
//                            val updates = hashMapOf<String, Any>(
//                                "joinedUsers/$userId" to true,
//                                "members" to (collab.members + 1)
//                            )
//
//                            dbRef.child(collab.id).updateChildren(updates)
//                        }
//                    )
//                }
//            }
//        }
//    }
//
//    // ➕ CREATE DIALOG
//    if (showDialog) {
//
//        AlertDialog(
//            onDismissRequest = { showDialog = false },
//            confirmButton = {
//                Button(onClick = {
//
//                    val id = dbRef.push().key ?: return@Button
//
//                    val newCollab = Collaboration(
//                        id = id,
//                        name = name,
//                        members = 1,
//                        joinedUsers = mapOf(userId to true)
//                    )
//
//                    dbRef.child(id).setValue(newCollab)
//
//                    name = ""
//                    showDialog = false
//                }) {
//                    Text("Save")
//                }
//            },
//            dismissButton = {
//                Button(onClick = { showDialog = false }) {
//                    Text("Cancel")
//                }
//            },
//            title = { Text("Create Collaboration") },
//            text = {
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = { name = it },
//                    label = { Text("Collaboration Name") }
//                )
//            }
//        )
//    }
//}
