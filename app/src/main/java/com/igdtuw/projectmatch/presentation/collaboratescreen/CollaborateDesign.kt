package com.igdtuw.projectmatch.presentation.collaboratescreen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun CollaborateDesign( collaboration: Collaboration){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(26.dp),
        verticalAlignment = Alignment.CenterVertically)
    {
        Column {
            Text(text = collaboration.name, fontSize = 28.sp,fontWeight= FontWeight.Bold)
            Text(text = collaboration.memberCount, color = Color.Gray, fontSize = 16.sp)
        }

    }

}
data class Collaboration(val name:String, val memberCount: String)