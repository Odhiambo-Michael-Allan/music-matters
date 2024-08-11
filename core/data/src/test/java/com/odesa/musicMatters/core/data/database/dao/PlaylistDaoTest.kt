package com.odesa.musicMatters.core.data.database.dao

import com.odesa.musicMatters.core.data.TestDatabase
import com.odesa.musicMatters.core.data.database.FAVORITES_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.RECENTLY_PLAYED_SONGS_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.model.Playlist
import com.odesa.musicMatters.core.data.database.model.PlaylistEntry
import com.odesa.musicMatters.core.data.utils.subListNonStrict
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith( RobolectricTestRunner::class )
class PlaylistDaoTest : TestDatabase() {

    private lateinit var playlistDao: PlaylistDao
    private lateinit var playlistEntryDao: PlaylistEntryDao

    @Before
    fun setUp() {
        playlistDao = database.playlistDao()
        playlistEntryDao = database.playlistEntryDao()
    }

    @Test
    fun testPlaylistExists() = runTest {
        assertTrue( playlistDao.playlistExists( FAVORITES_PLAYLIST_ID ) )
        assertTrue( playlistDao.playlistExists( RECENTLY_PLAYED_SONGS_PLAYLIST_ID ) )
    }

    @Test
    fun testFetchPlaylists() = runTest {
        val playlists = playlistDao.fetchPlaylists()
        testSongs.subListNonStrict( 5 ).forEach {
            playlistEntryDao.insert(
                PlaylistEntry(
                    playlistId = FAVORITES_PLAYLIST_ID,
                    songId = it.id
                )
            )
        }
        assertEquals( 3, playlists.size )
        assertEquals(
            5,
            playlistDao.fetchPlaylistWithId( FAVORITES_PLAYLIST_ID ).entries.size
        )
    }

    @Test
    fun testFetchFavoriteSongsPlaylist() = runTest {
        val favoritesPlaylist = playlistDao.fetchPlaylistWithId( FAVORITES_PLAYLIST_ID )
        assertNotNull( favoritesPlaylist )
    }

    @Test
    fun testFetchRecentlyPlayedSongsPlaylist() = runTest {
        val recentSongsPlaylist = playlistDao.fetchPlaylistWithId( RECENTLY_PLAYED_SONGS_PLAYLIST_ID )
        assertNotNull( recentSongsPlaylist )
        assertEquals( 0, recentSongsPlaylist.entries.size )
    }

    @Test
    fun testDeletePlaylist() = runTest {
        val playlist1 = Playlist(
            id = UUID.randomUUID().toString(),
            title = "Playlist-1"
        )
        val playlist2 = Playlist(
            id = UUID.randomUUID().toString(),
            title = "Playlist-2"
        )
        playlistDao.insertAll( playlist1, playlist2 )
        assertEquals( 5, playlistDao.fetchPlaylists().size )
        playlistDao.delete( playlist2 )
        assertEquals( 4, playlistDao.fetchPlaylists().size )
        playlistDao.delete( playlist1 )
        assertEquals( 3, playlistDao.fetchPlaylists().size )
    }

    @Test
    fun testRenamePlaylist() = runTest {
        val id1 = UUID.randomUUID().toString()
        val playlist1 = Playlist(
            id = id1,
            title = "Playlist-1"
        )
        val playlist2 = Playlist(
            id = UUID.randomUUID().toString(),
            title = "Playlist-2"
        )
        playlistDao.insertAll( playlist1, playlist2 )
        playlistDao.update(
            playlist1.copy(
                title = "Playlist-One"
            )
        )
        assertEquals(
            "Playlist-One",
            playlistDao.fetchPlaylistWithId( id1 ).playlist.title
        )
    }
}