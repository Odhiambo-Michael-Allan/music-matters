package com.odesa.musicMatters.ui.queue

import com.odesa.musicMatters.core.common.media.extensions.artistTagSeparators
import com.odesa.musicMatters.core.common.media.extensions.toSong
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
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class QueueScreenViewModelTest {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var musicServiceConnection: FakeMusicServiceConnection
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
    private lateinit var viewModel: QueueScreenViewModel

    @Before
    fun setup() {
        settingsRepository = FakeSettingsRepository()
        playlistRepository = FakePlaylistRepository()
        musicServiceConnection = FakeMusicServiceConnection()
        songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
        viewModel = QueueScreenViewModel(
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            musicServiceConnection = musicServiceConnection,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        )
    }

    @Test
    fun testMediaItemsChange() {
        musicServiceConnection.setMediaItems( emptyList() )
        assertEquals( 0, viewModel.uiState.value.songsInQueue.size )
        musicServiceConnection.setMediaItems( testSongMediaItemsForId )
        assertEquals( testSongMediaItemsForId.size,
            viewModel.uiState.value.songsInQueue.size )
    }

    @Test
    fun testNowPlayingMediaItemIsCorrectlyUpdated() {
        assertEquals("", viewModel.uiState.value.currentlyPlayingSongId)
        musicServiceConnection.setNowPlaying( testSongMediaItemsForId.first() )
        assertEquals( id1, viewModel.uiState.value.currentlyPlayingSongId )
    }

    @Test
    fun testThemeModeChange() = runTest {
        assertEquals( SettingsDefaults.themeMode, viewModel.uiState.value.themeMode )
        ThemeMode.entries.forEach {
            settingsRepository.setThemeMode( it )
            assertEquals( it, viewModel.uiState.value.themeMode )
        }
    }

    @Test
    fun testAddSongToFavorites() {
        viewModel.addToFavorites( testSongs.first().id )
        assertEquals( 1, viewModel.uiState.value.favoriteSongIds.size )
        viewModel.addToFavorites( testSongs.first().id )
        assertEquals( 0, viewModel.uiState.value.favoriteSongIds.size )
    }

    @Test
    fun testFavoriteSongsChange() = runTest {
        assertEquals(0, viewModel.uiState.value.favoriteSongIds.size )
        testSongs.forEach {
            playlistRepository.addToFavorites( it.id )
        }
        assertEquals(
            testSongs.size,
            viewModel.uiState.value.favoriteSongIds.size
        )
    }

    @Test
    fun testClearQueue() {
        musicServiceConnection.setMediaItems( testSongMediaItemsForId )
        assertEquals( testSongMediaItemsForId.size, viewModel.uiState.value.songsInQueue.size )
        viewModel.clearQueue()
        assertEquals( 0, viewModel.uiState.value.songsInQueue.size )
    }

    @Test
    fun testSaveCurrentPlaylist() {
        val playlistName = "playlist-1"
        musicServiceConnection.setMediaItems( testSongMediaItemsForId )
        viewModel.createPlaylist( playlistName, testSongMediaItemsForId.map { it.toSong( artistTagSeparators ) } )
        assertEquals( 4, playlistRepository.playlists.value.size )
    }

    @Test
    fun testCurrentlyPlayingSongIndexIsCorrectlyUpdated() {
        assertEquals( 0,
            viewModel.uiState.value.currentlyPlayingSongIndex )
        musicServiceConnection.setCurrentMediaItemIndex( 10 )
        assertEquals( 10,
            viewModel.uiState.value.currentlyPlayingSongIndex )
    }

    @Test
    fun testPlaylistsAreCorrectlyUpdated() = runTest {
        assertEquals( 1, viewModel.uiState.value.playlistInfos.size )
        testPlaylistInfos.forEach { playlistRepository.savePlaylist( it ) }
        assertEquals( 1 + testPlaylistInfos.size, viewModel.uiState.value.playlistInfos.size )
    }

}