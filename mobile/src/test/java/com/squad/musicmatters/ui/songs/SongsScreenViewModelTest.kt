package com.squad.musicmatters.ui.songs

import com.squad.musicmatters.core.data.preferences.SortSongsBy
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
import com.squad.musicmatters.core.testing.songs.testSongsForSorting
import com.squad.musicmatters.core.i8n.Belarusian
import com.squad.musicmatters.core.i8n.Chinese
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.French
import com.squad.musicmatters.core.i8n.German
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.i8n.Spanish
import com.squad.musicmatters.core.model.ThemeMode
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class SongsScreenViewModelTest {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var musicServiceConnection: TestMusicServiceConnection
    private lateinit var viewModel: SongsScreenViewModel
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository

    @Before
    fun setup() {
        settingsRepository = FakeSettingsRepository()
        musicServiceConnection = TestMusicServiceConnection()
        playlistRepository = FakePlaylistRepository()
        songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
        viewModel = SongsScreenViewModel(
            settingsRepository = settingsRepository,
            musicServiceConnection = musicServiceConnection,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        )
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
    fun testSongsAreCorrectlyLoadedFromTheMusicServiceConnection() {
        assertTrue( viewModel.uiState.value.isLoading )
        musicServiceConnection.setIsInitialized()
        assertFalse( viewModel.uiState.value.isLoading )
        assertEquals( testSongs.size, viewModel.uiState.value.songs.size )
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
    fun testSongIsCorrectlyRemovedFromFavoriteList() = runTest {
        testSongs.forEach {
            playlistRepository.addToFavorites( it.id )
        }
        assertEquals( testSongs.size, viewModel.uiState.value.favoriteSongIds.size )
        viewModel.addToFavorites( testSongs.first().id )
        assertEquals( testSongs.size - 1, viewModel.uiState.value.favoriteSongIds.size )
    }

    @Test
    fun testSongsAreSortedCorrectly() = runTest {
        musicServiceConnection.setIsInitialized()

        // -- Sort by title --
        // Ascending
        musicServiceConnection.setSongs( testSongsForSorting )
        assertEquals( "id1", viewModel.uiState.value.songs.first().id )
        // Descending
        viewModel.setSortSongsInReverse( true )
        assertEquals( "id5", viewModel.uiState.value.songs.first().id )
        viewModel.setSortSongsInReverse( false )
        viewModel.setSortSongsBy( SortSongsBy.ALBUM )
        assertEquals( "id5", viewModel.uiState.value.songs.first().id )
        viewModel.setSortSongsBy( SortSongsBy.ARTIST )
        assertEquals( "id1", viewModel.uiState.value.songs.first().id )
        viewModel.setSortSongsBy( SortSongsBy.COMPOSER )
        assertEquals( "id5", viewModel.uiState.value.songs.first().id )
        viewModel.setSortSongsBy( SortSongsBy.DURATION )
        assertEquals( "id2", viewModel.uiState.value.songs.first().id )
        viewModel.setSortSongsBy( SortSongsBy.YEAR )
        assertEquals( "id5", viewModel.uiState.value.songs.first().id )
        viewModel.setSortSongsBy( SortSongsBy.DATE_ADDED )
        assertEquals( "id4", viewModel.uiState.value.songs.first().id )
        viewModel.setSortSongsBy( SortSongsBy.FILENAME )
        assertEquals( "id4", viewModel.uiState.value.songs.first().id )
        viewModel.setSortSongsBy( SortSongsBy.TRACK_NUMBER )
        assertEquals( "id5", viewModel.uiState.value.songs.first().id )
    }

    @Test
    fun testPlaylistsAreCorrectlyUpdated() = runTest {
        assertEquals( 1, viewModel.uiState.value.playlistInfos.size )
        testPlaylistInfos.forEach { playlistRepository.savePlaylist( it ) }
        assertEquals( 1 + testPlaylistInfos.size, viewModel.uiState.value.playlistInfos.size )
    }
}

