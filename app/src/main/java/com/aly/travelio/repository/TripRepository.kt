package com.aly.travelio.repository

import com.aly.travelio.data.remote.firebase.firestore.FirestoreDataSource
import com.aly.travelio.model.Trip
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.toFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) {

    suspend fun getFeaturedTrips(): ResultCallBack<List<Trip>> = withContext(Dispatchers.IO) {
        try {
            ResultCallBack.Success(firestoreDataSource.getFeaturedTrips())
        } catch (e: Exception) { e.toFailure() }
    }

    suspend fun getAllTrips(): ResultCallBack<List<Trip>> = withContext(Dispatchers.IO) {
        try {
            ResultCallBack.Success(firestoreDataSource.getAllTrips())
        } catch (e: Exception) { e.toFailure() }
    }

    suspend fun filterTripsByRate(): ResultCallBack<List<Trip>> = withContext(Dispatchers.IO) {
        try {
            ResultCallBack.Success(firestoreDataSource.filterTripsByRate())
        } catch (e: Exception) { e.toFailure() }
    }
}
