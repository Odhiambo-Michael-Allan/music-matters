package com.odesa.musicMatters.ui.playlist

import com.odesa.musicMatters.MainCoroutineRule
import com.odesa.musicMatters.core.data.preferences.SortSongsBy
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.connection.FakeMusicServiceConnection
import com.odesa.musicMatters.core.datatesting.playlist.FakePlaylistRepository
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.datatesting.repository.FakeSettingsRepository
import com.odesa.musicMatters.core.datatesting.repository.FakeSongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.datatesting.songs.id1
import com.odesa.musicMatters.core.datatesting.songs.testSongMediaItemsForId
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.datatesting.songs.testSongsForSorting
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class PlaylistInfoScreenViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var musicServiceConnection: FakeMusicServiceConnection
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
    private lateinit var viewModel: PlaylistScreenViewModel

    @Before
    fun setup() {
        musicServiceConnection = FakeMusicServiceConnection()
        settingsRepository = FakeSettingsRepository()
        playlistRepository = FakePlaylistRepository()
        songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
        viewModel = PlaylistScreenViewModel(
            playlistId = playlistRepository.favoritesPlaylistInfo.value.id,
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        )
    }

    @Test
    fun testLoadSongsInPlaylistWithGivenId() = runTest {
        assertEquals( 0, viewModel.uiState.value.songsInPlaylist.size )
        assertTrue( viewModel.uiState.value.isLoadingSongsInPlaylist )
        testSongs.subList( 0, 3 ).forEach {
            playlistRepository.addToFavorites( it.id )
        }
        musicServiceConnection.setIsInitialized()
        assertEquals( 3, viewModel.uiState.value.songsInPlaylist.size )
        assertFalse( viewModel.uiState.value.isLoadingSongsInPlaylist )
    }

    @Test
    fun testNowPlayingMediaItemIsCorrectlyUpdated() {
        assertEquals( "", viewModel.uiState.value.currentlyPlayingSongId )
        musicServiceConnection.setNowPlaying( testSongMediaItemsForId.first() )
        assertEquals( id1, viewModel.uiState.value.currentlyPlayingSongId )
    }

    @Test
    fun testFavoriteSongsChange() = runTest {
        assertEquals( 0, viewModel.uiState.value.favoriteSongIds.size )
        testSongs.forEach {
            playlistRepository.addToFavorites( it.id )
        }
        assertEquals( testSongs.size, viewModel.uiState.value.favoriteSongIds.size )
    }

    @Test
    fun testPlaylistsAreCorrectlyUpdated() = runTest {
        assertEquals( 1, viewModel.uiState.value.playlistInfos.size )
        testPlaylistInfos.forEach { playlistRepository.savePlaylist( it ) }
        assertEquals( 1 + testPlaylistInfos.size, viewModel.uiState.value.playlistInfos.size )
    }

    fun testSongsAreSortedCorrectly() = runTest {
        musicServiceConnection.setIsInitialized()

        // -- Sort by title --
        // Ascending
        assertEquals( SettingsDefaults.sortSongsBy, viewModel.uiState.value.sortSongsBy )
        assertEquals(
            SettingsDefaults.SORT_SONGS_IN_REVERSE,
            viewModel.uiState.value.sortSongsInReverse
        )
        musicServiceConnection.setSongs( testSongsForSorting )
        assertEquals( "id1", viewModel.uiState.value.songsInPlaylist.first().id )
        // Descending
        viewModel.setSortSongsInReverse( true )
        assertTrue( viewModel.uiState.value.sortSongsInReverse )
        assertEquals( "id5", viewModel.uiState.value.songsInPlaylist.first().id )
        viewModel.setSortSongsInReverse( false )
        assertFalse( viewModel.uiState.value.sortSongsInReverse )
        viewModel.setSortSongsBy( SortSongsBy.ALBUM )
        assertEquals( SortSongsBy.ALBUM, viewModel.uiState.value.sortSongsBy )
        assertEquals( "id5", viewModel.uiState.value.songsInPlaylist.first().id )
    }

}