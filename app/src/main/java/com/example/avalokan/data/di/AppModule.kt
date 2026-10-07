package com.example.avalokan.data.di

import android.content.Context
import androidx.room3.Room
import com.example.avalokan.data.AvalokanDatabase
import com.example.avalokan.data.event.EventDao
import com.example.avalokan.data.event.EventRepository
import com.example.avalokan.data.event.FirestoreEventRepository
import com.example.avalokan.data.place.PlaceDao
import com.example.avalokan.data.place.PlaceRepository
import com.example.avalokan.data.place.FirestorePlaceRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AvalokanDatabase =
        Room.databaseBuilder(context, AvalokanDatabase::class.java, "avalokan.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePlaceDao(database: AvalokanDatabase): PlaceDao = database.placeDao()

    @Provides
    fun provideEventDao(database: AvalokanDatabase): EventDao = database.eventDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPlaceRepository(impl: FirestorePlaceRepository): PlaceRepository

    @Binds
    abstract fun bindEventRepository(impl: FirestoreEventRepository): EventRepository
}