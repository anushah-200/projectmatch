package com.igdtuw.projectmatch.presentation.homescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.presentation.imageutils.ImageUtils
import com.igdtuw.projectmatch.presentation.viewmodel.BaseViewModel

@Composable
fun ChatDesign(
    chatListModel: ChatListModel,
    onClick:()-> Unit,
    baseViewModel: BaseViewModel
){

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val profileImage = chatListModel?.profileImage
        val bitmap = remember {
            profileImage?.let { ImageUtils.base64ToBitmap(it) }
        }
        Image(
            painter = if (bitmap!= null){
                rememberImagePainter(bitmap)
            }else{
                painterResource(R.drawable.user_placeholder)

            },
            contentDescription = null,
            modifier = Modifier.size(60.dp).clip(shape = CircleShape)
                .background(color = Color.Gray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column() {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = chatListModel.name?:"Unknown",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chatListModel.time?:"--:--",
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chatListModel.message?:"message",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

}