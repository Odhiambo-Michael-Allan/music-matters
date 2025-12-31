package com.squad.musicmatters.feature.queue

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.repository.impl.FAVORITES_PLAYLIST_ID
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.testing.connection.TestMusicServiceConnection
import com.squad.musicmatters.core.testing.repository.TestPlaylistRepository
import com.squad.musicmatters.core.testing.repository.TestPreferencesDataSource
import com.squad.musicmatters.core.testing.repository.TestQueueRepository
import com.squad.musicmatters.core.testing.repository.TestSongsAdditionalMetadataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QueueScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var preferencesDataSource: TestPreferencesDataSource
    private lateinit var playlistRepository: TestPlaylistRepository
    private lateinit var musicServiceConnection: TestMusicServiceConnection
    private lateinit var metadataRepository: TestSongsAdditionalMetadataRepository
    private lateinit var queueRepository: TestQueueRepository
    private lateinit var viewModel: QueueScreenViewModel

    @Before
    fun setup() {
        preferencesDataSource = TestPreferencesDataSource()
        playlistRepository = TestPlaylistRepository()
        musicServiceConnection = TestMusicServiceConnection()
        queueRepository = TestQueueRepository()
        metadataRepository = TestSongsAdditionalMetadataRepository()
        viewModel = QueueScreenViewModel(
            queueRepository = queueRepository,
            preferencesDataSource = preferencesDataSource,
            playlistRepository = playlistRepository,
            player = musicServiceConnection,
            metadataRepository = metadataRepository
        )
    }

    @Test
    fun testUiStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }
        assertEquals(
            QueueScreenUiState.Loading,
            viewModel.uiState.value
        )
    }

    @Test
    fun testUiStateIsSuccessWhenAllRequiredFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )
        queueRepository.sendSongs( songs )
        preferencesDataSource.sendUserData(
            emptyUserData.copy( currentlyPlayingSongId = "song-id-3" )
        )
        playlistRepository.sendPlaylists( emptyList() )
        playlistRepository.addToFavorites( testSong( "song-id-3" ) )
        metadataRepository.sendMetadata( emptyList() )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
                favoriteSongIds = setOf( "song-id-3" ),
                playlists = listOf(
                    Playlist(
                        id = FAVORITES_PLAYLIST_ID,
                        title = "",
                        songIds = setOf( "song-id-3" )
                    )
                ),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun testSongsInQueueChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )
        queueRepository.sendSongs( songs )
        preferencesDataSource.sendUserData(
            emptyUserData.copy( currentlyPlayingSongId = "song-id-3" )
        )
        playlistRepository.sendPlaylists( emptyList() )
        playlistRepository.addToFavorites( testSong( "song-id-3" ) )
        metadataRepository.sendMetadata( emptyList() )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
                favoriteSongIds = setOf( "song-id-3" ),
                playlists = listOf(
                    Playlist(
                        id = FAVORITES_PLAYLIST_ID,
                        title = "",
                        songIds = setOf( "song-id-3" )
                    )
                ),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )

        queueRepository.clearQueue()

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = emptyList(),
                currentlyPlayingSongId = "song-id-3",
                favoriteSongIds = setOf( "song-id-3" ),
                playlists = listOf(
                    Playlist(
                        id = FAVORITES_PLAYLIST_ID,
                        title = "",
                        songIds = setOf( "song-id-3" )
                    )
                ),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun testCurrentlyPlayingSongIdChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )
        queueRepository.sendSongs( songs )
        preferencesDataSource.sendUserData(
            emptyUserData.copy( currentlyPlayingSongId = "song-id-3" )
        )
        playlistRepository.sendPlaylists( emptyList() )
        playlistRepository.addToFavorites( testSong( "song-id-3" ) )
        metadataRepository.sendMetadata( emptyList() )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
                favoriteSongIds = setOf( "song-id-3" ),
                playlists = listOf(
                    Playlist(
                        id = FAVORITES_PLAYLIST_ID,
                        title = "",
                        songIds = setOf( "song-id-3" )
                    )
                ),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )

        preferencesDataSource.sendUserData(
            emptyUserData.copy(
                currentlyPlayingSongId = "song-id-4"
            )
        )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-4",
                favoriteSongIds = setOf( "song-id-3" ),
                playlists = listOf(
                    Playlist(
                        id = FAVORITES_PLAYLIST_ID,
                        title = "",
                        songIds = setOf( "song-id-3" )
                    )
                ),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun testFavoriteSongIdsChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )
        queueRepository.sendSongs( songs )
        preferencesDataSource.sendUserData(
            emptyUserData.copy( currentlyPlayingSongId = "song-id-3" )
        )
        playlistRepository.sendPlaylists( emptyList() )
        playlistRepository.addToFavorites( testSong( "song-id-3" ) )
        metadataRepository.sendMetadata( emptyList() )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
                favoriteSongIds = setOf( "song-id-3" ),
                playlists = listOf(
                    Playlist(
                        id = FAVORITES_PLAYLIST_ID,
                        title = "",
                        songIds = setOf( "song-id-3" )
                    )
                ),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )

        playlistRepository.removeFromFavorites( "song-id-3" )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
                favoriteSongIds = emptySet(),
                playlists = listOf(
                    Playlist(
                        id = FAVORITES_PLAYLIST_ID,
                        title = "",
                        songIds = emptySet()
                    )
                ),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun testPlaylistsChange() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { viewModel.uiState.collect() }
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )
        queueRepository.sendSongs( songs )
        preferencesDataSource.sendUserData(
            emptyUserData.copy( currentlyPlayingSongId = "song-id-3" )
        )
        playlistRepository.sendPlaylists( emptyList() )
        playlistRepository.addToFavorites( testSong( "song-id-3" ) )
        metadataRepository.sendMetadata( emptyList() )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
                favoriteSongIds = setOf( "song-id-3" ),
                playlists = listOf(
                    Playlist(
                        id = FAVORITES_PLAYLIST_ID,
                        title = "",
                        songIds = setOf( "song-id-3" )
                    )
                ),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )

        val additionalPlaylist = Playlist(
            id = "2",
            title = "Additional Playlist",
            songIds = setOf( "song-id-1" )
        )
        playlistRepository.savePlaylist(
            id = additionalPlaylist.id,
            playlistName = additionalPlaylist.title,
            songsInPlaylist = additionalPlaylist.songIds.map { id -> testSong( id ) }
        )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
                favoriteSongIds = setOf( "song-id-3" ),
                playlists = listOf(
                    Playlist(
                        id = FAVORITES_PLAYLIST_ID,
                        title = "",
                        songIds = setOf( "song-id-3" )
                    ),
                    additionalPlaylist,
                ),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )
    }

}