package com.igdtuw.projectmatch.presentation.viewmodel

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.igdtuw.projectmatch.models.EmailAuthUser
import com.igdtuw.projectmatch.presentation.imageutils.ImageUtils
import java.io.ByteArrayOutputStream

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val _authState= MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get()= _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        _authState.value = AuthState.InitialLoading
        if (auth.currentUser==null){
            _authState.value= AuthState.Unauthenticated
        }else{
            _authState.value=AuthState.Authenticated
        }
    }
    fun resetToUnauthenticated() {
        _authState.value = AuthState.Unauthenticated
    }
    fun login(email: String,password: String){
        if (email.isEmpty() || password.isEmpty()){
            _authState.value= AuthState.Error("Email or Password can't be empty")
            return
        }
        _authState.value= AuthState.Loading
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    _authState.value= AuthState.Authenticated
                }else{
                    _authState.value= AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }

    fun signin(email: String,password: String){
        if (email.isEmpty() || password.isEmpty()){
            _authState.value= AuthState.Error("Email or Password can't be empty")
            return
        }
        _authState.value= AuthState.Loading
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    _authState.value= AuthState.Authenticated
                }else{
                    val message = if (task.exception?.message?.contains("already in use") == true) {
                        "An account with this email already exists. Please login instead."
                    } else {
                        task.exception?.message ?: "Something went wrong"
                    }
                    _authState.value = AuthState.Error(message)
                }
            }
    }

    fun signout(){
        auth.signOut()
        _authState.value= AuthState.Unauthenticated
    }

    fun saveUserProfile(
        name: String,
        skills: String,
        profileImage: Bitmap?,
        onSuccess: () -> Unit){

        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid
        val email = currentUser.email ?: ""
        val encodedImage= profileImage?.let { ImageUtils.bitmapToBase64(it) }

        val user = EmailAuthUser(
            uid = uid,
            name = name,
            email = email,
            profileImage = encodedImage,
            skills = skills
        )

        database.getReference("users")
            .child(uid)
            .setValue(user)
            .addOnSuccessListener {
                onSuccess()
            }

    }

//    fun convertBitmapToBase64(bitmap:Bitmap): String{
//        val byteArrayOutputStream= ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
//        val byteArray=byteArrayOutputStream.toByteArray()
//        return Base64.encodeToString(byteArray, Base64.DEFAULT)
//    }

    private val _userData = MutableLiveData<EmailAuthUser?>()
    val userData: LiveData<EmailAuthUser?> = _userData

    fun fetchUserProfile(){

        val uid = auth.currentUser?.uid ?: return

        database.getReference("users")
            .child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(EmailAuthUser::class.java)
                    _userData.value = user
                }

                override fun onCancelled(error: DatabaseError) {
                    _userData.value = null
                }
            })
    }

    fun updateUserProfile(
        name     : String,
        skills   : String,
        onSuccess: () -> Unit,
        onError  : (String) -> Unit
    ) {
        val currentUser = auth.currentUser ?: return
        val uid         = currentUser.uid

        val updates = mapOf(
            "name"   to name,
            "skills" to skills
        )

        database.getReference("users")
            .child(uid)
            .updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Update failed") }
    }
}

sealed class AuthState{
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Loading: AuthState()
    object InitialLoading: AuthState()
    data class Error(val message: String): AuthState()
}

