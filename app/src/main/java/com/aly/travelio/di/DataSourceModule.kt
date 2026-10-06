package com.aly.travelio.di

import com.aly.travelio.data.remote.firebase.auth.AuthDataSource
import com.aly.travelio.data.remote.firebase.firestore.FirestoreDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideAuthDataSource(auth: FirebaseAuth): AuthDataSource {
        return AuthDataSource(auth)
    }

    @Provides
    @Singleton
    fun provideFirestoreDataSource(firestore: FirebaseFirestore): FirestoreDataSource {
        return FirestoreDataSource(firestore)
    }
}
