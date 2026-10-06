package com.aly.travelio.repository

import com.aly.travelio.data.remote.firebase.firestore.FirestoreDataSource
import com.aly.travelio.model.Hotel
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.toFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HotelRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) {

    suspend fun getFeaturedHotels(): ResultCallBack<List<Hotel>> = withContext(Dispatchers.IO) {
        try {
            ResultCallBack.Success(firestoreDataSource.getFeaturedHotels())
        } catch (e: Exception) { e.toFailure() }
    }

    suspend fun getAllHotels(): ResultCallBack<List<Hotel>> = withContext(Dispatchers.IO) {
        try {
            ResultCallBack.Success(firestoreDataSource.getAllHotels())
        } catch (e: Exception) { e.toFailure() }
    }

    suspend fun filterHotelsByRate(): ResultCallBack<List<Hotel>> = withContext(Dispatchers.IO) {
        try {
            ResultCallBack.Success(firestoreDataSource.filterHotelsByRate())
        } catch (e: Exception) { e.toFailure() }
    }

    suspend fun searchHotels(query: String): ResultCallBack<List<Hotel>> = withContext(Dispatchers.IO) {
        try {
            ResultCallBack.Success(firestoreDataSource.searchHotels(query))
        } catch (e: Exception) { e.toFailure() }
    }
}
