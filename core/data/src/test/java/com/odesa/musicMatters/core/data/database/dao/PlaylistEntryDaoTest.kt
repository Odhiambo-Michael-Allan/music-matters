package com.odesa.musicMatters.core.data.database.dao

import com.odesa.musicMatters.core.data.TestDatabase
import com.odesa.musicMatters.core.data.database.CURRENT_PLAYING_QUEUE_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.FAVORITES_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.RECENTLY_PLAYED_SONGS_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.model.PlaylistEntry
import com.odesa.musicMatters.core.data.utils.subListNonStrict
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class PlaylistEntryDaoTest : TestDatabase() {

    private lateinit var playlistEntryDao: PlaylistEntryDao

    @Before
    fun setUp() {
        playlistEntryDao = database.playlistEntryDao()
    }

    @Test
    fun testAddSongIdToFavoriteSongsPlaylist() {
        testAddSongsToPlaylistWithId( FAVORITES_PLAYLIST_ID )
    }

    @Test
    fun testRemoveSongIdFromFavoritesSongsPlaylist() {
        testRemoveSongFromPlaylistWithId( FAVORITES_PLAYLIST_ID )
    }

    @Test
    fun testAddSongIdToRecentlyPlayedSongsPlaylist() {
        testAddSongsToPlaylistWithId( RECENTLY_PLAYED_SONGS_PLAYLIST_ID )
    }

    @Test
    fun testRemoveSongIdFromRecentlyPlayedSongsPlaylist() {
        testRemoveSongFromPlaylistWithId( RECENTLY_PLAYED_SONGS_PLAYLIST_ID )
    }

    @Test
    fun testAddSongIdToCurrentPlayingQueuePlaylist() {
        testAddSongsToPlaylistWithId( CURRENT_PLAYING_QUEUE_PLAYLIST_ID )
    }

    @Test
    fun testClearCurrentlyPlayingQueuePlaylistEntries() = runTest {
        testSongs.subListNonStrict( 5 ).forEach {
            playlistEntryDao.insert(
                PlaylistEntry(
                    playlistId = CURRENT_PLAYING_QUEUE_PLAYLIST_ID,
                    songId = it.id
                )
            )
        }
        playlistEntryDao.removeEntriesForPlaylistWithId( CURRENT_PLAYING_QUEUE_PLAYLIST_ID )
        assertTrue( playlistEntryDao.fetchEntriesForPlaylistWithId( CURRENT_PLAYING_QUEUE_PLAYLIST_ID ).isEmpty() )
    }

    private fun testAddSongsToPlaylistWithId( playlistId: String ) = runTest {
        playlistEntryDao.insertAll(
            testSongs.map {
                PlaylistEntry(
                    playlistId = playlistId,
                    songId = it.id
                )
            }
        )
        assertEquals(
            testSongs.size,
            playlistEntryDao.fetchEntriesForPlaylistWithId( playlistId ).size
        )
    }

    private fun testRemoveSongFromPlaylistWithId( playlistId: String ) = runTest {
        testSongs.subListNonStrict( 5 ).forEach {
            playlistEntryDao.insert(
                PlaylistEntry(
                    playlistId = playlistId,
                    songId = it.id
                )
            )
        }
        assertEquals(
            5,
            playlistEntryDao.fetchEntriesForPlaylistWithId( playlistId ).size
        )
        playlistEntryDao.deleteEntry(
            playlistId = playlistId,
            songId = testSongs.first().id
        )
        assertEquals(
            4,
            playlistEntryDao.fetchEntriesForPlaylistWithId( playlistId ).size
        )
        playlistEntryDao.deleteEntry(
            playlistId = playlistId,
            songId = testSongs[ 1 ].id
        )
        assertEquals(
            3,
            playlistEntryDao.fetchEntriesForPlaylistWithId( playlistId ).size
        )
    }
}