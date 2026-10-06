package com.aly.travelio.repository

import com.aly.travelio.data.remote.firebase.firestore.FirestoreDataSource
import com.aly.travelio.model.Reservation
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.toFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReservationRepository @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) {

    suspend fun addReservation(userId: String, reservation: Reservation): ResultCallBack<Unit> = withContext(Dispatchers.IO) {
        try {
            firestoreDataSource.addReservation(userId, reservation)
            ResultCallBack.Success(Unit)
        } catch (e: Exception) {
            e.toFailure()
        }
    }

    suspend fun getReservations(userId: String): ResultCallBack<List<Reservation>> = withContext(Dispatchers.IO) {
        try {
            val list = firestoreDataSource.getReservations(userId)
            ResultCallBack.Success(list)
        } catch (e: Exception) {
            e.toFailure()
        }
    }
}
