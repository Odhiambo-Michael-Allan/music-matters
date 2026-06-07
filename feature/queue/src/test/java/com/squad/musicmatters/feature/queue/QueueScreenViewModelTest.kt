package com.squad.musicmatters.feature.queue

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.testing.connection.TestMusicMattersPlayerController
import com.squad.musicmatters.core.testing.repository.TestPreferencesDataSource
import com.squad.musicmatters.core.testing.repository.TestQueueRepository
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
    private lateinit var musicServiceConnection: TestMusicMattersPlayerController
    private lateinit var queueRepository: TestQueueRepository
    private lateinit var viewModel: QueueScreenViewModel

    @Before
    fun setup() {
        preferencesDataSource = TestPreferencesDataSource()
        musicServiceConnection = TestMusicMattersPlayerController()
        queueRepository = TestQueueRepository()
        viewModel = QueueScreenViewModel(
            preferencesDataSource = preferencesDataSource,
            player = musicServiceConnection,
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
        queueRepository.sendSongs( songs )
        preferencesDataSource.sendUserData(
            emptyUserData.copy( currentlyPlayingSongId = "song-id-3" )
        )

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
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

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
            ),
            viewModel.uiState.value
        )

        queueRepository.clearQueue()

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = emptyList(),
                currentlyPlayingSongId = "song-id-3",
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

        assertEquals(
            QueueScreenUiState.Success(
                songsInQueue = songs,
                currentlyPlayingSongId = "song-id-3",
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
            ),
            viewModel.uiState.value
        )
    }

}