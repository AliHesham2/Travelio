package com.aly.travelio.repository

import com.aly.travelio.data.remote.firebase.firestore.FirestoreDataSource
import com.aly.travelio.model.User
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.toFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) {

    suspend fun getUserProfile(userId: String): ResultCallBack<User> = withContext(Dispatchers.IO) {
        try {
            val user = firestoreDataSource.getUser(userId)
            if (user != null) {
                ResultCallBack.Success(user)
            } else {
                Exception("User not found").toFailure()
            }
        } catch (e: Exception) {
            e.toFailure()
        }
    }

    suspend fun updateUserName(userId: String, newName: String): ResultCallBack<Unit> =
        withContext(Dispatchers.IO) {
            firestoreDataSource.updateUserName(userId, newName)
        }
}
