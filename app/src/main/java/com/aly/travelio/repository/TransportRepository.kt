package com.aly.travelio.repository

import com.aly.travelio.data.remote.firebase.firestore.FirestoreDataSource
import com.aly.travelio.model.Transportation
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.toFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TransportRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) {

    suspend fun getFeaturedTransportations(): ResultCallBack<List<Transportation>> = withContext(Dispatchers.IO) {
        try {
            ResultCallBack.Success(firestoreDataSource.getFeaturedTransportations())
        } catch (e: Exception) { e.toFailure() }
    }

    suspend fun getAllTransportations(): ResultCallBack<List<Transportation>> = withContext(Dispatchers.IO) {
        try {
            ResultCallBack.Success(firestoreDataSource.getAllTransportations())
        } catch (e: Exception) { e.toFailure() }
    }

    suspend fun filterTransportationsByType(type: String): ResultCallBack<List<Transportation>> = withContext(Dispatchers.IO) {
        try {
            ResultCallBack.Success(firestoreDataSource.filterTransportationsByType(type))
        } catch (e: Exception) { e.toFailure() }
    }

    suspend fun searchTransportations(startAirportShort: String): ResultCallBack<List<Transportation>> = withContext(Dispatchers.IO) {
        try {
            ResultCallBack.Success(firestoreDataSource.searchTransportations(startAirportShort))
        } catch (e: Exception) { e.toFailure() }
    }
}
