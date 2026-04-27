package com.igdtuw.projectmatch.presentation.explorescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.igdtuw.projectmatch.R

@Composable
@Preview(showSystemUi = false)
fun AddListings(){

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.p1),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp).clip(shape = CircleShape),
                    contentScale = ContentScale.Crop
                )
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .background(
                            color = colorResource(id = R.color.sapphire),
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Add My Listings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Tap to add and view listings",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray
        )



    }
}


