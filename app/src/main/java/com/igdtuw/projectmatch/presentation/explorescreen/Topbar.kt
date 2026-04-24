package com.igdtuw.projectmatch.presentation.explorescreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.igdtuw.projectmatch.R

@Composable
fun Topbar(
    onSearchQuery: (String) -> Unit = {}   // callback to ExploreScreen
) {
    var isSearching by remember { mutableStateOf(false) }
    var search      by remember { mutableStateOf("") }
    var showMenu    by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSearching) {
                TextField(
                    value         = search,
                    onValueChange = {
                        search = it
                        onSearchQuery(it)   // ✅ send query up to ExploreScreen
                    },
                    placeholder = { Text(text = "Search listings...") },
                    colors      = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor   = Color.Transparent,
                        focusedIndicatorColor   = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier   = Modifier
                        .padding(start = 12.dp)
                        .weight(1f),
                    singleLine = true
                )
            } else {
                Text(
                    text       = "Explore",
                    fontSize   = 28.sp,
                    color      = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier
                        .padding(start = 12.dp)
                        .weight(1f)
                )
            }

            if (isSearching) {
                IconButton(onClick = {
                    isSearching = false
                    search      = ""
                    onSearchQuery("")   // ✅ clear search — show all listings again
                }) {
                    Icon(
                        painter            = painterResource(id = R.drawable.cross),
                        contentDescription = null,
                        modifier           = Modifier.size(15.dp)
                    )
                }
            } else {
                IconButton(onClick = { isSearching = true }) {
                    Icon(
                        painter            = painterResource(id = R.drawable.search_icon),
                        contentDescription = null,
                        modifier           = Modifier.size(24.dp)
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            painter            = painterResource(id = R.drawable.menu_icon),
                            contentDescription = null,
                            modifier           = Modifier.size(24.dp)
                        )
                    }

                    DropdownMenu(
                        expanded          = showMenu,
                        onDismissRequest  = { showMenu = false }
                    ) {
                        // ✅ Sign Out only — same as HomeScreen
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
                                onSearchQuery("__signout__")  // signal to ExploreScreen
                            }
                        )
                    }
                }
            }
        }
        HorizontalDivider()
    }
}