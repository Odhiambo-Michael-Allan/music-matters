package com.squad.musicmatters.feature.albums

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.utils.sortAlbums
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakeAlbumsRepository
import com.squad.musicmatters.core.testing.repository.FakePlaylistRepository
import com.squad.musicmatters.core.testing.repository.FakePreferencesDataSource
import com.squad.musicmatters.core.testing.repository.emptyUserData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AlbumsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var albumsRepository: FakeAlbumsRepository
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var preferencesDataSource: FakePreferencesDataSource
    private lateinit var playlistRepository: FakePlaylistRepository
    private lateinit var subject: AlbumsScreenViewModel

    @Before
    fun setUp() {
        albumsRepository = FakeAlbumsRepository()
        player = FakeMusicMattersPlayer()
        preferencesDataSource = FakePreferencesDataSource()
        playlistRepository = FakePlaylistRepository()
        subject = AlbumsScreenViewModel(
            albumsRepository = albumsRepository,
            player = player,
            preferencesDataSource = preferencesDataSource,
            playlistRepository = playlistRepository,
        )
    }

    @Test
    fun testUiStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            AlbumsScreenUiState.Loading,
            subject.uiState.value
        )
    }

    @Test
    fun uiStateIsSuccessWhenAllFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }
        preferencesDataSource.sendUserData(
            emptyUserData.copy(
                sortAlbumsBy = SortAlbumsBy.TRACK_COUNT
            )
        )
        val albums = listOf(
            Album(
                id = 0L,
                title = "Views",
                trackCount = 2,
                albumArtist = "Drake",
                artworkUri = ""
            ),
            Album(
                id = 1L,
                title = "Scorpion",
                trackCount = 4,
                albumArtist = "Drake",
                artworkUri = ""
            ),
            Album(
                id = 2L,
                title = "More Life",
                trackCount = 5,
                albumArtist = "Drake",
                artworkUri = ""
            )
        )
        albumsRepository.sendAlbums( albums )
        playlistRepository.sendPlaylists( emptyList() )

        assertEquals(
            AlbumsScreenUiState.Success(
                albums = albums.sortAlbums( by = SortAlbumsBy.TRACK_COUNT, reverse = false ),
                sortAlbumsBy = SortAlbumsBy.TRACK_COUNT,
                sortAlbumsInReverse = false,
                playlists = emptyList()
            ),
            subject.uiState.value
        )
    }

}