package com.igdtuw.projectmatch.presentation.bottomnavigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.database.snapshot.Index
import com.igdtuw.projectmatch.R

@Composable
fun BottomNavigation(
    navHostController: NavHostController,
    onClick:(Index: Int) -> Unit,
    selectedItem: Int
){

    val items = listOf(
        NavigationItem(name = "Chats", R.drawable.chat_icon, R.drawable.chat_icon),
        NavigationItem(name = "Explore", R.drawable.explore_icon, R.drawable.explore_icon),
        NavigationItem(name = "Community", R.drawable.collaborate_icon, R.drawable.collaborate_icon),
        NavigationItem(name = "Profile", R.drawable.profile_icon, R.drawable.profile_icon)
    )

    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.height(80.dp)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = { onClick(index) },
                label = {
                    Text(
                        text = item.name,
                        color = if (index == selectedItem)
                            colorResource(R.color.sapphire)  // highlighted color
                        else
                            Color.DarkGray,
                        fontWeight = if (index == selectedItem)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                    )
                },
                icon = {
                    Icon(
                        painter = if (index == selectedItem)
                            painterResource(item.selectedIcon)
                        else
                            painterResource(item.unselectedIcon),
                        contentDescription = null,
                        tint = if (index == selectedItem)
                            colorResource(R.color.sapphire)  // highlighted color
                        else
                            Color.DarkGray,
                        modifier = Modifier.size(26.dp)  // fix: was 4.dp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = colorResource(R.color.misty_blue),  // background pill highlight
                    selectedIconColor = colorResource(R.color.sapphire),
                    unselectedIconColor = Color.DarkGray,
                    selectedTextColor = colorResource(R.color.sapphire),
                    unselectedTextColor = Color.DarkGray
                )
            )
        }
    }
}

data class NavigationItem(
    val name: String,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,

    )
//    BottomAppBar(
//        tonalElevation = 12.dp,
//        containerColor = Color.White
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceAround) {
//            Column(
//                modifier = Modifier.padding(horizontal = 16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.chat_icon),
//                    contentDescription = null,
//                    modifier = Modifier.size(28.dp)
//                )
//                Spacer(modifier = Modifier.height(2.dp))
//                Text(
//                    text = "Chats",
//                    fontWeight = FontWeight.Bold
//                )
//            }
//            Column(
//                modifier = Modifier.padding(horizontal = 16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.explore_icon),
//                    contentDescription = null,
//                    modifier = Modifier.size(28.dp)
//                )
//                Spacer(modifier = Modifier.height(2.dp))
//                Text(
//                    text = "Explore",
//                    fontWeight = FontWeight.Bold
//                )
//            }
//            Column(
//                modifier = Modifier.padding(horizontal = 16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.collaborate_icon),
//                    contentDescription = null,
//                    modifier = Modifier.size(28.dp)
//                )
//                Spacer(modifier = Modifier.height(2.dp))
//                Text(
//                    text = "Collaborate",
//                    fontWeight = FontWeight.Bold
//                )
//            }
//            Column(
//                modifier = Modifier.padding(horizontal = 16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.profile_icon),
//                    contentDescription = null,
//                    modifier = Modifier.size(28.dp)
//                )
//                Spacer(modifier = Modifier.height(2.dp))
//                Text(
//                    text = "Profile",
//                    fontWeight = FontWeight.Bold
//                )
//            }
//            /*Column(
//                modifier = Modifier.padding(horizontal = 16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.community_icon),
//                    contentDescription = null,
//                    modifier = Modifier.size(28.dp)
//                )
//                Spacer(modifier = Modifier.height(2.dp))
//                Text(
//                    text = "Community",
//                    fontWeight = FontWeight.Bold
//                )
//            }
//            Column(
//                modifier = Modifier.padding(horizontal = 16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.phone_icon),
//                    contentDescription = null,
//                    modifier = Modifier.size(28.dp)
//                )
//                Spacer(modifier = Modifier.height(2.dp))
//                Text(
//                    text = "Calls",
//                    fontWeight = FontWeight.Bold
//                )
//            }*/
//        }
//
//    }
//
//}