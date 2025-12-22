package com.squad.musicmatters.feature.songs

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.repository.impl.FAVORITES_PLAYLIST_ID
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.testing.connection.TestMusicServiceConnection
import com.squad.musicmatters.core.testing.repository.TestPlaylistRepository
import com.squad.musicmatters.core.testing.repository.TestPreferencesDataSource
import com.squad.musicmatters.core.testing.repository.TestSongsAdditionalMetadataRepository
import com.squad.musicmatters.core.testing.repository.TestSongsRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSongsForSorting
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.model.PlaylistInfo
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SongsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var preferencesDataSource: TestPreferencesDataSource
    private lateinit var musicServiceConnection: TestMusicServiceConnection
    private lateinit var viewModel: SongsScreenViewModel
    private lateinit var playlistRepository: TestPlaylistRepository
    private lateinit var songsRepository: TestSongsRepository
    private lateinit var songsAdditionalMetadataRepository: TestSongsAdditionalMetadataRepository

    @Before
    fun setup() {
        preferencesDataSource = TestPreferencesDataSource()
        musicServiceConnection = TestMusicServiceConnection()
        songsAdditionalMetadataRepository = TestSongsAdditionalMetadataRepository()
        songsRepository = TestSongsRepository()
        playlistRepository = TestPlaylistRepository()
        viewModel = SongsScreenViewModel(
            songsRepository = songsRepository,
            preferencesDataSource = preferencesDataSource,
            musicServiceConnection = musicServiceConnection,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        )
    }

    @Test
    fun testUiStateIsInitiallyLoading() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }
        assertEquals(
            SongsScreenUiState.Loading,
            viewModel.uiState.value
        )
    }

    @Test
    fun testUiStateIsUpdatedWhenThemeModeChanges() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }

        preferencesDataSource.sendUserData( emptyUserData )
        songsRepository.sendSongs( emptyList() )
        playlistRepository.sendPlaylists( emptyList() )
        songsAdditionalMetadataRepository.sendMetadata( emptyList() )

        assertEquals(
            SongsScreenUiState.Success(
                songs = emptyList(),
                sortSongsBy = DefaultPreferences.SORT_SONGS_BY,
                sortSongsInReverse = false,
                language = English,
                themeMode = DefaultPreferences.THEME_MODE,
                currentlyPlayingSongId = "",
                favoriteSongIds = emptySet(),
                playlists = emptyList(),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )
        ThemeMode.entries.forEach {
            preferencesDataSource.setThemeMode( it )
            assertEquals(
                SongsScreenUiState.Success(
                    songs = emptyList(),
                    sortSongsBy = DefaultPreferences.SORT_SONGS_BY,
                    sortSongsInReverse = false,
                    language = English,
                    themeMode = it,
                    currentlyPlayingSongId = "",
                    favoriteSongIds = emptySet(),
                    playlists = emptyList(),
                    songsAdditionalMetadata = emptyList()
                ),
                viewModel.uiState.value
            )
        }
    }

    @Test
    fun testSongsAreCorrectlyLoadedFromTheMusicServiceConnection() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }

        preferencesDataSource.sendUserData( emptyUserData )
        songsRepository.sendSongs( testSongsForSorting )
        playlistRepository.sendPlaylists( emptyList() )
        songsAdditionalMetadataRepository.sendMetadata( emptyList() )

        assertEquals(
            listOf( "id1", "id2", "id3", "id4", "id5" ),
            ( viewModel.uiState.value as SongsScreenUiState.Success ).songs.map { it.id }
        )

        var currentUserData = emptyUserData.copy( sortSongsReverse = true )
        preferencesDataSource.sendUserData( currentUserData )

        assertEquals(
            listOf( "id5", "id4", "id3", "id2", "id1" ),
            ( viewModel.uiState.value as SongsScreenUiState.Success ).songs.map { it.id }
        )

        currentUserData = currentUserData.copy( sortSongsBy = SortSongsBy.ALBUM )
        preferencesDataSource.sendUserData( currentUserData )

        assertEquals(
            listOf( "id1", "id2", "id3", "id4", "id5" ),
            ( viewModel.uiState.value as SongsScreenUiState.Success ).songs.map { it.id }
        )

        currentUserData = currentUserData.copy( sortSongsReverse = false )
        preferencesDataSource.sendUserData( currentUserData )

        assertEquals(
            listOf( "id5", "id4", "id3", "id2", "id1" ),
            ( viewModel.uiState.value as SongsScreenUiState.Success ).songs.map { it.id }
        )
    }

    @Test
    fun testPlayerStateIsCorrectlyUpdated() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }

        preferencesDataSource.sendUserData( emptyUserData )
        songsRepository.sendSongs( emptyList() )
        playlistRepository.sendPlaylists( emptyList() )
        songsAdditionalMetadataRepository.sendMetadata( emptyList() )

        assertEquals(
            SongsScreenUiState.Success(
                songs = emptyList(),
                sortSongsBy = DefaultPreferences.SORT_SONGS_BY,
                sortSongsInReverse = false,
                language = English,
                themeMode = DefaultPreferences.THEME_MODE,
                currentlyPlayingSongId = "",
                favoriteSongIds = emptySet(),
                playlists = emptyList(),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )

        musicServiceConnection.sendPlayerState(
            PlayerState(
                currentlyPlayingSongId = "song-id-1"
            )
        )

        assertEquals(
            SongsScreenUiState.Success(
                songs = emptyList(),
                sortSongsBy = DefaultPreferences.SORT_SONGS_BY,
                sortSongsInReverse = false,
                language = English,
                themeMode = DefaultPreferences.THEME_MODE,
                currentlyPlayingSongId = "song-id-1",
                favoriteSongIds = emptySet(),
                playlists = emptyList(),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun testFavoriteSongsAreCorrectlyUpdated() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }

        preferencesDataSource.sendUserData( emptyUserData )
        songsRepository.sendSongs( emptyList() )
        playlistRepository.sendPlaylists( emptyList() )
        songsAdditionalMetadataRepository.sendMetadata( emptyList() )

        testSongsForSorting.forEach {
            playlistRepository.addToFavorites( it.id )
        }
        assertEquals(
            SongsScreenUiState.Success(
                songs = emptyList(),
                sortSongsBy = DefaultPreferences.SORT_SONGS_BY,
                sortSongsInReverse = false,
                language = English,
                themeMode = DefaultPreferences.THEME_MODE,
                currentlyPlayingSongId = "",
                favoriteSongIds = testSongsForSorting.map { it.id }.toSet(),
                playlists = listOf(
                    PlaylistInfo(
                        id = FAVORITES_PLAYLIST_ID,
                        title = "",
                        songIds = testSongsForSorting.map { it.id }.toSet(),
                    )
                ),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )

    }

}

