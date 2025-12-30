package com.squad.musicmatters.ui.search

//import com.squad.musicmatters.core.data.preferences.impl.SettingsDefaults
//import com.squad.musicmatters.core.data.repository.PlaylistRepository
//import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
//import com.squad.musicmatters.core.data.search.SearchHistoryRepository
//import com.squad.musicmatters.core.data.settings.SettingsRepository
//import com.squad.musicmatters.core.testing.connection.TestMusicServiceConnection
//import com.squad.musicmatters.core.testing.playlist.FakePlaylistRepository
//import com.squad.musicmatters.core.testing.search.testSearchHistoryItems
//import com.squad.musicmatters.core.testing.songs.id1
//import com.squad.musicmatters.core.testing.songs.testSongMediaItemsForId
//import com.squad.musicmatters.core.i8n.Belarusian
//import com.squad.musicmatters.core.i8n.Chinese
//import com.squad.musicmatters.core.i8n.English
//import com.squad.musicmatters.core.i8n.French
//import com.squad.musicmatters.core.i8n.German
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.i8n.Spanish
//import com.squad.musicmatters.core.model.ThemeMode
//import junit.framework.TestCase.assertEquals
//import junit.framework.TestCase.assertFalse
//import junit.framework.TestCase.assertTrue
//import kotlinx.coroutines.test.runTest
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//
//@RunWith( RobolectricTestRunner::class )
//class SearchScreenViewModelTest {
//
//    private lateinit var musicServiceConnection: TestMusicServiceConnection
//    private lateinit var playlistRepository: PlaylistRepository
//    private lateinit var settingsRepository: SettingsRepository
//    private lateinit var searchHistoryRepository: SearchHistoryRepository
//    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
//    private lateinit var viewModel: SearchScreenViewModel
//
//    @Before
//    fun setup() {
//        musicServiceConnection = TestMusicServiceConnection()
//        playlistRepository = FakePlaylistRepository()
//        settingsRepository = FakeSettingsRepository()
//        searchHistoryRepository = FakeSearchHistoryRepository()
//        songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
//        viewModel = SearchScreenViewModel(
//            musicServiceConnection = musicServiceConnection,
//            playlistRepository = playlistRepository,
//            settingsRepository = settingsRepository,
//            searchHistoryRepository = searchHistoryRepository,
//            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//        )
//    }
//
//    @Test
//    fun testSearchHistoryItemsAreSavedCorrectly() = runTest {
//        assertEquals( 0, viewModel.uiState.value.searchHistoryItems.size )
//        testSearchHistoryItems.forEach {
//            viewModel.saveSearchHistoryItem( it )
//        }
//        assertEquals( testSearchHistoryItems.size, viewModel.uiState.value.searchHistoryItems.size )
//    }
//
//    @Test
//    fun testSearchHistoryItemsAreDeletedCorrectly() = runTest {
//        testSearchHistoryItems.forEach {
//            viewModel.saveSearchHistoryItem( it )
//        }
//        viewModel.deleteSearchHistoryItem( testSearchHistoryItems.first() )
//        viewModel.deleteSearchHistoryItem( testSearchHistoryItems.last() )
//        assertEquals( testSearchHistoryItems.size - 2, viewModel.uiState.value.searchHistoryItems.size )
//    }
//
//    @Test
//    fun testLanguageChange() {
//        assertEquals( "Settings", viewModel.uiState.value.language.settings )
//        changeLanguageTo( Belarusian, "Налады" )
//        changeLanguageTo( Chinese, "设置" )
//        changeLanguageTo( English, "Settings" )
//        changeLanguageTo( French, "Paramètres" )
//        changeLanguageTo( German, "Einstellungen" )
//        changeLanguageTo( Spanish, "Configuración" )
//    }
//
//    private fun changeLanguageTo( language: Language, testString: String ) = runTest {
//        settingsRepository.setLanguage( language.locale )
//        val currentLanguage = viewModel.uiState.value.language
//        assertEquals( testString, currentLanguage.settings )
//    }
//
//    @Test
//    fun testThemeModeChange() = runTest {
//        assertEquals( SettingsDefaults.themeMode, viewModel.uiState.value.themeMode )
//        ThemeMode.entries.forEach {
//            settingsRepository.setThemeMode( it )
//            assertEquals( it, viewModel.uiState.value.themeMode )
//        }
//    }
//
//    @Test
//    fun testMusicServiceConnectionInitializationStatusIsCorrectlyUpdated() {
//        assertTrue( viewModel.uiState.value.isLoadingSearchHistory )
//        musicServiceConnection.setIsInitialized()
//        assertFalse( viewModel.uiState.value.isLoadingSearchHistory )
//    }
//
//    @Test
//    fun testNowPlayingMediaItemIsCorrectlyUpdated() {
//        assertEquals( "", viewModel.uiState.value.currentlyPlayingSongId )
//        musicServiceConnection.setNowPlaying( testSongMediaItemsForId.first() )
//        assertEquals( id1, viewModel.uiState.value.currentlyPlayingSongId )
//    }
//}