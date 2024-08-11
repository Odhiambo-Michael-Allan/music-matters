package com.odesa.musicMatters.ui.genres

import com.odesa.musicMatters.core.data.preferences.SortGenresBy
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.connection.FakeMusicServiceConnection
import com.odesa.musicMatters.core.datatesting.genres.testGenres
import com.odesa.musicMatters.core.datatesting.repository.FakeSettingsRepository
import com.odesa.musicMatters.core.datatesting.repository.FakeSongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.datatesting.songs.testSongMediaItems
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
class GenresScreenViewModelTest {

    private lateinit var musicServiceConnection: FakeMusicServiceConnection
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
    private lateinit var viewModel: GenresScreenViewModel

    @Before
    fun setup() {
        musicServiceConnection = FakeMusicServiceConnection()
        settingsRepository = FakeSettingsRepository()
        songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
        viewModel = GenresScreenViewModel(
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
        )
        musicServiceConnection.setMediaItems( testSongMediaItems )
    }

    @Test
    fun testGenresAreCorrectlyLoadedFromMusicServiceConnection() = runTest {
        assertTrue( viewModel.uiState.value.isLoading )
        musicServiceConnection.setIsInitialized()
        assertTrue( viewModel.uiState.value.isLoading )
        musicServiceConnection.setIsLoadingGenres( false )
        assertFalse( viewModel.uiState.value.isLoading )
        assertEquals( testGenres.size, viewModel.uiState.value.genres.size )
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
    fun testGenresAreCorrectlySorted() = runTest {
        assertEquals( SortGenresBy.NAME, viewModel.uiState.value.sortGenresBy )
        assertFalse( viewModel.uiState.value.sortGenresInReverse )
        assertEquals( "Hip Hop", viewModel.uiState.value.genres.first().name )
        viewModel.setSortGenresInReverse( true )
        assertTrue( viewModel.uiState.value.sortGenresInReverse )
        assertEquals( "Rock", viewModel.uiState.value.genres.first().name )
        viewModel.setSortGenresBy( SortGenresBy.TRACKS_COUNT )
        assertEquals( SortGenresBy.TRACKS_COUNT, viewModel.uiState.value.sortGenresBy )
        assertEquals( "Hip Hop", viewModel.uiState.value.genres.first().name )
    }
}