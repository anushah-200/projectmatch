package com.igdtuw.projectmatch.presentation.authviewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()


    fun getGoogleClient(context: Context): GoogleSignInClient {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1:148442190177:android:1ff11efac210aad81f7520")
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, googleSignInOptions)
    }
    fun firebaseAuthWithGoogle(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val email = auth.currentUser?.email ?: ""

                    if (email.endsWith("@igdtuw.ac.in")) {
                        onSuccess()
                    } else {
                        auth.signOut()
                        onError("Use IGDTUW email only")
                    }

                } else {
                    onError("Authentication failed")
                }
            }
    }
}