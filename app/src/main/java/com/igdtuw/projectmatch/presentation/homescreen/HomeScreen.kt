package com.igdtuw.projectmatch.presentation.homescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.sp
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation


@Composable
@Preview(showSystemUi = true)
fun HomeScreen(){

    val chatData= listOf(
        ChatListModel(
            R.drawable.p1,
            "Susan Connor",
            "10:00AM",
            "Hello"
        ),
        ChatListModel(
            R.drawable.p2,
            "Aston Hawk",
            "7:00AM",
            "Hi"
        ),
        ChatListModel(
            R.drawable.p3,
            "Hugo Clive",
            "9:00AM",
            "Hello"
        ),
        ChatListModel(
            R.drawable.p4,
            "Jiya Singh",
            "3:00PM",
            "Hello"
        ),
        ChatListModel(
            R.drawable.p5,
            "Vinayak Gupta",
            "7:00AM",
            "Hello"
        ),
        ChatListModel(
            R.drawable.p6,
            "David Simon",
            "6:00PM",
            "Hello"
        )

    )

    Scaffold(
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

        bottomBar = {
            BottomNavigation()
        }
    ) {
        Column(modifier = Modifier.padding(it)) {

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()){

                Text(
                    text = "ProjectMatch",
                    fontSize = 28.sp, color = colorResource(R.color.sapphire),
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp),
                    fontWeight = FontWeight.Bold

                )

                Row(modifier = Modifier.align(Alignment.CenterEnd)) {

                    IconButton(onClick = {/*ToDo*/}) {
                        Icon(
                            painter = painterResource(id = R.drawable.search_icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = {/*ToDo*/}) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

            }
            HorizontalDivider()

            LazyColumn() {
                items(chatData){
                        chatItem -> ChatDesign(chatListModel = chatItem)
                }
            }

        }

    }
}