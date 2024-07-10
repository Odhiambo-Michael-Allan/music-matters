package com.odesa.musicMatters.ui.forYou

import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.connection.FakeMusicServiceConnection
import com.odesa.musicMatters.core.datatesting.playlist.FakePlaylistRepository
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.datatesting.repository.FakeSettingsRepository
import com.odesa.musicMatters.core.datatesting.repository.FakeSongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.datatesting.songs.testSongs
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
class ForYouScreenViewModelTest {

    private lateinit var musicServiceConnection: FakeMusicServiceConnection
    private lateinit var playlistRepository: FakePlaylistRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
    private lateinit var viewModel: ForYouScreenViewModel

    @Before
    fun setup() {
        musicServiceConnection = FakeMusicServiceConnection()
        playlistRepository = FakePlaylistRepository()
        settingsRepository = FakeSettingsRepository()
        songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
        viewModel = ForYouScreenViewModel(
            musicServiceConnection = musicServiceConnection,
            playlistRepository = playlistRepository,
            settingsRepository = settingsRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        )
    }

    @Test
    fun testRecentlyAddedSongsAreCorrectlyLoadedFromMusicServiceConnection() {
        assertTrue( viewModel.uiState.value.isLoadingRecentSongs )
        musicServiceConnection.setIsInitialized()
        assertFalse( viewModel.uiState.value.isLoadingRecentSongs )
        assertEquals( 5, viewModel.uiState.value.recentlyAddedSongs.size )
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
    fun testSuggestedAlbumsAreCorrectlyLoadedFromMusicServiceConnection() {
        assertTrue( viewModel.uiState.value.isLoadingSuggestedAlbums )
        musicServiceConnection.setIsInitialized()
        assertFalse( viewModel.uiState.value.isLoadingSuggestedAlbums )
        assertEquals( 5, viewModel.uiState.value.suggestedAlbums.size )
    }

    @Test
    fun testSuggestedArtistsAreCorrectlyLoadedFromMusicServiceConnection() {
        assertTrue( viewModel.uiState.value.isLoadingSuggestedArtists )
        musicServiceConnection.setIsInitialized()
        assertFalse( viewModel.uiState.value.isLoadingSuggestedArtists )
        assertEquals( 5, viewModel.uiState.value.suggestedArtists.size )
    }

    @Test
    fun testRecentlyPlayedSongsAreCorrectlyUpdated() = runTest {
        musicServiceConnection.setIsInitialized()
        assertEquals( 0, viewModel.uiState.value.recentlyPlayedSongs.size )
        assertFalse( viewModel.uiState.value.isLoadingRecentlyPlayedSongs )
        testSongs.forEach {
            playlistRepository.addToRecentlyPlayedSongsPlaylist( it.id )
        }
        assertEquals( 5, viewModel.uiState.value.recentlyPlayedSongs.size )
        assertFalse( viewModel.uiState.value.isLoadingRecentlyPlayedSongs )
        playlistRepository.addToRecentlyPlayedSongsPlaylist( testSongs.last().id )
        assertEquals( testSongs.last().id, viewModel.uiState.value.recentlyPlayedSongs.first().id )
    }

    @Test
    fun testShuffleAndPlay() = runTest {
        musicServiceConnection.setIsInitialized()
        viewModel.shuffleAndPlay( songs = testSongs )
        assertEquals( testSongs.size, musicServiceConnection.mediaItemsInQueue.value.size )
    }

    @Test
    fun testPlaylistsAreCorrectlyUpdated() = runTest {
        assertEquals( 1, viewModel.uiState.value.playlistInfos.size )
        testPlaylistInfos.forEach { playlistRepository.savePlaylist( it ) }
        assertEquals( 1 + testPlaylistInfos.size, viewModel.uiState.value.playlistInfos.size )
    }

    @Test
    fun testMostPlayedSongsAreCorrectlyUpdated() = runTest {
        musicServiceConnection.setIsInitialized()
        assertEquals( 0, viewModel.uiState.value.mostPlayedSongs.size )
        assertFalse( viewModel.uiState.value.isLoadingMostPlayedSongs )
        testSongs.forEach {
            playlistRepository.addToMostPlayedPlaylist( it.id )
        }
        assertEquals( 5, viewModel.uiState.value.mostPlayedSongs.size )
//        playlistRepository.addToMostPlayedPlaylist( testSongs.last().id )
//        assertEquals( testSongs.last().id, viewModel.uiState.value.mostPlayedSongs.first().id )
    }
}