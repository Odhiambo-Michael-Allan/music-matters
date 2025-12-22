package com.squad.musicmatters.ui.tree

import com.squad.musicmatters.core.data.preferences.SortPathsBy
import com.squad.musicmatters.core.data.preferences.SortSongsBy
import com.squad.musicmatters.core.data.preferences.impl.SettingsDefaults
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.data.settings.SettingsRepository
import com.squad.musicmatters.core.testing.connection.TestMusicServiceConnection
import com.squad.musicmatters.core.testing.playlist.FakePlaylistRepository
import com.squad.musicmatters.core.testing.playlists.testPlaylistInfos
import com.squad.musicmatters.core.testing.songs.testSongs
import com.squad.musicmatters.core.testing.tree.testPaths
import com.squad.musicmatters.core.i8n.Belarusian
import com.squad.musicmatters.core.i8n.Chinese
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.French
import com.squad.musicmatters.core.i8n.German
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.i8n.Spanish
import com.squad.musicmatters.core.media.media.extensions.toMediaItem
import com.squad.musicmatters.core.model.ThemeMode
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.io.path.Path
import kotlin.io.path.name

@RunWith( RobolectricTestRunner::class )
class TreeScreenViewModelTest {

    private lateinit var musicServiceConnection: TestMusicServiceConnection
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
    private lateinit var viewModel: TreeScreenViewModel

    @Before
    fun setup() {
        musicServiceConnection = TestMusicServiceConnection()
        settingsRepository = FakeSettingsRepository()
        playlistRepository = FakePlaylistRepository()
        songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
        viewModel = TreeScreenViewModel(
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = FakeSongsAdditionalMetadataRepository()
        )
    }

    @Test
    fun testTreeIsProperlyConstructed() {
        assertEquals( 5, viewModel.uiState.value.tree.keys.size )
        assertEquals( testSongs.size, viewModel.uiState.value.songsCount )
        assertTrue( viewModel.uiState.value.isConstructingTree )
        musicServiceConnection.setIsInitialized()
        assertFalse( viewModel.uiState.value.isConstructingTree )
    }

    @Test
    fun testCurrentlyPlayingSongIdIsCorrectlyUpdated() {
        musicServiceConnection.setNowPlaying( testSongs.first().toMediaItem() )
        assertEquals( testSongs.first().toMediaItem().mediaId, viewModel.uiState.value.currentlyPlayingSongId )
    }

