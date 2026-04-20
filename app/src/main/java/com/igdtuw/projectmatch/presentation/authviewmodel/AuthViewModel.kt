package com.igdtuw.projectmatch.presentation.authviewmodel

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.igdtuw.projectmatch.models.EmailAuthUser
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

    fun saveUserProfile(name: String, skills: String,profileImage: Bitmap?){

        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid
        val email = currentUser.email ?: ""
        val encodedImage= profileImage?.let { covertBitmapToBase64(it) }

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

    }

    fun covertBitmapToBase64(bitmap:Bitmap): String{
        val byteArrayOutputStream= ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray=byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


}

sealed class AuthState{
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    object Loading: AuthState()
    object InitialLoading: AuthState()
    data class Error(val message: String): AuthState()
}