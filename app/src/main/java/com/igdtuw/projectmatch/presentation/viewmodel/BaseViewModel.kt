package com.igdtuw.projectmatch.presentation.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.igdtuw.projectmatch.models.Message
import com.igdtuw.projectmatch.presentation.homescreen.ChatListModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import javax.inject.Inject
import kotlin.io.encoding.ExperimentalEncodingApi

@HiltViewModel
class BaseViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    // ─── Chat List (HomeScreen) ───────────────────────────────────────────────
    private val _chatList = MutableStateFlow<List<ChatListModel>>(emptyList())
    val chatList = _chatList.asStateFlow()

    // ─── All Users (ExploreScreen) ────────────────────────────────────────────
    private val _allUsers = MutableStateFlow<List<ChatListModel>>(emptyList())
    val allUsers = _allUsers.asStateFlow()

    init {
        loadChatData()
    }

    // ─── Explore: fetch every user except self ────────────────────────────────
    fun fetchAllUsers() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.child("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = mutableListOf<ChatListModel>()
                    snapshot.children.forEach { child ->
                        val uid = child.key ?: return@forEach
                        if (uid == currentUserId) return@forEach

                        val name         = child.child("name").value as? String ?: "Unknown"
                        val email        = child.child("email").value as? String
                        val skills       = child.child("skills").value as? String
                        val profileImage = child.child("profileImage").value as? String

                        users.add(
                            ChatListModel(
                                name         = name,
                                userId       = uid,
                                email        = email,
                                profileImage = profileImage,
                                message      = skills
                            )
                        )
                    }
                    _allUsers.value = users
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BaseViewModel", "fetchAllUsers: ${error.message}")
                }
            })
    }

    // ─── Home: load existing chats from "chats" node ─────────────────────────
    private fun loadChatData() {
        val currentUserId = auth.currentUser?.uid ?: return

        // Read from "chats" node (not "messages")
        db.child("chats")
            .child(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = mutableListOf<ChatListModel>()

                    snapshot.children.forEach { child ->
                        val otherUserId = child.key ?: return@forEach

                        db.child("users").child(otherUserId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnap: DataSnapshot) {
                                    val name         = userSnap.child("name").value as? String ?: "Unknown"
                                    val email        = userSnap.child("email").value as? String
                                    val profileImage = userSnap.child("profileImage").value as? String

                                    fetchLastMessageForChat(otherUserId) { lastMsg, time ->
                                        val updated = chatList.toMutableList()
                                        updated.removeAll { it.userId == otherUserId }
                                        updated.add(
                                            ChatListModel(
                                                name         = name,
                                                userId       = otherUserId,
                                                email        = email,
                                                profileImage = profileImage,
                                                message      = lastMsg,
                                                time         = time
                                            )
                                        )
                                        _chatList.value = updated
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BaseViewModel", "loadChatData: ${error.message}")
                }
            })
    }

    // ─── Add user to chat list — writes to "chats" node only ─────────────────
    fun addChat(user: ChatListModel) {
        val currentUserId = auth.currentUser?.uid ?: return
        val otherUserId   = user.userId ?: return

        // Only track the relationship in "chats", not "messages"
        db.child("chats")
            .child(currentUserId)
            .child(otherUserId)
            .setValue(true)

        db.child("chats")
            .child(otherUserId)
            .child(currentUserId)
            .setValue(true)
    }

    // ─── Send a message — writes to "messages" node ───────────────────────────
    fun sendMessage(receiverId: String, messageText: String) {
        val senderId  = auth.currentUser?.uid ?: return
        val messageId = db.push().key ?: return

        val message = Message(
            senderId   = senderId,
            receiverId = receiverId,
            message    = messageText,
            timeStamp  = System.currentTimeMillis()
        )

        db.child("messages").child(senderId).child(receiverId).child(messageId).setValue(message)
        db.child("messages").child(receiverId).child(senderId).child(messageId).setValue(message)
    }

    // ─── Listen for new messages — skips non-Message nodes ───────────────────
    fun getMessage(
        receiverId: String,
        onNewMessage: (Message) -> Unit
    ) {
        val senderId = auth.currentUser?.uid ?: return

        db.child("messages")
            .child(senderId)
            .child(receiverId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
                        // Skip any boolean/init nodes
                        if (snapshot.key == "__init__") return
                        val message = snapshot.getValue(Message::class.java)
                        if (message != null && message.senderId != null) {
                            onNewMessage(message)
                        }
                    } catch (e: Exception) {
                        Log.e("BaseViewModel", "getMessage skip bad node: ${e.message}")
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // ─── Fetch last message preview ───────────────────────────────────────────
    fun fetchLastMessageForChat(
        receiverId: String,
        onLastMessageFetched: (String, String) -> Unit
    ) {
        val senderId = auth.currentUser?.uid ?: return

        db.child("messages")
            .child(senderId)
            .child(receiverId)
            .orderByChild("timeStamp")
            .limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val last = snapshot.children.first()
                        val msg  = last.child("message").value as? String ?: ""
                        val time = last.child("timeStamp").value as? Long ?: 0L
                        onLastMessageFetched(msg, formatTime(time))
                    } else {
                        onLastMessageFetched("", "--")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onLastMessageFetched("", "--")
                }
            })
    }

    // ─── Search user by email ─────────────────────────────────────────────────
    fun searchUserByEmail(email: String, callback: (ChatListModel?) -> Unit) {
        db.child("users")
            .orderByChild("email")
            .equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val child = snapshot.children.first()
                        val user = ChatListModel(
                            name         = child.child("name").value as? String,
                            userId       = child.key,
                            email        = child.child("email").value as? String,
                            profileImage = child.child("profileImage").value as? String
                        )
                        callback(user)
                    } else callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BaseViewModel", "searchUserByEmail: ${error.message}")
                    callback(null)
                }
            })
    }

    // ─── Get current user UID ─────────────────────────────────────────────────
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // ─── Format timestamp ─────────────────────────────────────────────────────
    private fun formatTime(timestamp: Long): String {
        if (timestamp == 0L) return "--"
        val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}

//
//    @OptIn(ExperimentalEncodingApi::class)
//    fun base64ToBitmap(base64String: String): Bitmap?{
//        return try {
//            val decodedByte = Base64.decode(base64String,android.util.Base64.DEFAULT)
//            val inputStream: InputStream = ByteArrayInputStream(decodedByte)
//            BitmapFactory.decodeStream(inputStream)
//        } catch (e: IOException){
//            null
//        }
//    }
