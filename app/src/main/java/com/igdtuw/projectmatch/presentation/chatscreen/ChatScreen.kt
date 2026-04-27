package com.igdtuw.projectmatch.presentation.chatscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.igdtuw.projectmatch.R
import com.igdtuw.projectmatch.models.Message
import com.igdtuw.projectmatch.presentation.imageutils.ImageUtils
import com.igdtuw.projectmatch.presentation.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navHostController: NavHostController,
    email: String,
    baseViewModel: BaseViewModel
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var receiverId by remember { mutableStateOf<String?>(null) }
    var receiverName by remember { mutableStateOf("Chat") }
    var receiverImage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(email) {
        baseViewModel.searchUserByEmail(email) { user ->
            receiverId    = user?.userId
            receiverName  = user?.name ?: "Chat"
            receiverImage = user?.profileImage

            user?.userId?.let { uid ->
                baseViewModel.getMessage(uid) { newMessage ->
                    if (messages.none {
                            it.timeStamp == newMessage.timeStamp &&
                                    it.senderId  == newMessage.senderId
                        }) {
                        messages.add(newMessage)
                        coroutineScope.launch {
                            if (messages.isNotEmpty()) {
                                listState.animateScrollToItem(messages.lastIndex)
                            }
                        }
                    }
                }
            }
        }
    }

    val currentUserId = baseViewModel.getCurrentUserId()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val bitmap = remember(receiverImage) {
                            receiverImage?.let { ImageUtils.base64ToBitmap(it) }
                        }
                        Image(
                            painter = if (bitmap != null) rememberAsyncImagePainter(bitmap)
                            else painterResource(R.drawable.user_placeholder),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text       = receiverName,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp,
                            color      = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor             = colorResource(R.color.light_blue),
                    titleContentColor          = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value         = messageText,
                    onValueChange = { messageText = it },
                    placeholder   = { Text("Type a message…") },
                    modifier      = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF1F1F1),
                        focusedContainerColor   = Color(0xFFF1F1F1),
                        focusedIndicatorColor   = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = false,
                    maxLines   = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val rid = receiverId ?: return@IconButton
                        if (messageText.isNotBlank()) {
                            baseViewModel.sendMessage(rid, messageText.trim())
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = colorResource(R.color.light_blue),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector        = Icons.Default.Send,
                        contentDescription = "Send",
                        tint               = Color.White,
                        modifier           = Modifier.size(22.dp)
                    )
                }
            }
        }
    ) { innerPadding ->

        if (messages.isEmpty()) {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = "No messages yet.\nSay hello! 👋",
                    color    = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                state          = listState,
                modifier       = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages) { msg ->
                    val isMine = msg.senderId == currentUserId
                    MessageBubble(message = msg, isMine = isMine)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message, isMine: Boolean) {
    val bubbleColor = if (isMine) colorResource(R.color.light_blue) else Color(0xFFEEEEEE)
    val textColor   = if (isMine) Color.White else Color.Black
    val alignment   = if (isMine) Alignment.End else Alignment.Start

    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(
                        topStart    = if (isMine) 16.dp else 4.dp,
                        topEnd      = if (isMine) 4.dp else 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd   = 16.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text       = message.message ?: "",
                color      = textColor,
                fontSize   = 15.sp,
                lineHeight = 20.sp
            )
        }
    }
}