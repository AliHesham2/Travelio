package com.aly.travelio.data.remote.firebase

import android.content.Context
import android.util.Log
import com.aly.travelio.datastore.AppDataStore
import com.aly.travelio.model.Hotel
import com.aly.travelio.model.Transportation
import com.aly.travelio.model.Trip
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSeeder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
    private val appDataStore: AppDataStore
) {

    private val gson = Gson()

    suspend fun seedDatabase() = withContext(Dispatchers.IO) {
        try {
            Log.d("FirebaseSeeder", "Starting database seeding...")
            val batch = firestore.batch()

            // Seed Hotels
            val hotelsJson = context.assets.open("hotels.json").use { InputStreamReader(it).readText() }
            val hotelType = object : TypeToken<List<Hotel>>() {}.type
            val hotels: List<Hotel> = gson.fromJson(hotelsJson, hotelType)
            hotels.forEach { hotel ->
                val ref = firestore.collection("hotels").document(hotel.id)
                batch.set(ref, hotel)
            }

            // Seed Transportations
            val transJson = context.assets.open("transportations.json").use { InputStreamReader(it).readText() }
            val transType = object : TypeToken<List<Transportation>>() {}.type
            val transports: List<Transportation> = gson.fromJson(transJson, transType)
            transports.forEach { transport ->
                val ref = firestore.collection("transportations").document(transport.id)
                batch.set(ref, transport)
            }

            // Seed Trips
            val tripsJson = context.assets.open("trips.json").use { InputStreamReader(it).readText() }
            val tripsType = object : TypeToken<List<Trip>>() {}.type
            val trips: List<Trip> = gson.fromJson(tripsJson, tripsType)
            trips.forEach { trip ->
                val ref = firestore.collection("trips").document(trip.id)
                batch.set(ref, trip)
            }

            // Commit batch
            batch.commit().await()
            appDataStore.setSeedingCompleted(true)
            Log.d("FirebaseSeeder", "Successfully seeded database! (${hotels.size} hotels, ${transports.size} transports, ${trips.size} trips)")
        } catch (e: Exception) {
            Log.e("FirebaseSeeder", "Error seeding database: ${e.message}", e)
        }
    }
}
