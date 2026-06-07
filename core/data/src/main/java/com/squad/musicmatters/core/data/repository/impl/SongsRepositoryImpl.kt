package com.squad.musicmatters.core.data.repository.impl

import android.util.Log
import com.squad.musicmatters.core.common.di.ApplicationScope
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.songs.SongsStore
import com.squad.musicmatters.core.data.songs.SongsStoreListener
import com.squad.musicmatters.core.data.utils.sortSongs
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

class SongsRepositoryImpl @Inject constructor(
    private val songsStore: SongsStore,
    @ApplicationScope applicationScope: CoroutineScope,
) : SongsRepository {

    /**
     * Returns a flow that emits the sorted list of songs and automatically re-queries and re-emits
     * when the list changes.
     * @param sortSongsBy The criteria passed to the [SongsStore] for sorting the songs.
     */
    // Share this flow across the whole app so it only reads the disk ONCE
    private val sharedSongsFlow = callbackFlow {
        suspend fun queryAndEmit() {
            val sortedSongs = songsStore.fetchSongs()
            trySend( sortedSongs )
        }

        val storeListener = object : SongsStoreListener {
            override fun onMediaStoreChanged() {
                launch(Dispatchers.IO) { queryAndEmit() }
            }
        }

        songsStore.registerListener( storeListener )
        queryAndEmit()
        awaitClose { songsStore.unregisterListener( storeListener ) }
    }.shareIn(
        scope = applicationScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        replay = 1 // Keeps the 10,000 songs cached in memory
    )

    override fun fetchSongs(
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean?
    ): Flow<List<Song>> = sharedSongsFlow.map { cachedSongs ->
        // Sort the list instantly in memory using your existing sort helper extension
        cachedSongs.sortSongs(
            sortSongsBy = sortSongsBy ?: DefaultPreferences.SORT_SONGS_BY,
            reverse = sortSongsInReverse ?: false
        )
    }

    override fun fetchLyricsForSong( song: Song? ): Flow<List<Lyric>> = callbackFlow {
        if ( song == null ) {
            trySend( emptyList() )
            close()
            return@callbackFlow
        }

        suspend fun load() {
            val result = songsStore.fetchLyricsFor( song )
            trySend( result )
        }

        val listener = object : SongsStoreListener {
            override fun onMediaStoreChanged() {
                // Re-read the file if the media store notifies of a change
                launch { load() }
            }
        }

        songsStore.registerListener( listener )
        launch { load() } // Initial load

        awaitClose { songsStore.unregisterListener( listener ) }
    }

}