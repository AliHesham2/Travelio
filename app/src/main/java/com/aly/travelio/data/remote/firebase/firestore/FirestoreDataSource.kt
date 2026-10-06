package com.aly.travelio.data.remote.firebase.firestore

import com.aly.travelio.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.aly.travelio.util.ResultCallBack
import com.aly.travelio.util.toFailure
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // USERS
    suspend fun saveUser(user: User): ResultCallBack<Unit> {
        return try {
            firestore.collection("users").document(user.id).set(user).await()
            ResultCallBack.Success(Unit)
        } catch (e: Exception) {
            e.toFailure()
        }
    }

    suspend fun getUser(id: String): User? {
        val snapshot = firestore.collection("users").document(id).get().await()
        return snapshot.toObject(User::class.java)
    }

    suspend fun updateUserName(userId: String, newName: String): ResultCallBack<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update("name", newName).await()
            ResultCallBack.Success(Unit)
        } catch (e: Exception) {
            e.toFailure()
        }
    }

    // HOTELS
    suspend fun getFeaturedHotels(): List<Hotel> {
        val snapshot = firestore.collection("hotels")
            .whereEqualTo("featured", true)
            .get().await()
        return snapshot.toObjects(Hotel::class.java)
    }

    suspend fun getAllHotels(): List<Hotel> {
        val snapshot = firestore.collection("hotels")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()
        return snapshot.toObjects(Hotel::class.java)
    }

    suspend fun filterHotelsByRate(): List<Hotel> {
        val snapshot = firestore.collection("hotels")
            .orderBy("rate", Query.Direction.DESCENDING)
            .get().await()
        return snapshot.toObjects(Hotel::class.java)
    }

    suspend fun searchHotels(query: String): List<Hotel> {
        val term = query.lowercase()
        // Firebase doesn't support full-text search easily without external tools,
        // so we use simple equality or range queries for demo.
        val snapshot = firestore.collection("hotels")
            .whereGreaterThanOrEqualTo("locationLowercase", term)
            .whereLessThanOrEqualTo("locationLowercase", term + '\uf8ff')
            .get().await()
        return snapshot.toObjects(Hotel::class.java)
    }

    // TRANSPORTATIONS
    suspend fun getFeaturedTransportations(): List<Transportation> {
        val snapshot = firestore.collection("transportations")
            .whereEqualTo("featured", true)
            .get().await()
        return snapshot.toObjects(Transportation::class.java)
    }

    suspend fun getAllTransportations(): List<Transportation> {
        val snapshot = firestore.collection("transportations")
            .orderBy("departDate", Query.Direction.ASCENDING)
            .get().await()
        return snapshot.toObjects(Transportation::class.java)
    }

    suspend fun filterTransportationsByType(type: String): List<Transportation> {
        val snapshot = firestore.collection("transportations")
            .whereEqualTo("transportType", type)
            .get().await()
        return snapshot.toObjects(Transportation::class.java)
    }

    suspend fun searchTransportations(startAirportShort: String): List<Transportation> {
        val snapshot = firestore.collection("transportations")
            .whereEqualTo("startAirportShort", startAirportShort)
            .get().await()
        return snapshot.toObjects(Transportation::class.java)
    }

    // TRIPS
    suspend fun getFeaturedTrips(): List<Trip> {
        val snapshot = firestore.collection("trips")
            .whereEqualTo("featured", true)
            .get().await()
        return snapshot.toObjects(Trip::class.java)
    }

    suspend fun getAllTrips(): List<Trip> {
        val snapshot = firestore.collection("trips")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()
        return snapshot.toObjects(Trip::class.java)
    }

    suspend fun filterTripsByRate(): List<Trip> {
        val snapshot = firestore.collection("trips")
            .orderBy("rate", Query.Direction.DESCENDING)
            .get().await()
        return snapshot.toObjects(Trip::class.java)
    }

    // FAVOURITES (subcollection inside user)
    suspend fun addFavourite(userId: String, favourite: Favourite) {
        firestore.collection("users").document(userId)
            .collection("favourites").document(favourite.id).set(favourite).await()
    }

    suspend fun getFavourites(userId: String): List<Favourite> {
        val snapshot = firestore.collection("users").document(userId)
            .collection("favourites").get().await()
        return snapshot.toObjects(Favourite::class.java)
    }

    // RESERVATIONS (subcollection inside user)
    suspend fun addReservation(userId: String, reservation: Reservation) {
        firestore.collection("users").document(userId)
            .collection("reservations").document(reservation.id).set(reservation).await()
    }

    suspend fun getReservations(userId: String): List<Reservation> {
        val snapshot = firestore.collection("users").document(userId)
            .collection("reservations").get().await()
        return snapshot.toObjects(Reservation::class.java)
    }
}
