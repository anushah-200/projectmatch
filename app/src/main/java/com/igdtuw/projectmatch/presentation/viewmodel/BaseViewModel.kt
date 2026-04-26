package com.igdtuw.projectmatch.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.igdtuw.projectmatch.models.Listing
import com.igdtuw.projectmatch.models.Message
import com.igdtuw.projectmatch.presentation.homescreen.ChatListModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseDatabase.getInstance().reference

    // ─── Chat List (HomeScreen) ───────────────────────────────────────────────
    private val _chatList = MutableStateFlow<List<ChatListModel>>(emptyList())
    val chatList = _chatList.asStateFlow()

    // ─── All Users (ExploreScreen) ────────────────────────────────────────────
    private val _allUsers = MutableStateFlow<List<ChatListModel>>(emptyList())
    val allUsers = _allUsers.asStateFlow()

    // ─── Listings ─────────────────────────────────────────────────────────────
    private val _myListings  = MutableStateFlow<List<Listing>>(emptyList())
    val myListings = _myListings.asStateFlow()

    private val _allListings = MutableStateFlow<List<Listing>>(emptyList())
    val allListings = _allListings.asStateFlow()

    init {
        loadChatData()
    }

    // ─── keep loadChatData pointing to reloadChats ────────────────────────────
    private fun loadChatData() {
        reloadChats()
    }

    // ─── Main chat loader — WhatsApp style ────────────────────────────────────
    fun reloadChats() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.child("chats")
            .child(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (!snapshot.exists()) {
                        _chatList.value = emptyList()
                        return
                    }

                    val resultList   = mutableListOf<ChatListModel>()
                    var pendingCount = snapshot.childrenCount.toInt()

                    if (pendingCount == 0) {
                        _chatList.value = emptyList()
                        return
                    }

                    snapshot.children.forEach { child ->
                        val otherUserId = child.key ?: run {
                            pendingCount--
                            if (pendingCount == 0) _chatList.value = resultList
                            return@forEach
                        }

                        // ── Step 1: fetch user details ────────────────────────
                        db.child("users").child(otherUserId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnap: DataSnapshot) {
                                    val name         = userSnap.child("name").value as? String ?: "Unknown"
                                    val email        = userSnap.child("email").value as? String ?: ""
                                    val profileImage = userSnap.child("profileImage").value as? String

                                    // ── Step 2: fetch last message ────────────
                                    db.child("messages")
                                        .child(currentUserId)
                                        .child(otherUserId)
                                        .orderByChild("timeStamp")
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(msgSnap: DataSnapshot) {
                                                var lastMsg  = ""
                                                var lastTime = "--"
                                                var rawTime  = 0L

                                                if (msgSnap.exists()) {
                                                    msgSnap.children.forEach { msgChild ->
                                                        // skip boolean true nodes
                                                        val msg  = msgChild.child("message").value as? String ?: ""
                                                        val time = msgChild.child("timeStamp").value as? Long ?: 0L
                                                        if (msg.isNotEmpty()) {
                                                            lastMsg  = msg
                                                            rawTime  = time
                                                            lastTime = formatTime(time)
                                                        }
                                                    }
                                                }

                                                // ── Step 3: add to list ───────
                                                synchronized(resultList) {
                                                    resultList.removeAll { it.userId == otherUserId }
                                                    resultList.add(
                                                        ChatListModel(
                                                            name         = name,
                                                            userId       = otherUserId,
                                                            email        = email,
                                                            profileImage = profileImage,
                                                            message      = lastMsg,
                                                            time         = lastTime,
                                                            timeStamp    = rawTime
                                                        )
                                                    )
                                                    pendingCount--
                                                    if (pendingCount == 0) {
                                                        // ✅ sort by raw timestamp — latest first
                                                        _chatList.value = resultList
                                                            .sortedByDescending { it.timeStamp }
                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                synchronized(resultList) {
                                                    pendingCount--
                                                    if (pendingCount == 0) {
                                                        _chatList.value = resultList
                                                            .sortedByDescending { it.timeStamp }
                                                    }
                                                }
                                            }
                                        })
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    synchronized(resultList) {
                                        pendingCount--
                                        if (pendingCount == 0) {
                                            _chatList.value = resultList
                                                .sortedByDescending { it.timeStamp }
                                        }
                                    }
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BaseViewModel", "reloadChats: ${error.message}")
                }
            })
    }

    // ─── Add user to chat list ────────────────────────────────────────────────
    fun addChat(user: ChatListModel) {
        val currentUserId = auth.currentUser?.uid ?: return
        val otherUserId   = user.userId ?: return

        db.child("chats").child(currentUserId).child(otherUserId).setValue(true)
        db.child("chats").child(otherUserId).child(currentUserId).setValue(true)

        reloadChats()
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

    // ─── Add listing ──────────────────────────────────────────────────────────
    fun addListing(
        projectName  : String,
        skillsNeeded : String,
        role         : String,
        onSuccess    : () -> Unit,
        onError      : (String) -> Unit
    ) {
        val currentUser = auth.currentUser ?: return
        val listingId   = db.push().key ?: return

        db.child("users").child(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName  = snapshot.child("name").value as? String ?: "Unknown"
                    val userEmail = snapshot.child("email").value as? String ?: ""

                    val listing = Listing(
                        listingId    = listingId,
                        userId       = currentUser.uid,
                        userName     = userName,
                        userEmail    = userEmail,
                        projectName  = projectName,
                        skillsNeeded = skillsNeeded,
                        role         = role
                    )

                    db.child("listings")
                        .child(listingId)
                        .setValue(listing)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onError(it.message ?: "Failed to post listing") }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    // ─── Fetch my listings ────────────────────────────────────────────────────
    fun fetchMyListings() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.child("listings")
            .orderByChild("userId")
            .equalTo(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull {
                        it.getValue(Listing::class.java)
                    }
                    _myListings.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BaseViewModel", "fetchMyListings: ${error.message}")
                }
            })
    }

    // ─── Fetch all other users listings ──────────────────────────────────────
    fun fetchAllListings() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.child("listings")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children
                        .mapNotNull { it.getValue(Listing::class.java) }
                        .filter { it.userId != currentUserId }
                    _allListings.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BaseViewModel", "fetchAllListings: ${error.message}")
                }
            })
    }

    // ─── Delete a listing ─────────────────────────────────────────────────────
    fun deleteListing(listingId: String) {
        if (listingId.isBlank()) return
        db.child("listings")
            .child(listingId)
            .removeValue()
            .addOnFailureListener {
                Log.e("BaseViewModel", "deleteListing error: ${it.message}")
            }
    }

    // ─── Send a message ───────────────────────────────────────────────────────
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

    // ─── Listen for new messages ──────────────────────────────────────────────
    fun getMessage(
        receiverId   : String,
        onNewMessage : (Message) -> Unit
    ) {
        val senderId = auth.currentUser?.uid ?: return

        db.child("messages")
            .child(senderId)
            .child(receiverId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    try {
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
        receiverId           : String,
        onLastMessageFetched : (String, String) -> Unit
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
                        val user  = ChatListModel(
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
