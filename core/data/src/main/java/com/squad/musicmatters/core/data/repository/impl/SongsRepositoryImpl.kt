package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.common.di.ApplicationScope
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.songs.SongsStore
import com.squad.musicmatters.core.data.songs.SongsStoreListener
import com.squad.musicmatters.core.data.utils.sortSongs
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class SongsRepositoryImpl @Inject constructor(
    private val songsStore: SongsStore,
    @ApplicationScope applicationScope: CoroutineScope,
    @param:Dispatcher(MusicMattersDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SongsRepository {

    /**
     * Returns a flow that emits the sorted list of songs and automatically re-queries and re-emits
     * when the list changes.
     * @param sortSongsBy The criteria passed to the [SongsStore] for sorting the songs.
     */
    // Share this flow across the whole app so it only reads the disk ONCE
    private val sharedSongsFlow = channelFlow {
        // 1. Create a simple event flow for media store changes
        val changeEvents = MutableSharedFlow<Unit>(
            extraBufferCapacity = 1,
            replay = 1,
        )

        val storeListener = object : SongsStoreListener {
            override fun onMediaStoreChanged() {
                changeEvents.tryEmit(Unit) // Trigger a new fetch
            }
        }
        songsStore.registerListener(storeListener)

        // 2. Use collectLatest inside the scope.
        // It automatically cancels the previous disk read if a new event arrives!
        launch( ioDispatcher ) {
            changeEvents.collectLatest {
                runCatching { songsStore.fetchSongs() }
                    .onSuccess { send( it ) } // send() is used in channelFlow
            }
        }

        // Trigger initial load
        changeEvents.tryEmit( Unit )

        awaitClose { songsStore.unregisterListener(storeListener) }
    }.shareIn(
        scope = applicationScope,
        started = SharingStarted.WhileSubscribed(5_000),
        replay = 1
    )

    override fun fetchSongs(
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean?
    ): Flow<List<Song>> = sharedSongsFlow.map { cachedSongs ->
        // Sort the list instantly in memory using your existing sort helper extension
        cachedSongs.sortSongs(
            by = sortSongsBy ?: DefaultPreferences.SORT_SONGS_BY,
            reverse = sortSongsInReverse ?: false
        )
    }.flowOn( Dispatchers.Default )

    override fun fetchLyricsForSong( song: Song? ): Flow<List<Lyric>> = channelFlow {
        if ( song == null ) {
            send( emptyList() )
            close()
            return@channelFlow
        }

        val changeEvents = MutableSharedFlow<Unit>(
            extraBufferCapacity = 1,
            replay = 1
        )

        val storeListener = object : SongsStoreListener {
            override fun onMediaStoreChanged() {
                changeEvents.tryEmit( Unit ) // Trigger new fetch
            }
        }

        songsStore.registerListener( storeListener )

        launch( ioDispatcher ) {
            changeEvents.collectLatest {
                runCatching { songsStore.fetchLyricsFor( song ) }
                    .onSuccess { send( it ) }
            }
        }
        // Trigger initial load.
        changeEvents.tryEmit( Unit )
        awaitClose { songsStore.unregisterListener( storeListener ) }
    }

}