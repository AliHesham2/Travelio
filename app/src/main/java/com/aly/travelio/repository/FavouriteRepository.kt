package com.aly.travelio.repository

import com.aly.travelio.data.remote.firebase.firestore.FirestoreDataSource
import com.aly.travelio.model.Favourite
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.toFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavouriteRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) {

    suspend fun addFavourite(userId: String, favourite: Favourite): ResultCallBack<Unit> = withContext(Dispatchers.IO) {
        try {
            firestoreDataSource.addFavourite(userId, favourite)
            ResultCallBack.Success(Unit)
        } catch (e: Exception) {
            e.toFailure()
        }
    }

    suspend fun getFavourites(userId: String): ResultCallBack<List<Favourite>> = withContext(Dispatchers.IO) {
        try {
            val list = firestoreDataSource.getFavourites(userId)
            ResultCallBack.Success(list)
        } catch (e: Exception) {
            e.toFailure()
        }
    }
}
