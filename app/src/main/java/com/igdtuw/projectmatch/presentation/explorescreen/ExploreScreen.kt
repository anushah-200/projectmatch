package com.igdtuw.projectmatch.presentation.explorescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.bottomnavigation.BottomNavigation

@Composable
@Preview(showSystemUi = true)
fun ExploreScreen() {

        val scrollState = rememberScrollState()
    val sampleListings = listOf(
            ListingData(
                image = R.drawable.p6,
                name = "Mathew",
                list = "#frontend #cpp #kotlin"
            ),
            ListingData(
                 image = R.drawable.p5,
                 name = "Anthony",
                 list = "#backend #python"
            ),
            ListingData(
                image = R.drawable.img,
                name = "Sarah",
                list = "#backend #ai and ml"
        )
    )



    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = { /*TODO()*/ },
//                containerColor = colorResource(id = R.color.light_blue),
//                modifier = Modifier.size(65.dp)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.plus_icon),
//                    contentDescription = null
//                )
//            }
//        },
        bottomBar = { BottomNavigation() },
        topBar = { Topbar() }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {


            Text(
                text = "My Listings:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            AddListings()


            Text(
                text = "Listings :",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            sampleListings.forEach {
                Listings(listingData = it)
            }

        }
    }
}