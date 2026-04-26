package com.igdtuw.projectmatch.presentation.explorescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.models.Listing
import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation
import com.igdtuw.projectmatch.presentation.homescreen.ChatListModel
import com.igdtuw.projectmatch.presentation.navigation.Routes
import com.igdtuw.projectmatch.presentation.viewmodel.BaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navHostController: NavHostController
) {
    val baseViewModel: BaseViewModel = hiltViewModel()
    val myListings  by baseViewModel.myListings.collectAsState()
    val allListings by baseViewModel.allListings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var showMenu    by remember { mutableStateOf(false) }

    val filteredListings = remember(searchQuery, allListings) {
        if (searchQuery.isBlank()) allListings
        else allListings.filter { listing ->
            listing.projectName?.contains(searchQuery, ignoreCase = true) == true ||
                    listing.skillsNeeded?.contains(searchQuery, ignoreCase = true) == true ||
                    listing.role?.contains(searchQuery, ignoreCase = true) == true ||
                    listing.userName?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    LaunchedEffect(Unit) {
        baseViewModel.fetchMyListings()
        baseViewModel.fetchAllListings()
    }

    Scaffold(
        bottomBar = { BottomNavigation(navHostController = navHostController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { navHostController.navigate(Routes.AddListingScreen) },
                containerColor = colorResource(R.color.light_blue),
                contentColor   = Color.White,
                modifier       = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Add Listing",
                    modifier           = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            // ── Top Bar (mirrors HomeScreen exactly) ──────────────────────
            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                if (isSearching) {
                    TextField(
                        value         = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder   = { Text(text = "Search listings...") },
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
                    IconButton(
                        onClick  = {
                            isSearching = false
                            searchQuery = ""
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
                    Text(
                        text       = "Explore",
                        fontSize   = 28.sp,
                        color      = colorResource(R.color.sapphire),
                        fontWeight = FontWeight.Bold,
                        modifier   = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp)
                    )
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

            // ── Scrollable body ───────────────────────────────────────────
            LazyColumn(
                modifier       = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {

                if (searchQuery.isBlank()) {
                    item {
                        Text(
                            text       = "My Listings :",
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.Black,
                            modifier   = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
                        )
                    }

                    if (myListings.isEmpty()) {
                        item {
                            Box(
                                modifier         = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text     = "No listings yet. Tap + below to add one!",
                                    color    = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(myListings) { listing ->
                            MyListingCard(
                                listing  = listing,
                                onDelete = {
                                    baseViewModel.deleteListing(listing.listingId ?: "")
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    item {
                        HorizontalDivider(
                            modifier  = Modifier.padding(vertical = 12.dp),
                            color     = Color.LightGray,
                            thickness = 1.dp
                        )
                    }
                }

                item {
                    Text(
                        text       = if (searchQuery.isBlank()) "Listings :"
                        else "Results for \"$searchQuery\" :",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.Black,
                        modifier   = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (filteredListings.isEmpty()) {
                    item {
                        Box(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text     = if (searchQuery.isBlank()) "No listings from others yet."
                                else "No listings found for \"$searchQuery\"",
                                color    = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(filteredListings) { listing ->
                        OtherListingCard(
                            listing     = listing,
                            onChatClick = {
                                val user = ChatListModel(
                                    userId = listing.userId,
                                    email  = listing.userEmail,
                                    name   = listing.userName
                                )
                                baseViewModel.addChat(user)
                                navHostController.navigate(
                                    Routes.ChatScreen.createRoute(
                                        email = listing.userEmail ?: ""
                                    )
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    /*Scaffold(
        bottomBar = { BottomNavigation(navHostController = navHostController) },
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    if (isSearching) {
                        // ── Search mode ───────────────────────────────────────
                        TextField(
                            value         = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder   = { Text(text = "Search listings...") },
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
                        IconButton(
                            onClick  = {
                                isSearching = false
                                searchQuery = ""
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
                        // ── Normal mode ───────────────────────────────────────
                        Text(
                            text       = "Explore",
                            fontSize   = 28.sp,
                            color      = colorResource(R.color.sapphire),
                            modifier   = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 16.dp),
                            fontWeight = FontWeight.Bold
                        )
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
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { navHostController.navigate(Routes.AddListingScreen) },
                containerColor = colorResource(R.color.light_blue),
                contentColor   = Color.White,
                modifier       = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = "Add Listing",
                    modifier           = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // ── My Listings section ───────────────────────────────────────────
            if (searchQuery.isBlank()) {

                item {
                    Text(
                        text       = "My Listings :",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.Black,
                        modifier   = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
                    )
                }

                if (myListings.isEmpty()) {
                    item {
                        Box(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text     = "No listings yet. Tap + below to add one!",
                                color    = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(myListings) { listing ->
                        MyListingCard(
                            listing  = listing,
                            onDelete = {
                                baseViewModel.deleteListing(listing.listingId ?: "")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                item {
                    HorizontalDivider(
                        modifier  = Modifier.padding(vertical = 12.dp),
                        color     = Color.LightGray,
                        thickness = 1.dp
                    )
                }
            }

            // ── All Listings header ───────────────────────────────────────────
            item {
                Text(
                    text       = if (searchQuery.isBlank()) "Listings :"
                    else "Results for \"$searchQuery\" :",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.Black,
                    modifier   = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ── All Listings cards ────────────────────────────────────────────
            if (filteredListings.isEmpty()) {
                item {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text     = if (searchQuery.isBlank()) "No listings from others yet."
                            else "No listings found for \"$searchQuery\"",
                            color    = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredListings) { listing ->
                    OtherListingCard(
                        listing     = listing,
                        onChatClick = {
                            val user = ChatListModel(
                                userId = listing.userId,
                                email  = listing.userEmail,
                                name   = listing.userName
                            )
                            baseViewModel.addChat(user)
                            navHostController.navigate(
                                Routes.ChatScreen.createRoute(
                                    email = listing.userEmail ?: ""
                                )
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }*/
}

// ── My own listing card with delete ──────────────────────────────────────────
@Composable
fun MyListingCard(
    listing  : Listing,
    onDelete : () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title            = { Text("Delete Listing") },
            text             = { Text("Are you sure you want to delete \"${listing.projectName}\"?") },
            confirmButton    = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(
            containerColor = colorResource(R.color.light_blue).copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = listing.projectName ?: "Untitled Project",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = Color.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text     = "Skills: ${listing.skillsNeeded ?: "-"}",
                    fontSize = 13.sp,
                    color    = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = "Role: ${listing.role ?: "-"}",
                    fontSize = 13.sp,
                    color    = Color.DarkGray
                )
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = "Delete Listing",
                    tint               = Color.Red.copy(alpha = 0.7f),
                    modifier           = Modifier.size(22.dp)
                )
            }
        }
    }
}

// ── Other user's listing card with Chat icon ──────────────────────────────────
@Composable
fun OtherListingCard(
    listing     : Listing,
    onChatClick : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = listing.projectName ?: "Untitled Project",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = "Skills: ${listing.skillsNeeded ?: "-"}",
                    fontSize = 13.sp,
                    color    = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = "Role: ${listing.role ?: "-"}",
                    fontSize = 13.sp,
                    color    = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = "By: ${listing.userName ?: "Unknown"}",
                    fontSize = 12.sp,
                    color    = Color.LightGray
                )
            }
            IconButton(
                onClick  = onChatClick,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = colorResource(R.color.light_blue),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter            = painterResource(R.drawable.add_chat_icon),
                    contentDescription = "Chat",
                    tint               = Color.White,
                    modifier           = Modifier.size(20.dp)
                )
            }
        }
    }
}