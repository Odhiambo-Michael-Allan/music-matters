package com.odesa.musicMatters.core.data.playlists.impl

import com.odesa.musicMatters.core.data.TestDatabase
import com.odesa.musicMatters.core.data.database.CURRENT_PLAYING_QUEUE_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.FAVORITES_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.dao.PlaylistDao
import com.odesa.musicMatters.core.data.database.dao.PlaylistEntryDao
import com.odesa.musicMatters.core.data.database.dao.SongPlayCountEntryDao
import com.odesa.musicMatters.core.data.utils.subListNonStrict
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistsForSorting
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.model.PlaylistInfo
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith( RobolectricTestRunner::class )
class LocalPlaylistStoreTest : TestDatabase() {

    private lateinit var playlistDao: PlaylistDao
    private lateinit var playlistEntryDao: PlaylistEntryDao
    private lateinit var songPlayCountEntryDao: SongPlayCountEntryDao
    private lateinit var playlistStore: LocalPlaylistStore

    @Before
    fun setUp() {
        var counter = 0L
        playlistDao = database.playlistDao()
        playlistEntryDao = database.playlistEntryDao()
        songPlayCountEntryDao = database.songPlayCountEntryDao()
        playlistStore = LocalPlaylistStore(
            playlistDao = playlistDao,
            playlistEntryDao = playlistEntryDao,
            songPlayCountEntryDao = songPlayCountEntryDao,
            currentTimeInMillis = { counter++ }
        )
    }

    @Test
    fun testFetchAllPlaylists() = runTest {
        val playlists = playlistStore.fetchAllPlaylists()
        assertEquals( 3, playlists.size )
        assertNull( playlists.find { it.id == CURRENT_PLAYING_QUEUE_PLAYLIST_ID } )
    }

    @Test
    fun testFetchFavoritesPlaylist() = runTest {
        assertEquals(
            FAVORITES_PLAYLIST_ID,
            playlistStore.fetchFavoritesPlaylist().id
        )
    }

    @Test
    fun testSongIdToFavoritesPlaylist() = runTest {
        testSongs.forEach {
            playlistStore.addSongIdToFavoritesPlaylist( it.id )
        }
        assertEquals(
            testSongs.size,
            playlistStore.fetchFavoritesPlaylist().songIds.size
        )
    }

    @Test
    fun testRemoveSongFromFavoritesPlaylist() = runTest {
        testSongs.forEach {
            playlistStore.addSongIdToFavoritesPlaylist( it.id )
        }
        playlistStore.removeSongIdFromFavoritesPlaylist( testSongs.first().id )
        playlistStore.removeSongIdFromFavoritesPlaylist( testSongs.last().id )
        assertEquals( testSongs.size - 2, playlistStore.fetchFavoritesPlaylist().songIds.size )
    }

    @Test
    fun testAddSongIdToRecentlyPlayedSongsPlaylist() = runTest {
        testSongs.subListNonStrict( 5 ).forEach {
            playlistStore.addSongIdToRecentlyPlayedSongsPlaylist( it.id )
        }
        playlistStore.addSongIdToRecentlyPlayedSongsPlaylist( testSongs.last().id )
        playlistStore.addSongIdToRecentlyPlayedSongsPlaylist( testSongs.last().id )
        assertEquals( 6, playlistStore.fetchRecentlyPlayedSongsPlaylist().songIds.size )
        assertEquals(
            testSongs.last().id,
            playlistStore.fetchRecentlyPlayedSongsPlaylist().songIds.first()
        )
    }

    @Test
    fun testAddSongIdToMostPlayedSongsPlaylist() = runTest {
        testSongs.forEach {
            playlistStore.addSongIdToMostPlayedSongsPlaylist( it.id )
        }
        assertEquals( testSongs.size, playlistStore.fetchMostPlayedSongsPlaylist().songIds.size )
        for ( i in 1..3 )
            playlistStore.addSongIdToMostPlayedSongsPlaylist( testSongs.last().id )
        for ( i in 1..2 )
            playlistStore.addSongIdToMostPlayedSongsPlaylist( testSongs.first().id )
        val mostPlayedSongsPlaylist = playlistStore.fetchMostPlayedSongsPlaylist()
        assertEquals( testSongs.size, mostPlayedSongsPlaylist.songIds.size )
        assertEquals(
            testSongs.last().id,
            mostPlayedSongsPlaylist.songIds.first()
        )
        assertEquals(
            testSongs.first().id,
            mostPlayedSongsPlaylist.songIds[1]
        )
    }

    @Test
    fun testFetchEditablePlaylists() = runTest {
        assertEquals( 0, playlistStore.fetchEditablePlaylists().size )
        testPlaylistsForSorting.forEach {
            playlistStore.savePlaylist( it )
        }
        assertEquals(
            testPlaylistsForSorting.size,
            playlistStore.fetchEditablePlaylists().size
        )
    }

    @Test
    fun testDeletePlaylist() = runTest {
        testPlaylistsForSorting.forEach {
            playlistStore.savePlaylist( it )
        }
        playlistStore.deletePlaylist( testPlaylistsForSorting.first() )
        assertEquals(
            3 + ( testPlaylistsForSorting.size - 1 ),
            playlistStore.fetchAllPlaylists().size
        )
    }

    @Test
    fun testAddSongIdToPlaylist() = runTest {
        testPlaylistInfos.forEach {
            playlistStore.savePlaylist( it )
        }
        testSongs.subListNonStrict( 5 ).forEach {
            playlistStore.addSongIdToPlaylist( it.id, testPlaylistInfos.first() )
            playlistStore.addSongIdToPlaylist( it.id, testPlaylistInfos.last() )
        }
        assertEquals(
            5,
            playlistStore
                .fetchAllPlaylists()
                .find { it.id == testPlaylistInfos.first().id }!!
                .songIds
                .size
        )
        assertEquals(
            5,
            playlistStore
                .fetchAllPlaylists()
                .find { it.id == testPlaylistInfos.last().id }!!
                .songIds
                .size
        )
    }

    @Test
    fun testRenamePlaylist() = runTest {
        val id = UUID.randomUUID().toString()
        val playlistInfo = PlaylistInfo(
            id = id,
            title = "Playlist-1",
            songIds = emptyList()
        )
        playlistStore.savePlaylist( playlistInfo )
        playlistStore.renamePlaylist(
            playlistInfo = playlistInfo,
            newTitle = "Playlist-One"
        )
        assertEquals(
            "Playlist-One",
            playlistStore
                .fetchAllPlaylists()
                .find { it.id == id }!!
                .title
        )
    }
}