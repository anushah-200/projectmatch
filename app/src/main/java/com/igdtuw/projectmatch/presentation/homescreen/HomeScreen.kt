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
    navHostController : NavHostController,
    homeBaseViewModel : BaseViewModel
) {
    var showPopup   by remember { mutableStateOf(false) }
    var showMenu    by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    var searchText  by remember { mutableStateOf("") }

    val chatData by homeBaseViewModel.chatList.collectAsState()


    LaunchedEffect(Unit) {
        homeBaseViewModel.reloadChats()
    }


    val filteredChats = remember(searchText, chatData) {
        if (searchText.isBlank()) chatData
        else chatData.filter { chat ->
            chat.name?.contains(searchText, ignoreCase = true) == true ||
                    chat.message?.contains(searchText, ignoreCase = true) == true
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { showPopup = true },
                containerColor = colorResource(id = R.color.light_blue),
                contentColor   = Color.White,
                modifier       = Modifier.size(65.dp)
            ) {
                Icon(
                    painter            = painterResource(R.drawable.add_chat_icon),
                    contentDescription = null,
                    modifier           = Modifier.size(20.dp),
                    tint               = Color.White
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
                        value         = searchText,
                        onValueChange = { searchText = it },
                        placeholder   = { Text(text = "Search chats...") },
                        colors        = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor   = Color.Transparent,
                            focusedIndicatorColor   = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier   = Modifier
                            .padding(start = 12.dp)
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(0.8f),
                        singleLine = true
                    )
                } else {
                    Text(
                        text       = "ProjectMatch",
                        fontSize   = 28.sp,
                        color      = colorResource(R.color.sapphire),
                        modifier   = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isSearching) {
                    IconButton(
                        onClick  = {
                            isSearching = false
                            searchText  = ""
                        },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            painter            = painterResource(id = R.drawable.cross),
                            contentDescription = null,
                            modifier           = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(
                                painter            = painterResource(id = R.drawable.search_icon),
                                contentDescription = null,
                                modifier           = Modifier.size(24.dp)
                            )
                        }
                        Box {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Icon(
                                    painter            = painterResource(id = R.drawable.menu_icon),
                                    contentDescription = null,
                                    modifier           = Modifier.size(24.dp)
                                )
                            }
                            DropdownMenu(
                                expanded         = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = "Sign Out") },
                                    leadingIcon = {
                                        Icon(
                                            painter            = painterResource(id = R.drawable.baseline_logout_24),
                                            contentDescription = null,
                                            modifier           = Modifier.size(20.dp)
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
                        showPopup = false
                    },
                    baseViewModel = homeBaseViewModel
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }


            if (chatData.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text     = "No chats yet!",
                            fontSize = 18.sp,
                            color    = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text     = "Tap + to start a conversation\nor explore listings to find collaborators",
                            fontSize = 14.sp,
                            color    = Color.LightGray
                        )
                    }
                }
            } else if (filteredChats.isEmpty() && searchText.isNotBlank()) {

                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text     = "No chats found for \"$searchText\"",
                        color    = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {

                LazyColumn {
                    items(filteredChats) { chat ->
                        ChatDesign(
                            chatListModel = chat,
                            onClick       = {
                                navHostController.navigate(
                                    Routes.ChatScreen.createRoute(email = chat.email ?: "")
                                )
                            },
                            baseViewModel = homeBaseViewModel
                        )
                        HorizontalDivider(
                            color     = Color.LightGray,
                            thickness = 0.5.dp,
                            modifier  = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddUserPopup(
    onDismiss     : () -> Unit,
    onUserAdd     : (ChatListModel) -> Unit,
    baseViewModel : BaseViewModel
) {
    var email     by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var userFound by remember { mutableStateOf<ChatListModel?>(null) }
    var searched  by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        TextField(
            value         = email,
            onValueChange = {
                email     = it
                userFound = null
                searched  = false
            },
            label      = { Text(text = "Enter email address") },
            modifier   = Modifier.fillMaxWidth(),
            singleLine = true,
            colors     = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor   = Color.Transparent,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        isLoading = true
                        searched  = false
                        userFound = null
                        baseViewModel.searchUserByEmail(email.trim()) { user ->
                            isLoading = false
                            userFound = user
                            searched  = true
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.light_blue)
                )
            ) {
                Text(text = "Search")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick  = onDismiss,
                modifier = Modifier.weight(1f),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.light_blue)
                )
            ) {
                Text(text = "Cancel")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color    = colorResource(R.color.light_blue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Searching...", color = Color.Gray)
                }
            }
            searched && userFound != null -> {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    colors    = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.light_blue).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text       = userFound!!.name ?: "Unknown",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp
                            )
                            Text(
                                text     = userFound!!.email ?: "",
                                color    = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                        Button(
                            onClick = {
                                onUserAdd(userFound!!)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.light_blue)
                            )
                        ) {
                            Text(text = "Add")
                        }
                    }
                }
            }
            searched && userFound == null -> {
                Text(
                    text     = "No user found with this email",
                    color    = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
