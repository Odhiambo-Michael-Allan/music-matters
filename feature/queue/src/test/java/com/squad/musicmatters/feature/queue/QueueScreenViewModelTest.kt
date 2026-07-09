package com.squad.musicmatters.feature.queue

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakePreferencesDataSource
import com.squad.musicmatters.core.testing.repository.FakePlaylistsRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsMetadataRepository
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

    private lateinit var preferencesDataSource: FakePreferencesDataSource
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var playlistRepository: FakePlaylistsRepository
    private lateinit var songsAdditionalMetadataRepository: FakeSongsMetadataRepository
    private lateinit var viewModel: QueueScreenViewModel

    @Before
    fun setup() {
        preferencesDataSource = FakePreferencesDataSource()
        player = FakeMusicMattersPlayer()
        songsRepository = FakeSongsRepository()
        playlistRepository = FakePlaylistsRepository()
        songsAdditionalMetadataRepository = FakeSongsMetadataRepository()
        viewModel = QueueScreenViewModel(
            songsRepository = songsRepository,
            playlistsRepository = playlistRepository,
            songsMetadataRepository = songsAdditionalMetadataRepository,
            preferencesDataSource = preferencesDataSource,
            player = player,
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
        backgroundScope.launch(
            UnconfinedTestDispatcher()
        ) {
            viewModel.uiState.collect()
        }
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )
        player.sendSongs( songs )
        songsRepository.sendSongs( songs )
        playlistRepository.sendPlaylists( emptyList() )
        songsAdditionalMetadataRepository.sendMetadata( emptyList() )
        preferencesDataSource.sendUserData(
            emptyUserData.copy( currentlyPlayingSongId = "song-id-3" )
        )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
                shuffle = false,
                favoriteSongIds = emptySet(),
                playlists = emptyList(),
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
        player.sendSongs( songs )
        songsRepository.sendSongs( songs )
        playlistRepository.sendPlaylists( emptyList() )
        songsAdditionalMetadataRepository.sendMetadata( emptyList() )
        preferencesDataSource.sendUserData(
            emptyUserData.copy( currentlyPlayingSongId = "song-id-3" )
        )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
                shuffle = false,
                favoriteSongIds = emptySet(),
                playlists = emptyList(),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )

        player.clearQueue()

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = emptyList(),
                currentlyPlayingSongId = "song-id-3",
                shuffle = false,
                favoriteSongIds = emptySet(),
                playlists = emptyList(),
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
        player.sendSongs( songs )
        songsRepository.sendSongs( songs )
        playlistRepository.sendPlaylists( emptyList() )
        songsAdditionalMetadataRepository.sendMetadata( emptyList() )
        preferencesDataSource.sendUserData(
            emptyUserData.copy( currentlyPlayingSongId = "song-id-3" )
        )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
                shuffle = false,
                favoriteSongIds = emptySet(),
                playlists = emptyList(),
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
                shuffle = false,
                favoriteSongIds = emptySet(),
                playlists = emptyList(),
                songsAdditionalMetadata = emptyList()
            ),
            viewModel.uiState.value
        )
    }

}