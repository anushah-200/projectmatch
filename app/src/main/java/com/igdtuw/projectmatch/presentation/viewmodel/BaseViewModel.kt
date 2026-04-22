package com.igdtuw.projectmatch.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.igdtuw.projectmatch.models.Message
import com.igdtuw.projectmatch.presentation.homescreen.ChatListModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class BaseViewModel: ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    fun searchUserByEmail(email: String, callback:(ChatListModel?)-> Unit){

        db.child("users")
            .orderByChild("email")
            .equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val user = snapshot.children.first()
                            .getValue(ChatListModel::class.java)
                        callback(user)
                    } else callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BaseViewModel", "Error fetching user: ${error.message}")
                    callback(null)
                }
            })
    }

    private val _chatList = MutableStateFlow<List<ChatListModel>>(emptyList())
    val chatList = _chatList.asStateFlow()

    init {
        loadChatData()
    }

    private fun loadChatData(){

        val currentUserId = auth.currentUser?.uid ?: return

        db.child("messages")
            .child(currentUserId)
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {

                    val chatList = mutableListOf<ChatListModel>()

                    snapshot.children.forEach { child ->

                        val otherUserId = child.key ?: return@forEach

                        // Fetch user details
                        db.child("users").child(otherUserId)
                            .addListenerForSingleValueEvent(object : ValueEventListener{

                                override fun onDataChange(userSnap: DataSnapshot) {

                                    val name = userSnap.child("name").value as? String ?: "Unknown"
                                    val email = userSnap.child("email").value as? String

                                    fetchLastMessageForChat(otherUserId){ lastMsg, time ->

                                        chatList.add(
                                            ChatListModel(
                                                name = name,
                                                userId = otherUserId,
                                                email = email,
                                                message = lastMsg,
                                                time = time
                                            )
                                        )

                                        _chatList.value = chatList
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BaseViewModel", "Error loading chats: ${error.message}")
                }
            })
    }

    fun sendMessage(receiverId: String, messageText: String){

        val senderId = auth.currentUser?.uid ?: return
        val messageId = db.push().key ?: return

        val message = Message(
            senderId = senderId,
            receiverId = receiverId,
            message = messageText,
            timeStamp = System.currentTimeMillis()
        )

        db.child("messages")
            .child(senderId)
            .child(receiverId)
            .child(messageId)
            .setValue(message)

        db.child("messages")
            .child(receiverId)
            .child(senderId)
            .child(messageId)
            .setValue(message)
    }

    fun getMessage(
        receiverId: String,
        onNewMessage: (Message)-> Unit){

        val senderId = auth.currentUser?.uid ?: return

        db.child("messages")
            .child(senderId)
            .child(receiverId)
            .addChildEventListener(object : ChildEventListener{

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)
                    if (message != null){
                        onNewMessage(message)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun fetchLastMessageForChat(
        receiverId: String,
        onLastMessageFetched: (String,String)->Unit
    ){

        val senderId = auth.currentUser?.uid ?: return

        db.child("messages")
            .child(senderId)
            .child(receiverId)
            .orderByChild("timeStamp")
            .limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val last = snapshot.children.first()
                        val msg = last.child("message").value as? String ?: "No message"
                        val time = last.child("timeStamp").value as? Long ?: 0L
                        onLastMessageFetched(msg, time.toString())
                    } else {
                        onLastMessageFetched("No message","--")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onLastMessageFetched("No message","--")
                }
            })
    }
    fun addChat(user: ChatListModel) {
        val currentUserId = auth.currentUser?.uid ?: return
        val otherUserId = user.userId ?: return
        db.child("messages")
            .child(currentUserId)
            .child(otherUserId)
            .setValue(true)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun decodeBase64toBitmap(base64Image: String): Bitmap?{
        return try {
            val decodedByte = Base64.decode(base64Image,android.util.Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedByte,0,decodedByte.size)
        } catch (e: IOException){
            null
        }
    }


    @OptIn(ExperimentalEncodingApi::class)
    fun base64ToBitmap(base64String: String): Bitmap?{
        return try {
            val decodedByte = Base64.decode(base64String,android.util.Base64.DEFAULT)
            val inputStream: InputStream = ByteArrayInputStream(decodedByte)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException){
            null
        }
    }
}