package com.igdtuw.projectmatch.presentation.bottomnavigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.navigation.Routes

@Composable
fun BottomNavigation(
    navHostController: NavHostController
) {
    val items = listOf(
        NavigationItem("Chats",     Routes.HomeScreen,           R.drawable.chat_icon,        R.drawable.chat_icon),
        NavigationItem("Explore",   Routes.ExploreScreen,        R.drawable.explore_icon,     R.drawable.explore_icon),
        NavigationItem("Community", Routes.CollaborateScreen,    R.drawable.collaborate_icon, R.drawable.collaborate_icon),
        NavigationItem("Profile",   Routes.ProfileDisplayScreen, R.drawable.profile_icon,     R.drawable.profile_icon)  // ✅ changed
    )

    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        modifier       = Modifier.height(80.dp)
    ) {
        items.forEach { item ->
            val itemRoute  = item.route::class.qualifiedName
            val isSelected = currentRoute == itemRoute

            NavigationBarItem(
                selected = isSelected,
                onClick  = {
                    navHostController.navigate(item.route) {
                        popUpTo(navHostController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                label = {
                    Text(
                        text       = item.name,
                        color      = if (isSelected) colorResource(R.color.sapphire) else Color.DarkGray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = {
                    Icon(
                        painter            = painterResource(if (isSelected) item.selectedIcon else item.unselectedIcon),
                        contentDescription = item.name,
                        tint               = if (isSelected) colorResource(R.color.sapphire) else Color.DarkGray,
                        modifier           = Modifier.size(26.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor      = colorResource(R.color.misty_blue),
                    selectedIconColor   = colorResource(R.color.sapphire),
                    unselectedIconColor = Color.DarkGray,
                    selectedTextColor   = colorResource(R.color.sapphire),
                    unselectedTextColor = Color.DarkGray
                )
            )
        }
    }
}

data class NavigationItem(
    val name         : String,
    val route        : Routes,
    @DrawableRes val selectedIcon   : Int,
    @DrawableRes val unselectedIcon : Int
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