    @Test
    fun testLanguageChange() {
        assertEquals( "Settings",
            viewModel.uiState.value.language.settings )
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
    fun testFavoriteSongsChange() = runTest {
        assertEquals( 0, viewModel.uiState.value.favoriteSongIds.size )
        testSongs.forEach {
            playlistRepository.addToFavorites( it.id )
        }
        assertEquals( testSongs.size, viewModel.uiState.value.favoriteSongIds.size )
    }

    @Test
    fun testDisabledTreePathsChange() = runTest {
        assertEquals( 0, viewModel.uiState.value.disabledTreePaths.size )
        settingsRepository.setCurrentlyDisabledTreePaths( testPaths.subList( 0, 3 ) )
        assertEquals( 3, viewModel.uiState.value.disabledTreePaths.size )
    }

    @Test
    fun testTogglePath() = runTest {
        settingsRepository.setCurrentlyDisabledTreePaths( testPaths )
        viewModel.togglePath( testPaths.first() )
        assertEquals( testPaths.size - 1, viewModel.uiState.value.disabledTreePaths.size )
        assertEquals( testPaths.size - 1, settingsRepository.currentlyDisabledTreePaths.value.size )
        viewModel.togglePath( testPaths.first() )
        assertEquals( testPaths.size, viewModel.uiState.value.disabledTreePaths.size )
        assertEquals( testPaths.size, settingsRepository.currentlyDisabledTreePaths.value.size )
    }

    @Test
    fun testPath() {
        val path1 = Path( testPaths.first() )
        val path2 = Path( testPaths[1] )
        val path3 = Path( testPaths[2] )
        val path4 = Path( testPaths[3] )
        val path5 = Path( testPaths[4] )
        assertEquals(
            "/storage/emulated/0/Music/Madeon/All My Friends",
            path1.directoryName()
        )
        assertEquals(
            "Madeon - All My Friends.mp3".trim(),
            path1.fileName.name
        )
        assertEquals(
            "/storage/emulated/0/Music/Bea Miller/elated!",
            path2.directoryName()
        )
        assertEquals(
            "/storage/emulated/0/Music/Tove Lo/Queen Of The Clouds",
            path3.directoryName()
        )
        assertEquals(
            "/storage/emulated/0/Music/Flume/Skin",
            path4.directoryName()
        )
        assertEquals(
            "/storage/emulated/0/Music/Sean Paul/I'm Still in Love with You",
            path5.directoryName()
        )
    }

    @Test
    fun testPlaylistsAreCorrectlyUpdated() = runTest {
        assertEquals( 1, viewModel.uiState.value.playlistInfos.size )
        testPlaylistInfos.forEach { playlistRepository.savePlaylist( it ) }
        assertEquals( 1 + testPlaylistInfos.size, viewModel.uiState.value.playlistInfos.size )
    }

    @Test
    fun testSongsAreCorrectlySorted() = runTest {
        musicServiceConnection.setIsInitialized()
        // --------- Sort by title ------------
        // Ascending
        musicServiceConnection.setSongs( testSongs )
        assertEquals( SettingsDefaults.sortSongsBy, viewModel.uiState.value.sortSongsBy )
        assertEquals( SettingsDefaults.SORT_SONGS_IN_REVERSE,
            viewModel.uiState.value.sortSongsInReverse
        )
        assertEquals(
            "Beat it",
            viewModel.uiState.value.tree[ "path/to/Thriller" ]!!.first().title
        )
        // Descending
        viewModel.setSortSongsInReverse( true )
        assertEquals(
            "Human Nature",
            viewModel.uiState.value.tree[ "path/to/Thriller" ]!!.first().title
        )
        // ----------- Sort by date added ------------
        // Descending
        viewModel.setSortSongsBy( SortSongsBy.DATE_ADDED )
        assertEquals(
            "Human Nature",
            viewModel.uiState.value.tree[ "path/to/Thriller"]!!.first().title
        )
        // Ascending
        viewModel.setSortSongsInReverse( false )
        assertEquals(
            "Beat it",
            viewModel.uiState.value.tree[ "path/to/Thriller"]!!.first().title
        )
    }

    @Test
    fun testPathsAreSortedCorrectly() = runTest {
        musicServiceConnection.setIsInitialized()
        // ------------ Sort by name ---------------
        // Ascending
        assertEquals( SortPathsBy.NAME, viewModel.uiState.value.sortPathsBy )
        assertFalse( viewModel.uiState.value.sortPathsInReverse )
        assertEquals(
            "path/to/It Takes a Nation of Millions to Hold Us Back",
            viewModel.uiState.value.tree.keys.first()
        )
        // Descending
        viewModel.setSortPathsInReverse( true )
        assertTrue( viewModel.uiState.value.sortPathsInReverse )
        assertEquals( "path/to/Thriller", viewModel.uiState.value.tree.keys.first() )

        // -------------- Sort by track count ------------------
        // Ascending
        viewModel.setSortPathsInReverse( false )
        viewModel.setSortPathsBy( SortPathsBy.TRACK_COUNT )
        assertEquals( SortPathsBy.TRACK_COUNT, viewModel.uiState.value.sortPathsBy )
        assertFalse( viewModel.uiState.value.sortPathsInReverse )
        assertEquals( "path/to/Speechless", viewModel.uiState.value.tree.keys.first() )
    }
}

