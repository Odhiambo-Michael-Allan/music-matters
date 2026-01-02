package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.songs.SongsStore
import com.squad.musicmatters.core.data.songs.SongsStoreListener
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

class SongsRepositoryImpl @Inject constructor(
    private val songsStore: SongsStore
) : SongsRepository {

    /**
     * Returns a flow that emits the sorted list of songs and automatically re-queries and re-emits
     * when the list changes.
     * @param sortSongsBy The criteria passed to the [SongsStore] for sorting the songs.
     */
    override fun fetchSongs(
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean?
    ): Flow<List<Song>> = callbackFlow {
        // Helper function to query and emit the data.
        suspend fun queryAndEmit() {
            // Pass the sorting criteria directly to the store
            val sortedSongs = songsStore.fetchSongs( sortSongsBy, sortSongsInReverse )
            trySend( sortedSongs )
        }
        // Define the internal listener
        val storeListener = object : SongsStoreListener {
            override fun onMediaStoreChanged() {
                // When notified of a change, re-query using the current [SortSongsBy]
                launch( Dispatchers.IO ) { queryAndEmit() }
            }
        }
        songsStore.registerListener( storeListener )
        queryAndEmit()
        awaitClose {
            songsStore.unregisterListener( storeListener )
        }
    }

    override fun fetchLyricsForSong( song: Song? ): Flow<List<Lyric>> =
        flow { songsStore.fetchLyricsFor( song ) }

}