package com.igdtuw.projectmatch.presentation.homescreen

import androidx.compose.foundation.background
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
import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation
import com.igdtuw.projectmatch.presentation.viewmodel.BaseViewModel
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.navigation.Routes

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    homeBaseViewModel: BaseViewModel
) {
    var showPopup by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val chatData by homeBaseViewModel.chatList.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPopup = true },
                containerColor = colorResource(id = R.color.light_blue),
                contentColor = Color.White,
                modifier = Modifier.size(65.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_chat_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        },
        bottomBar = {
            BottomNavigation(navHostController = navHostController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(color = Color.White)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                if (isSearching) {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text(text = "Search") },
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
                        text = "ProjectMatch",
                        fontSize = 28.sp,
                        color = colorResource(R.color.sapphire),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp),
                        fontWeight = FontWeight.Bold
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
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.search_icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Box {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.menu_icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = "Sign Out") },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_logout_24),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
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
            Spacer(modifier = Modifier.height(12.dp))

            if (showPopup) {
                AddUserPopup(
                    onDismiss = { showPopup = false },
                    onUserAdd = { newUser ->
                        homeBaseViewModel.addChat(newUser)
                    },
                    baseViewModel = homeBaseViewModel
                )
            }

            LazyColumn {
                items(chatData) { chat ->
                    ChatDesign(
                        chatListModel = chat,
                        onClick = {
                            navHostController.navigate(
                                Routes.ChatScreen.createRoute(email = chat.email ?: "")
                            )
                        },
                        baseViewModel = homeBaseViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AddUserPopup(
    onDismiss: () -> Unit,
    onUserAdd: (ChatListModel) -> Unit,
    baseViewModel: BaseViewModel
) {
    var email by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var userFound by remember { mutableStateOf<ChatListModel?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Enter email address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Row {
            Button(
                onClick = {
                    isSearching = true
                    baseViewModel.searchUserByEmail(email) { user ->
                        isSearching = false
                        userFound = user
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.light_blue))
            ) {
                Text(text = "Search")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.light_blue))
            ) {
                Text(text = "Cancel")
            }
        }

        if (isSearching) {
            Text(text = "Searching...", color = Color.Gray)
        }

        userFound?.let { user ->
            Column {
                Text(text = "User Found: ${user.name}")
                Button(
                    onClick = {
                        onUserAdd(user)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.light_blue))
                ) {
                    Text(text = "Add to Chat")
                }
            }
        } ?: run {
            if (!isSearching) {
                Text(text = "No user found with this email id", color = Color.Gray)
            }
        }
    }
}

//    val chatData= listOf(
//        ChatListModel(
//            R.drawable.p1,
//            "Susan Connor",
//            "10:00AM",
//            "Hello"
//        ),
//        ChatListModel(
//            R.drawable.p2,
//            "Aston Hawk",
//            "7:00AM",
//            "Hi"
//        ),
//        ChatListModel(
//            R.drawable.p3,
//            "Hugo Clive",
//            "9:00AM",
//            "Hello"
//        ),
//        ChatListModel(
//            R.drawable.p4,
//            "Jiya Singh",
//            "3:00PM",
//            "Hello"
//        ),
//        ChatListModel(
//            R.drawable.p5,
//            "Vinayak Gupta",
//            "7:00AM",
//            "Hello"
//        ),
//        ChatListModel(
//            R.drawable.p6,
//            "David Simon",
//            "6:00PM",
//            "Hello"
//        )
//
//    )

//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = {/*ToDo*/},
//                containerColor = colorResource(R.color.sapphire),
//                modifier = Modifier.size(65.dp),
//                contentColor = Color.White
//            ) {
//
//                Icon(
//                    painter = painterResource(id = R.drawable.plus_icon),
//                    contentDescription = null,
//                    modifier = Modifier.size(28.dp))
//            }
//        },

//        bottomBar = {
//            BottomNavigation()
//        }
//    ) {
//        Column(modifier = Modifier.padding(it)) {
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Box(modifier = Modifier.fillMaxWidth()){
//
//                Text(
//                    text = "ProjectMatch",
//                    fontSize = 28.sp, color = colorResource(R.color.sapphire),
//                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp),
//                    fontWeight = FontWeight.Bold
//
//                )
//
//                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
//
//                    IconButton(onClick = {/*ToDo*/}) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.search_icon),
//                            contentDescription = null,
//                            modifier = Modifier.size(24.dp)
//                        )
//                    }
//                    IconButton(onClick = {/*ToDo*/}) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.menu_icon),
//                            contentDescription = null,
//                            modifier = Modifier.size(24.dp)
//                        )
//                    }
//                }
//
//            }
//            HorizontalDivider()

//            LazyColumn() {
//                items(chatData){
//                        chatItem -> ChatDesign(chatListModel = chatItem)
//                }
//            }
//
//        }
//
//    }


//}