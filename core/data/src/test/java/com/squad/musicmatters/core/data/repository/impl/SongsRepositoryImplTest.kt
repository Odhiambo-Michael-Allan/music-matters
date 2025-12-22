package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.songs.SongsStore
import com.squad.musicmatters.core.data.songs.SongsStoreListener
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SongsRepositoryImplTest {

    private lateinit var songsStore: TestSongsStore
    private lateinit var subject: SongsRepository

    @Before
    fun setUp() {
        songsStore = TestSongsStore()
        subject = SongsRepositoryImpl(
            songsStore = songsStore
        )
    }

    @Test
    fun testFetchSongs() = runTest {
        var songs = emptyList<Song>()
        backgroundScope.launch(UnconfinedTestDispatcher() ) {
            subject.fetchSongs( SortSongsBy.TITLE ).collect { currentSongs ->
                songs = currentSongs
            }
        }

        assertTrue( songs.isEmpty() )

        val testSongs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )

        songsStore.sendSongs( testSongs )
        println( "SONGS SIZE: ${songs.size}" )

        assertEquals( testSongs.size, songs.size )
    }

}

private class TestSongsStore : SongsStore {

    private var currentSongs = emptyList<Song>()
    private val listeners = mutableListOf<SongsStoreListener>()

    override suspend fun fetchSongs(
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean?
    ): List<Song> = currentSongs

    override fun registerListener( listener: SongsStoreListener ) {
        listeners.add( listener )
    }

    override fun unregisterListener( listener: SongsStoreListener ) {
        listeners.remove( listener )
    }

    fun sendSongs( newSongs: List<Song> ) {
        currentSongs = newSongs

        listeners.forEach {
            it.onMediaStoreChanged()
        }
    }
}