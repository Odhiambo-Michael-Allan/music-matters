package com.squad.musicmatters.feature.folder

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakePlaylistsRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsMetadataRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
import com.squad.musicmatters.feature.folder.navigation.FolderRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 *
 * These tests use Robolectric because the subject under test ( the ViewModel ) uses
 * `SavedStateHandle.toRoute` which has a dependency on `android.os.Bundle`.
 *
 * TODO: Remove Robolectric if/when AndroidX Navigation API is updated to remove Android dependency.
 * See https://issuetracker.google.com/340966212.
 */
@RunWith( RobolectricTestRunner::class )
class FolderScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var userDataRepository: FakeUserDataRepository
    private lateinit var playlistsRepository: FakePlaylistsRepository
    private lateinit var metadataRepository: FakeSongsMetadataRepository
    private lateinit var subject: FolderScreenViewModel

    @Before
    fun setUp() {
        songsRepository = FakeSongsRepository()
        player = FakeMusicMattersPlayer()
        userDataRepository = FakeUserDataRepository()
        playlistsRepository = FakePlaylistsRepository()
        metadataRepository = FakeSongsMetadataRepository()
        subject = FolderScreenViewModel(
            savedStateHandle = SavedStateHandle(
                route = FolderRoute( path = paths.last() )
            ),
            songsRepository = songsRepository,
            userDataRepository = userDataRepository,
            playlistsRepository = playlistsRepository,
            songsMetadataRepository = metadataRepository,
            player = player,
        )
    }

    @Test
    fun testUiStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            FolderScreenUiState.Loading,
            subject.uiState.value 
        )
    }

}

private val paths = listOf(
    "/storage/emulated/0/Rihanna/ANTI",
    "/storage/emulated/0/Music/Travis Scott/ASTROWORLD",
    "/storage/emulated/0/Music/The Weekend/Beauty Behind The Madness",
    "/storage/emulated/0/Music/Drake/ICEMAN",
    "/storage/emulated/0/Music/The Weekend/Starboy",
    "/storage/emulated/0/Music/Travis Scott/UTOPIA",
    "/storage/emulated/0/Music/Drake/Views"
)