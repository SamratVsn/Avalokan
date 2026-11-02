package com.example.avalokan.domain.repository

import com.example.avalokan.domain.model.Place
import kotlinx.coroutines.flow.Flow


//Content contract used by the UI. Implementations may use Room, Firestore, or a fake without any screen knowing the difference.
interface PlaceRepository {
    fun observePlaces(): Flow<List<Place>>
    fun observePlace(id: String): Flow<Place?>
    suspend fun clearCache()

    /** Forces a server fetch; snapshot listeners keep streaming afterwards. */
    suspend fun refresh()
}
