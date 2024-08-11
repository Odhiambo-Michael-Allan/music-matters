package com.odesa.musicMatters.ui.artists

import com.odesa.musicMatters.core.data.preferences.SortArtistsBy
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.artists.testArtistMediaItems
import com.odesa.musicMatters.core.datatesting.artists.testArtistsForSorting
import com.odesa.musicMatters.core.datatesting.connection.FakeMusicServiceConnection
import com.odesa.musicMatters.core.datatesting.playlist.FakePlaylistRepository
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.datatesting.repository.FakeSettingsRepository
import com.odesa.musicMatters.core.datatesting.repository.FakeSongsAdditionalMetadataRepository
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
class ArtistsViewModelTest {

    private lateinit var musicServiceConnection: FakeMusicServiceConnection
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var viewModel: ArtistsScreenViewModel
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository

    @Before
    fun setup() {
        musicServiceConnection = FakeMusicServiceConnection()
        settingsRepository = FakeSettingsRepository()
        playlistRepository = FakePlaylistRepository()
        songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
        viewModel = ArtistsScreenViewModel(
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        )
    }

    @Test
    fun testArtistsAreCorrectlyLoadedFromMusicServiceConnection() {
        assertTrue( viewModel.uiState.value.isLoadingArtists )
        musicServiceConnection.setIsInitialized()
        assertEquals( testArtistMediaItems.size, viewModel.uiState.value.artists.size )
        assertFalse( viewModel.uiState.value.isLoadingArtists )
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
    fun testSortArtistsByValueIsCorrectlySet() = runTest {
        assertEquals( SettingsDefaults.sortArtistsBy, viewModel.uiState.value.sortArtistsBy )
        SortArtistsBy.entries.forEach {
            viewModel.setSortArtistsBy( it )
            assertEquals( it, settingsRepository.sortArtistsBy.value )
            assertEquals( it, viewModel.uiState.value.sortArtistsBy )
        }
    }

    @Test
    fun testSortArtistsInReverseIsCorrectlySet() = runTest {
        assertFalse( viewModel.uiState.value.sortArtistsInReverse )
        viewModel.setSortArtistsInReverse( true )
        assertTrue( settingsRepository.sortArtistsInReverse.value )
        assertTrue( viewModel.uiState.value.sortArtistsInReverse )
        viewModel.setSortArtistsInReverse( false )
        assertFalse( settingsRepository.sortArtistsInReverse.value )
        assertFalse( viewModel.uiState.value.sortArtistsInReverse )
    }

    @Test
    fun testArtistsAreCorrectlySorted() = runTest {
        musicServiceConnection.setIsInitialized()
        musicServiceConnection.setArtists( testArtistsForSorting )

        assertEquals( "7 Developers and a Pastry Chef", viewModel.uiState.value.artists.first().name )

        viewModel.setSortArtistsInReverse( true )
        assertEquals( "Public Enemy", viewModel.uiState.value.artists.first().name )

        viewModel.setSortArtistsInReverse( false )
        viewModel.setSortArtistsBy( SortArtistsBy.TRACKS_COUNT )
        assertEquals( "7 Developers and a Pastry Chef", viewModel.uiState.value.artists.first().name )

        viewModel.setSortArtistsInReverse( true )
        assertEquals( "Michael Jackson", viewModel.uiState.value.artists.first().name )

        viewModel.setSortArtistsInReverse( false )
        viewModel.setSortArtistsBy( SortArtistsBy.ALBUMS_COUNT )
        assertEquals( "7 Developers and a Pastry Chef", viewModel.uiState.value.artists.first().name )

        viewModel.setSortArtistsInReverse( true )
        assertEquals( "Michael Jackson", viewModel.uiState.value.artists.first().name )
    }

    @Test
    fun testPlaylistsAreCorrectlyUpdated() = runTest {
        assertEquals( 1, viewModel.uiState.value.playlistInfos.size )
        testPlaylistInfos.forEach { playlistRepository.savePlaylist( it ) }
        assertEquals( 1 + testPlaylistInfos.size, viewModel.uiState.value.playlistInfos.size )
    }
}

