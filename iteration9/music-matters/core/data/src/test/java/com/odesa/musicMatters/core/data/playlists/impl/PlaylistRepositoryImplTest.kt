package com.odesa.musicMatters.core.data.playlists.impl

import com.odesa.musicMatters.core.data.playlists.PlaylistRepository
import com.odesa.musicMatters.core.data.playlists.PlaylistStore
import com.odesa.musicMatters.core.datatesting.playlist.FakePlaylistStore
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
            playlistStore,
            coroutineDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun testPlaylistRepositoryInitiallyHasThreePlaylists_favoritesRecentsAndMostPlayedPlaylist() = runTest {
        TestCase.assertEquals(3, playlistRepository.playlists.value.size)
    }

    @Test
    fun whenNoFavoritesPlaylistHasPreviouslyBeenSaved_emptyPlaylistIsReturned() {
        val favoritesPlaylist = playlistRepository.favoritesPlaylistInfo.value
        TestCase.assertEquals(0, favoritesPlaylist.songIds.size)
    }

    @Test
    fun testSongIdsAreCorrectlyAddedToFavoritesPlaylist() = runTest {
        for ( i in 0 until 100 )
            playlistRepository.addToFavorites( UUID.randomUUID().toString() )
        TestCase.assertEquals(100, playlistRepository.favoritesPlaylistInfo.value.songIds.size)
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
        TestCase.assertEquals(99, playlistRepository.favoritesPlaylistInfo.value.songIds.size)
        playlistRepository.removeFromFavorites( songIdsToBeAdded.last() )
        TestCase.assertEquals(98, playlistRepository.favoritesPlaylistInfo.value.songIds.size)
    }

    @Test
    fun testFavoriteSongIdIsCorrectlyIdentified() = runTest {
        songIdsToBeAdded.forEach {
            playlistRepository.addToFavorites( it )
        }
        TestCase.assertTrue(playlistRepository.isFavorite(songIdsToBeAdded.first()))
        TestCase.assertTrue(playlistRepository.isFavorite(songIdsToBeAdded.last()))
        TestCase.assertFalse(playlistRepository.isFavorite("random_string"))
    }

    @Test
    fun testAddToRecentlyPlayedSongsPlaylist() = runTest {
        songIdsToBeAdded.forEach {
            playlistRepository.addToRecentlyPlayedSongsPlaylist( it )
        }
        TestCase.assertEquals(
            songIdsToBeAdded.size,
            playlistRepository.recentlyPlayedSongsPlaylistInfo.value.songIds.size
        )
        playlistRepository.addToRecentlyPlayedSongsPlaylist( songIdsToBeAdded.last() )
        TestCase.assertEquals(
            songIdsToBeAdded.size,
            playlistRepository.recentlyPlayedSongsPlaylistInfo.value.songIds.size
        )
        TestCase.assertEquals(
            songIdsToBeAdded.last(),
            playlistRepository.recentlyPlayedSongsPlaylistInfo.value.songIds.first()
        )
    }

    @Test
    fun testAddToMostPlayedPlaylist() = runTest {
        songIdsToBeAdded.forEach {
            playlistRepository.addToMostPlayedPlaylist( it )
        }
        TestCase.assertEquals(
            songIdsToBeAdded.size,
            playlistRepository.mostPlayedSongsPlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testSavePlaylist() = runTest {
        testPlaylistInfos.forEach {
            playlistRepository.savePlaylist( it )
        }
        TestCase.assertEquals(testPlaylistInfos.size + 3, playlistRepository.playlists.value.size)
    }

    @Test
    fun testDeletePlaylist() = runTest {
        testPlaylistInfos.forEach {
            playlistRepository.savePlaylist( it )
        }
        playlistRepository.deletePlaylist( testPlaylistInfos.first() )
        playlistRepository.deletePlaylist( testPlaylistInfos.last() )
        TestCase.assertEquals(
            (testPlaylistInfos.size - 2) + 3,
            playlistRepository.playlists.value.size
        )
    }

    @Test
    fun testSongIsCorrectlyRemovedFromFavoriteList() = runTest {
        testSongs.forEach {
            playlistRepository.addToFavorites( it.id )
        }
        TestCase.assertEquals(
            testSongs.size,
            playlistRepository.favoritesPlaylistInfo.value.songIds.size
        )
        playlistRepository.addToFavorites( testSongs.first().id )
        TestCase.assertEquals(
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
        playlistRepository.saveCurrentQueue( songIdsToBeAdded )
        TestCase.assertEquals(
            songIdsToBeAdded.size,
            playlistStore.fetchCurrentPlayingQueue().songIds.size
        )
        TestCase.assertEquals(
            songIdsToBeAdded.size,
            playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.size
        )
    }

    @Test
    fun testClearCurrentPlayingQueuePlaylist() = runTest {
        playlistRepository.saveCurrentQueue( songIdsToBeAdded )
        playlistRepository.clearCurrentPlayingQueuePlaylist()
        TestCase.assertTrue(playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds.isEmpty())
    }
}

val songIdsToBeAdded = testSongs.map { it.id }