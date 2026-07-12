package com.squad.musicmatters.feature.folders

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.model.Folder
import com.squad.musicmatters.core.model.SortPathsBy
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
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

class FoldersScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var userDataRepository: FakeUserDataRepository
    private lateinit var subject: FoldersScreenViewModel

    @Before
    fun setUp() {
        songsRepository = FakeSongsRepository()
        userDataRepository = FakeUserDataRepository()
        subject = FoldersScreenViewModel(
            songsRepository = songsRepository,
            userDataRepository = userDataRepository
        )
    }

    @Test
    fun testUiStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            FoldersScreenUiState.Loading,
            subject.uiState.value
        )
    }

    @Test
    fun testUiStateIsSuccessWhenAllRequiredFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        val songs = listOf(
            testSong( id = "song-id-1", path = "path/to/song-5/song-id-5" ),
            testSong( id = "song-id-2", path = "path/to/song-2/song-id-2" ),
            testSong( id = "song-id-1", path = "path/to/song-1/song-id-1" ),
            testSong( id = "song-id-3", path = "path/to/song-3/song-id-3" ),
            testSong( id = "song-id-5", path = "path/to/song-5/song-id-5" )
        )
        songsRepository.sendSongs( songs )
        userDataRepository.sendUserData( emptyUserData )

        assertEquals(
            FoldersScreenUiState.Success(
                folders = listOf(
                    Folder(
                        name = "song-1",
                        path = "path/to/song-1",
                        artworkUri = null,
                        trackCount = 1
                    ),
                    Folder(
                        name = "song-2",
                        path = "path/to/song-2",
                        artworkUri = null,
                        trackCount = 1
                    ),
                    Folder(
                        name = "song-3",
                        path = "path/to/song-3",
                        artworkUri = null,
                        trackCount = 1
                    ),
                    Folder(
                        name = "song-5",
                        path = "path/to/song-5",
                        artworkUri = null,
                        trackCount = 2
                    )
                ),
                sortPathsBy = SortPathsBy.NAME,
                sortPathsInReverse = false
            ),
            subject.uiState.value,
        )
    }

}