package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.testDoubles.TestPlaylistDao
import com.squad.musicmatters.core.data.testDoubles.TestPlaylistEntryDao
import com.squad.musicmatters.core.model.PlaylistInfo
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PlaylistRepositoryImplTest {

    private lateinit var playlistDao: TestPlaylistDao
    private lateinit var playlistEntryDao: TestPlaylistEntryDao
    private lateinit var subject: PlaylistRepository

    @OptIn( ExperimentalCoroutinesApi::class )
    @Before
    fun setup() {
        playlistDao = TestPlaylistDao()
        playlistEntryDao = TestPlaylistEntryDao()
        subject = PlaylistRepositoryImpl(
            playlistDao = playlistDao,
            playlistEntryDao = playlistEntryDao,
        )
    }

    @Test
    fun testFetchFavoritesPlaylist() = runTest {
        var favoritesPlaylist = subject.fetchFavorites().first()
        TestCase.assertNull(favoritesPlaylist)
        subject.addToFavorites("song-id")
        favoritesPlaylist = subject.fetchFavorites().first()
        TestCase.assertNotNull(favoritesPlaylist)
    }

    @Test
    fun testSavePlaylist() = runTest {
        val testPlaylists = listOf(
            PlaylistInfo(id = "1", title = "", songIds = emptySet()),
            PlaylistInfo(id = "2", title = "", songIds = emptySet()),
            PlaylistInfo(
                id = "3",
                title = "",
                songIds = setOf("song-id-1", "song-id-2")
            ),
            PlaylistInfo(id = "4", title = "", songIds = emptySet()),
            PlaylistInfo(
                id = "5",
                title = "",
                songIds = setOf("song-id-1", "song-id-2", "song-id-3")
            ),
        )
        testPlaylists.forEach { subject.savePlaylist(it) }

        TestCase.assertEquals(5, playlistDao.fetchPlaylists().first().size)
        TestCase.assertEquals(
            3,
            playlistEntryDao
                .fetchEntriesForPlaylistWithId(id = "5")
                .first()
                .size
        )
        TestCase.assertEquals(
            2,
            playlistEntryDao
                .fetchEntriesForPlaylistWithId(id = "3")
                .first()
                .size
        )

    }

    @Test
    fun testDeletePlaylist() = runTest {
        val testPlaylists = listOf(
            PlaylistInfo(id = "1", title = "", songIds = emptySet()),
            PlaylistInfo(id = "2", title = "", songIds = emptySet()),
            PlaylistInfo(
                id = "3",
                title = "",
                songIds = setOf("song-id-1", "song-id-2")
            ),
            PlaylistInfo(id = "4", title = "", songIds = emptySet()),
            PlaylistInfo(
                id = "5",
                title = "",
                songIds = setOf("song-id-1", "song-id-2", "song-id-3")
            ),
        )
        testPlaylists.forEach { subject.savePlaylist(it) }

        subject.deletePlaylist(testPlaylists.first())
        subject.deletePlaylist(testPlaylists.last())

        TestCase.assertEquals(3, subject.fetchPlaylists().first().size)
    }

    @Test
    fun testAddSongIdToPlaylist() = runTest {
        val testPlaylists = listOf(
            PlaylistInfo(id = "1", title = "", songIds = emptySet()),
            PlaylistInfo(id = "2", title = "", songIds = emptySet()),
            PlaylistInfo(
                id = "3",
                title = "",
                songIds = setOf("song-id-1", "song-id-2")
            ),
            PlaylistInfo(id = "4", title = "", songIds = emptySet()),
            PlaylistInfo(
                id = "5",
                title = "",
                songIds = setOf("song-id-1", "song-id-2", "song-id-3")
            ),
        )
        testPlaylists.forEach { subject.savePlaylist(it) }

        subject.addSongIdToPlaylist(
            songId = "song-id-6",
            playlistId = "5"
        )
        subject.addSongIdToPlaylist(
            songId = "song-id-4",
            playlistId = "5"
        )
        subject.addSongIdToPlaylist(
            songId = "song-id-2",
            playlistId = "1"
        )

        TestCase.assertEquals(
            5,
            playlistEntryDao
                .fetchEntriesForPlaylistWithId(id = "5")
                .first()
                .size
        )
        TestCase.assertEquals(
            1,
            playlistEntryDao
                .fetchEntriesForPlaylistWithId(id = "1")
                .first()
                .size
        )
    }

    @Test
    fun testRemoveSongIdFromPlaylist() = runTest {
        val testPlaylists = listOf(
            PlaylistInfo(id = "1", title = "", songIds = emptySet()),
            PlaylistInfo(id = "2", title = "", songIds = emptySet()),
            PlaylistInfo(
                id = "3",
                title = "",
                songIds = setOf("song-id-1", "song-id-2")
            ),
            PlaylistInfo(id = "4", title = "", songIds = emptySet()),
            PlaylistInfo(
                id = "5",
                title = "",
                songIds = setOf("song-id-1", "song-id-2", "song-id-3")
            ),
        )
        testPlaylists.forEach { subject.savePlaylist(it) }

        subject.addSongIdToPlaylist(
            songId = "song-id-6",
            playlistId = "5"
        )
        subject.addSongIdToPlaylist(
            songId = "song-id-4",
            playlistId = "5"
        )
        subject.addSongIdToPlaylist(
            songId = "song-id-2",
            playlistId = "1"
        )

        TestCase.assertEquals(
            5,
            playlistEntryDao
                .fetchEntriesForPlaylistWithId(id = "5")
                .first()
                .size
        )

        subject.removeSongIdFromPlaylist(
            songId = "song-id-6",
            playlistId = "5"
        )

        TestCase.assertEquals(
            4,
            playlistEntryDao
                .fetchEntriesForPlaylistWithId(id = "5")
                .first()
                .size
        )

        TestCase.assertEquals(
            1,
            playlistEntryDao
                .fetchEntriesForPlaylistWithId(id = "1")
                .first()
                .size
        )
    }

    @Test
    fun testRenamePlaylist() = runTest {
        subject.addToFavorites(songId = "song-id-1")
        val testPlaylists = listOf(
            PlaylistInfo(id = "2", title = "", songIds = emptySet()),
            PlaylistInfo(
                id = "3",
                title = "",
                songIds = setOf("song-id-1", "song-id-2")
            ),
            PlaylistInfo(id = "4", title = "", songIds = emptySet()),
            PlaylistInfo(
                id = "5",
                title = "",
                songIds = setOf("song-id-1", "song-id-2", "song-id-3")
            ),
        )

        subject.renamePlaylist(
            playlistInfo = PlaylistInfo(
                id = FAVORITES_PLAYLIST_ID,
                title = "",
                songIds = emptySet()
            ),
            newTitle = "New Title"
        )

        TestCase.assertEquals(
            "",
            subject
                .fetchPlaylistWithId(FAVORITES_PLAYLIST_ID)
                .first()!!
                .title
        )

        subject.renamePlaylist(
            playlistInfo = PlaylistInfo(
                id = "5",
                title = "",
                songIds = setOf("song-id-1", "song-id-2", "song-id-3")
            ),
            newTitle = "Playlist 5"
        )

        TestCase.assertEquals(
            "Playlist 5",
            subject
                .fetchPlaylistWithId("5")
                .first()!!
                .title
        )

    }

}