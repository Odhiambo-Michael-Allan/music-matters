package com.squad.musicmatters.core.data.repository.impl

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.store.SongsStore
import com.squad.musicmatters.core.data.store.MediaStoreListener
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.testing.songs.testLyric
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SongsRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var songsStore: FakeSongsStore
    private lateinit var subject: SongsRepository

    @Before
    fun setUp() {
        songsStore = FakeSongsStore()
        subject = SongsRepositoryImpl(
            songsStore = songsStore,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun testFetchSongs() = runTest {

        val testSongs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )

        songsStore.sendSongs( testSongs )

        assertEquals(
            testSongs.size,
            subject.fetchSongs( SortSongsBy.TITLE ).first().size
        )
    }

    @Test
    fun testFetchLyrics() = runTest {
        val testLyrics = listOf(
            testLyric( content = "Lyric-1" ),
            testLyric( content = "Lyric-2" ),
            testLyric( content = "Lyric-3" ),
            testLyric( content = "Lyric-4" ),
            testLyric( content = "Lyric-5" ),
        )

        songsStore.sendLyrics( testLyrics )

        assertEquals( testLyrics.size, testLyrics.size )
    }

}

private class FakeSongsStore : SongsStore {

    private var currentSongs = MutableSharedFlow<List<Song>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private var currentLyrics = emptyList<Lyric>()
    private val listeners = mutableListOf<MediaStoreListener>()


    override fun fetchSongsFlow(
        filterSongIds: Set<String>,
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean
    ): Flow<List<Song>> = currentSongs

    override suspend fun fetchLyricsFor( song: Song? ) = currentLyrics

    override suspend fun searchSongsMatching(
        query: String,
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean
    ): List<Song> = currentSongs.first()

    override suspend fun searchSongsInAlbumMatching(
        query: String
    ): List<Song> = currentSongs.map { songs ->
        songs.filter { it.title.contains( query ) }
    }.first()

    override suspend fun searchSongsByArtistMatching(
        query: String
    ): List<Song> = currentSongs.map { songs ->
        songs.filter { it.title.contains( query ) }
    }.first()

    override fun unregisterListener( listener: MediaStoreListener ) {
        listeners.remove( listener )
    }

    fun sendSongs( newSongs: List<Song> ) {
        currentSongs.tryEmit( newSongs )
        listeners.forEach {
            it.onMediaStoreChanged()
        }
    }

    fun sendLyrics( lyrics: List<Lyric> ) {
        currentLyrics = lyrics
        println( "LYRICS SIZE: ${currentLyrics.size}" )
    }
}