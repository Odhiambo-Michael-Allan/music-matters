package com.squad.musicmatters.ui.queue

import com.squad.musicmatters.core.media.media.extensions.artistTagSeparators
import com.squad.musicmatters.core.media.media.extensions.toSong
import com.squad.musicmatters.core.data.preferences.impl.SettingsDefaults
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.data.settings.SettingsRepository
import com.squad.musicmatters.core.testing.connection.TestMusicServiceConnection
import com.squad.musicmatters.core.testing.playlist.FakePlaylistRepository
import com.squad.musicmatters.core.testing.playlists.testPlaylistInfos
import com.squad.musicmatters.core.testing.songs.id1
import com.squad.musicmatters.core.testing.songs.testSongMediaItemsForId
import com.squad.musicmatters.core.testing.songs.testSongs
import com.squad.musicmatters.core.model.ThemeMode
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
    private lateinit var musicServiceConnection: TestMusicServiceConnection
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
    private lateinit var viewModel: QueueScreenViewModel

    @Before
    fun setup() {
        settingsRepository = FakeSettingsRepository()
        playlistRepository = FakePlaylistRepository()
        musicServiceConnection = TestMusicServiceConnection()
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
        assertEquals( 4, 0 )
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