package com.odesa.musicMatters.core.data.playlists.impl

import com.odesa.musicMatters.core.data.playlists.PlaylistStore
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.PlaylistRepositoryImpl
import com.odesa.musicMatters.core.datatesting.playlist.FakePlaylistStore
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.UUID

@RunWith( RobolectricTestRunner::class )
class PlaylistRepositoryImplTest {

    private lateinit var playlistStore: PlaylistStore
    private lateinit var playlistRepository: PlaylistRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        playlistStore = FakePlaylistStore()
        playlistRepository = PlaylistRepositoryImpl(
            playlistStore = playlistStore,
            coroutineScope = TestCoroutineScope()
        )
    }

    @Test
    fun testPlaylistRepositoryInitiallyHasThreePlaylists_favoritesRecentAndMostPlayedPlaylist() = runTest {
        assertEquals( 3, playlistRepository.playlists.value.size )
    }

    @Test
    fun whenNoFavoritesPlaylistHasPreviouslyBeenSaved_emptyPlaylistIsReturned() {
        val favoritesPlaylist = playlistRepository.favoritesPlaylistInfo.value
        assertEquals( 0, favoritesPlaylist.songIds.size )
    }

    @Test
    fun testSongIdsAreCorrectlyAddedToFavoritesPlaylist() = runTest {
        for ( i in 0 until 100 )
            playlistRepository.addToFavorites( UUID.randomUUID().toString() )
        assertEquals( 100, playlistRepository.favoritesPlaylistInfo.value.songIds.size )
    }

    @Test
    fun testSongIdsAreCorrectlyRemovedFromFavoritesPlaylist() = runTest {
        val songIdsToBeAdded = List( 100 ) {
            UUID.randomUUID().toString()
        }
        songIdsToBeAdded.forEach {
            playlistRepository.addToFavorites( it )
        }
        playlistRepository.removeFromFavorites( songIdsToBeAdded.first() )
        assertEquals(99, playlistRepository.favoritesPlaylistInfo.value.songIds.size)
        playlistRepository.removeFromFavorites( songIdsToBeAdded.last() )
        assertEquals(98, playlistRepository.favoritesPlaylistInfo.value.songIds.size)
    }

    @Test
    fun testFavoriteSongIdIsCorrectlyIdentified() = runTest {
        songIdsToBeAdded.forEach {
            playlistRepository.addToFavorites( it )
        }
        TestCase.assertTrue( playlistRepository.isFavorite( songIdsToBeAdded.first() ) )
        TestCase.assertTrue( playlistRepository.isFavorite(songIdsToBeAdded.last() ) )
        TestCase.assertFalse( playlistRepository.isFavorite( "random_string" ) )
    }

    @Test
    fun testAddToRecentlyPlayedSongsPlaylist() = runTest {
        songIdsToBeAdded.forEach {
            playlistRepository.addToRecentlyPlayedSongsPlaylist( it )
        }
        assertEquals(
            songIdsToBeAdded.size,
            playlistRepository.recentlyPlayedSongsPlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testAddToMostPlayedPlaylist() = runTest {
        songIdsToBeAdded.forEach {
            playlistRepository.addToMostPlayedPlaylist( it )
        }
        assertEquals(
            songIdsToBeAdded.size,
            playlistRepository.mostPlayedSongsPlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testRemoveFromMostPlayedPlaylist() = runTest {
        songIdsToBeAdded.forEach {
            playlistRepository.addToMostPlayedPlaylist( it )
        }
        playlistRepository.removeSongIdFromMostPlayedPlaylist(
            songIdsToBeAdded.first()
        )
        assertEquals(
            songIdsToBeAdded.size - 1,
            playlistRepository.mostPlayedSongsPlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testSongsAreCorrectlyRemovedFromPlaylist() = runTest {
        playlistRepository.savePlaylist( testPlaylistInfos.first() )
        songIdsToBeAdded.forEach {
            playlistRepository.addSongIdToPlaylist( it, testPlaylistInfos.first().id )
        }
        assertEquals(
            songIdsToBeAdded.size,
            playlistRepository.playlists.value.find { it.id == testPlaylistInfos.first().id }!!
                .songIds.size
        )
    }

    @Test
    fun testSavePlaylist() = runTest {
        testPlaylistInfos.forEach {
            playlistRepository.savePlaylist( it )
        }
        assertEquals(testPlaylistInfos.size + 3, playlistRepository.playlists.value.size)
    }

    @Test
    fun testDeletePlaylist() = runTest {
        testPlaylistInfos.forEach {
            playlistRepository.savePlaylist( it )
        }
        playlistRepository.deletePlaylist( testPlaylistInfos.first() )
        playlistRepository.deletePlaylist( testPlaylistInfos.last() )
        assertEquals(
            (testPlaylistInfos.size - 2) + 3,
            playlistRepository.playlists.value.size
        )
    }

    @Test
    fun testSongIsCorrectlyRemovedFromFavoriteList() = runTest {
        testSongs.forEach {
            playlistRepository.addToFavorites( it.id )
        }
        assertEquals(
            testSongs.size,
            playlistRepository.favoritesPlaylistInfo.value.songIds.size
        )
        playlistRepository.addToFavorites( testSongs.first().id )
        assertEquals(
            testSongs.size - 1,
            playlistRepository.favoritesPlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testPlaylistsAreRenamedCorrectly() = runTest {
        testPlaylistInfos.forEach {
            playlistRepository.savePlaylist( it )
        }
        playlistRepository.renamePlaylist( testPlaylistInfos.first(), "mob-deep" )
        TestCase.assertTrue(playlistStore.fetchAllPlaylists().map { it.title }.contains("mob-deep"))
        TestCase.assertTrue(playlistRepository.playlists.value.map { it.title }
            .contains("mob-deep"))
    }

    @Test
    fun testSongsIdsAreCorrectlyAddedToCurrentPlayingQueuePlaylist() = runTest {
        TestCase.assertTrue(playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.isEmpty())
        playlistRepository.saveCurrentlyPlayingQueue( songIdsToBeAdded )
        assertEquals(
            songIdsToBeAdded.size,
            playlistStore.fetchCurrentPlayingQueue().songIds.size
        )
        assertEquals(
            songIdsToBeAdded.size,
            playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testClearCurrentPlayingQueuePlaylist() = runTest {
        playlistRepository.saveCurrentlyPlayingQueue( songIdsToBeAdded )
        playlistRepository.clearCurrentPlayingQueuePlaylist()
        TestCase.assertTrue(playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.isEmpty())
    }
}

val songIdsToBeAdded = testSongs.map { it.id }