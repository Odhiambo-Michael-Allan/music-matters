package com.squad.musicmatters.core.data.repository.impl

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.songs.SongsStore
import com.squad.musicmatters.core.data.songs.SongsStoreListener
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.testing.songs.testLyric
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
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
            ioScope = TestScope(),
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

    private var currentSongs = emptyList<Song>()
    private var currentLyrics = emptyList<Lyric>()
    private val listeners = mutableListOf<SongsStoreListener>()

    override suspend fun fetchSongs(
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean?
    ): List<Song> = currentSongs

    override suspend fun fetchLyricsFor( song: Song? ) = currentLyrics

    override fun registerListener( listener: SongsStoreListener ) {
        listeners.add( listener )
    }

    override fun unregisterListener( listener: SongsStoreListener ) {
        listeners.remove( listener )
    }

    fun sendSongs( newSongs: List<Song> ) {
        currentSongs = newSongs
        println( "NEW SONGS SIZE: ${currentSongs.size}" )
        listeners.forEach {
            it.onMediaStoreChanged()
        }
    }

    fun sendLyrics( lyrics: List<Lyric> ) {
        currentLyrics = lyrics
        println( "LYRICS SIZE: ${currentLyrics.size}" )
    }
}