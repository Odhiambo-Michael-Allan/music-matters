package com.odesa.musicMatters.ui.playlists

import com.odesa.musicMatters.core.data.preferences.SortPlaylistsBy
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.connection.FakeMusicServiceConnection
import com.odesa.musicMatters.core.datatesting.playlist.FakePlaylistRepository
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistsForSorting
import com.odesa.musicMatters.core.datatesting.repository.FakeSettingsRepository
import com.odesa.musicMatters.core.datatesting.repository.FakeSongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.Belarusian
import com.odesa.musicMatters.core.i8n.Chinese
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.French
import com.odesa.musicMatters.core.i8n.German
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.i8n.Spanish
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class PlaylistsViewModelTest {

    private lateinit var musicServiceConnection: FakeMusicServiceConnection
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
    private lateinit var viewModel: PlaylistsViewModel

    @Before
    fun setup() {
        musicServiceConnection = FakeMusicServiceConnection()
        settingsRepository = FakeSettingsRepository()
        playlistRepository = FakePlaylistRepository()
        songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
        viewModel = PlaylistsViewModel(
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        )
    }

    @Test
    fun testPlaylistsAreCorrectlyLoaded() {
        musicServiceConnection.setIsInitialized()
        assertEquals( testSongs.size, viewModel.uiState.value.songs.size )
        assertEquals( 3, viewModel.uiState.value.playlistInfos.size )
        assertFalse( viewModel.uiState.value.isLoadingSongs )
    }

    @Test
    fun testPlaylistsAreCorrectlyUpdated() = runTest {
        musicServiceConnection.setIsInitialized()
        playlistRepository.savePlaylist( testPlaylistInfos.first() )
        assertEquals( 4, viewModel.uiState.value.playlistInfos.size )
        playlistRepository.deletePlaylist( testPlaylistInfos.first() )
        assertEquals( 3, viewModel.uiState.value.playlistInfos.size )
    }

    @Test
    fun testPlaylistIsCorrectlyUpdated() = runTest {
        musicServiceConnection.setIsInitialized()
        playlistRepository.playlists.value.forEach {
            playlistRepository.addSongIdToPlaylist( testSongs.first().id, it.id )
        }
        viewModel.uiState.value.playlistInfos.forEach {
            assertTrue( it.songIds.contains( testSongs.first().id ) )
        }
    }

    @Test
    fun testLanguageChange() {
        assertEquals( "Settings", viewModel.uiState.value.language.settings )
        changeLanguageTo( Belarusian, "Налады" )
        changeLanguageTo( Chinese, "设置" )
        changeLanguageTo( English, "Settings" )
        changeLanguageTo( French, "Paramètres" )
        changeLanguageTo( German, "Einstellungen" )
        changeLanguageTo( Spanish, "Configuración" )
    }

    private fun changeLanguageTo( language: Language, testString: String ) = runTest {
        settingsRepository.setLanguage( language.locale )
        val currentLanguage = viewModel.uiState.value.language
        assertEquals( testString, currentLanguage.settings )
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
    fun testPlaylistsAreSortedCorrectly() = runTest {
        testPlaylistsForSorting.forEach { playlistRepository.savePlaylist( it ) }
        // --------- Sort by title ------------
        // Ascending
        assertEquals( "Favorites", viewModel.uiState.value.playlistInfos.first().title )
        assertEquals( SortPlaylistsBy.TITLE, viewModel.uiState.value.sortPlaylistsBy )
        assertFalse( viewModel.uiState.value.sortPlaylistsInReverse )
        // Descending
        viewModel.setSortPlaylistsInReverse( true )
        assertEquals( "Recently Played Songs", viewModel.uiState.value.playlistInfos.first().title )
        assertTrue( viewModel.uiState.value.sortPlaylistsInReverse )
        // -------- Sort by track count ----------
        // Descending
        viewModel.setSortPlaylistsBy( SortPlaylistsBy.TRACKS_COUNT )
        assertEquals( SortPlaylistsBy.TRACKS_COUNT, viewModel.uiState.value.sortPlaylistsBy )
        assertEquals( "Playlist-19", viewModel.uiState.value.playlistInfos.first().title )
        // Ascending
        viewModel.setSortPlaylistsInReverse( false )
        assertFalse( viewModel.uiState.value.sortPlaylistsInReverse )
        assertEquals( "Favorites", viewModel.uiState.value.playlistInfos.first().title )
    }
}