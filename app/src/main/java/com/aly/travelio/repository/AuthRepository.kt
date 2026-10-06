package com.aly.travelio.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.aly.travelio.R
import com.aly.travelio.data.remote.firebase.auth.AuthDataSource
import com.aly.travelio.data.remote.firebase.firestore.FirestoreDataSource
import com.aly.travelio.datastore.AppDataStore
import com.aly.travelio.model.User
import com.aly.travelio.util.NetworkError
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.toFireBaseFailure
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    private val appDataStore: AppDataStore
) {

    suspend fun login(email: String, pass: String): ResultCallBack<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val user = authDataSource.login(email, pass)
            if (user != null) {
                appDataStore.setLoggedIn(true)
                appDataStore.setUserId(user.uid)
                ResultCallBack.Success(user)
            } else {
                Exception("Login failed").toFireBaseFailure()
            }
        } catch (e: Exception) {
            e.toFireBaseFailure()
        }
    }

    suspend fun register(email: String, pass: String, name: String): ResultCallBack<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val fbUser = authDataSource.register(email, pass)
            if (fbUser != null) {
                // create firestore user doc
                val user = User(id = fbUser.uid, name = name, email = email)
                val saveResult = firestoreDataSource.saveUser(user)
                if (saveResult is ResultCallBack.Error) {
                    return@withContext saveResult
                }
                
                appDataStore.setLoggedIn(true)
                appDataStore.setUserId(fbUser.uid)
                ResultCallBack.Success(fbUser)
            } else {
                Exception("Registration failed").toFireBaseFailure()
            }
        } catch (e: Exception) {
            e.toFireBaseFailure()
        }
    }

    suspend fun signInWithGoogle(context: Context): ResultCallBack<FirebaseUser> {
        return try {
            // Step 1: Configure Google ID option
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .build()

            // Step 2: Build the credential request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            // Step 3: Launch Credential Manager
            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context, request)

            // Step 4: Extract the Google ID token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
            val idToken = googleIdTokenCredential.idToken

            // Step 5: Sign in with Firebase using the token
            val authResult = authDataSource.signInWithGoogle(idToken)
            if (authResult is ResultCallBack.Success) {
                val firebaseAuthResult = authResult.data
                val firebaseUser = firebaseAuthResult.user ?: return ResultCallBack.Error(
                    NetworkError.SERVER,
                    "Google sign-in failed."
                )

                // Create Firestore user doc only for new users
                val isNewUser = firebaseAuthResult.additionalUserInfo?.isNewUser == true
                if (isNewUser) {
                    val user = User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: "",
                        profileImage = firebaseUser.photoUrl?.toString() ?: ""
                    )
                    val createResult = firestoreDataSource.saveUser(user)
                    if (createResult is ResultCallBack.Error) {
                        return createResult
                    }
                }
                return ResultCallBack.Success(firebaseUser)
            }
            authResult as ResultCallBack.Error

        } catch (e: GetCredentialException) {
            ResultCallBack.Error(NetworkError.SERVER, e.message ?: "Google sign-in cancelled.")
        } catch (e: Exception) {
            ResultCallBack.Error(NetworkError.SERVER, e.message ?: "Google sign-in failed.")
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        authDataSource.logout()
        appDataStore.setLoggedIn(false)
        appDataStore.setUserId("")
    }

    suspend fun resetPassword(email: String): ResultCallBack<Any> = withContext(Dispatchers.IO) {
        try {
            authDataSource.resetPassword(email)
            ResultCallBack.Success(Any())
        } catch (e: Exception) {
            e.toFireBaseFailure()
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return authDataSource.getCurrentUser()
    }
}
