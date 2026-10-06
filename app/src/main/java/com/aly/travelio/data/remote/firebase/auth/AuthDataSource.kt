package com.aly.travelio.data.remote.firebase.auth

import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.toFireBaseFailure
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {

    suspend fun login(email: String, pass: String): FirebaseUser? {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            result.user
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun register(email: String, pass: String): FirebaseUser? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            result.user
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun signInWithGoogle(idToken: String): ResultCallBack<AuthResult> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            ResultCallBack.Success(result)
        } catch (e: Exception) {
            e.toFireBaseFailure()
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun resetPassword(email: String) : Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            throw e
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
