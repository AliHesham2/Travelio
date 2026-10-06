package com.aly.travelio.di

import com.aly.travelio.data.remote.firebase.auth.AuthDataSource
import com.aly.travelio.data.remote.firebase.firestore.FirestoreDataSource
import com.aly.travelio.datastore.AppDataStore
import com.aly.travelio.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authDataSource: AuthDataSource,
        firestoreDataSource: FirestoreDataSource,
        appDataStore: AppDataStore
    ): AuthRepository = AuthRepository(authDataSource, firestoreDataSource, appDataStore)

    @Provides
    @Singleton
    fun provideUserRepository(
        firestoreDataSource: FirestoreDataSource
    ): UserRepository = UserRepository(firestoreDataSource)

    @Provides
    @Singleton
    fun provideHotelRepository(
        firestoreDataSource: FirestoreDataSource
    ): HotelRepository = HotelRepository(firestoreDataSource)

    @Provides
    @Singleton
    fun provideTransportRepository(
        firestoreDataSource: FirestoreDataSource
    ): TransportRepository = TransportRepository(firestoreDataSource)

    @Provides
    @Singleton
    fun provideTripRepository(
        firestoreDataSource: FirestoreDataSource
    ): TripRepository = TripRepository(firestoreDataSource)

    @Provides
    @Singleton
    fun provideFavouriteRepository(
        firestoreDataSource: FirestoreDataSource
    ): FavouriteRepository = FavouriteRepository(firestoreDataSource)

    @Provides
    @Singleton
    fun provideReservationRepository(
        firestoreDataSource: FirestoreDataSource
    ): ReservationRepository = ReservationRepository(firestoreDataSource)
}
