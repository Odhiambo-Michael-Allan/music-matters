package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.common.di.IoScope
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.store.SongsStore
import com.squad.musicmatters.core.data.store.MediaStoreListener
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "SONGS-REPOSITORY"

class SongsRepositoryImpl @Inject constructor(
    private val songsStore: SongsStore,
    @param:Dispatcher( MusicMattersDispatchers.IO )
    private val ioDispatcher: CoroutineDispatcher,
) : SongsRepository {

    override fun fetchSongs(
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean
    ): Flow<List<Song>> = callbackFlow {

        // Helper function to fetch, sort, and emit the latest songs safely
        fun fetchAndEmit() {
            launch( ioDispatcher ) {
                runCatching { songsStore.fetchSongs( sortSongsBy, sortSongsInReverse ) }
                    .onSuccess { songs -> send( songs ) }
            }
        }

        val storeListener = object : MediaStoreListener {
            override fun onMediaStoreChanged() {
                fetchAndEmit()
            }
        }
        songsStore.registerListener( storeListener )

        fetchAndEmit()

        // 3. Keep the flow active until the collector cancels it, then clean up
        awaitClose {
            songsStore.unregisterListener( storeListener )
        }
    }.flowOn( ioDispatcher )

    override fun searchSongsMatching(
        query: String,
        sortSongsBy: SortSongsBy,
        sortSongsInReverse: Boolean
    ): Flow<List<Song>> = flow {
        val searchResults = songsStore.searchSongsMatching(
            query = query,
            sortSongsBy = sortSongsBy,
            sortSongsInReverse = sortSongsInReverse,
        )
        Timber.tag( TAG ).d( "SEARCH RESULTS: $searchResults" )
        emit( searchResults )
    }.flowOn( ioDispatcher )

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

        val storeListener = object : MediaStoreListener {
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

    override fun searchSongsInAlbumMatching( query: String ): Flow<List<Song>> =
        flow {
            emit( songsStore.searchSongsInAlbumMatching( query ) )
        }.flowOn( ioDispatcher )

    override fun searchSongsByArtistMatching( query: String ): Flow<List<Song>> =
        flow {
            emit( songsStore.searchSongsByArtistMatching( query ) )
        }.flowOn( ioDispatcher )

}