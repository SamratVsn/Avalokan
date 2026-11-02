package com.example.avalokan.data.di

import android.content.Context
import androidx.room3.Room
import com.example.avalokan.data.AvalokanDatabase
import com.example.avalokan.data.event.EventDao
import com.example.avalokan.data.event.FirestoreEventRepository
import com.example.avalokan.data.local.AvatarStore
import com.example.avalokan.data.local.LocalAvatarStore
import com.example.avalokan.data.local.PreferencesStore
import com.example.avalokan.data.local.UserPreferences
import com.example.avalokan.data.place.FirestorePlaceRepository
import com.example.avalokan.data.place.PlaceDao
import com.example.avalokan.data.story.FirestoreStoryRepository
import com.example.avalokan.data.story.StoryDao
import com.example.avalokan.data.user.FirestoreUserRepository
import com.example.avalokan.data.user.UserRepository
import com.example.avalokan.domain.repository.EventRepository
import com.example.avalokan.domain.repository.PlaceRepository
import com.example.avalokan.domain.repository.StoryRepository
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
            // CONTRACT: this database is a disposable cache of Firestore
            // content (places, events, stories). Wiping it on a schema change
            // is safe and self-heals from the next snapshot. If user-created
            // data is ever stored here, remove this fallback and register real
            // migrations. Schemas are exported to app/schemas/ for that case.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): PreferencesStore =
        UserPreferences(context)

    @Provides
    fun providePlaceDao(database: AvalokanDatabase): PlaceDao = database.placeDao()

    @Provides
    fun provideEventDao(database: AvalokanDatabase): EventDao = database.eventDao()

    @Provides
    fun provideStoryDao(database: AvalokanDatabase): StoryDao = database.storyDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPlaceRepository(impl: FirestorePlaceRepository): PlaceRepository

    @Binds
    abstract fun bindUserRepository(impl: FirestoreUserRepository): UserRepository

    @Binds
    abstract fun bindEventRepository(impl: FirestoreEventRepository): EventRepository

    @Binds
    abstract fun bindStoryRepository(impl: FirestoreStoryRepository): StoryRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    abstract fun bindAvatarStore(impl: LocalAvatarStore): AvatarStore
}