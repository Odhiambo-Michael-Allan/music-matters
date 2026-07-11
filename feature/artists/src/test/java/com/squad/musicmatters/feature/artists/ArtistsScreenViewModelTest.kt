package com.squad.musicmatters.feature.artists

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.data.utils.sortArtists
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.SortArtistsBy
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakeArtistsRepository
import com.squad.musicmatters.core.testing.repository.FakePlaylistsRepository
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ArtistsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var artistsRepository: FakeArtistsRepository
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var preferencesDataSource: FakeUserDataRepository
    private lateinit var playlistRepository: FakePlaylistsRepository
    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var subject: ArtistsScreenViewModel

    @Before
    fun setUp() {
        artistsRepository = FakeArtistsRepository()
        player = FakeMusicMattersPlayer()
        preferencesDataSource = FakeUserDataRepository()
        playlistRepository = FakePlaylistsRepository()
        songsRepository = FakeSongsRepository()
        subject = ArtistsScreenViewModel(
            artistsRepository = artistsRepository,
            songsRepository = songsRepository,
            playlistsRepository = playlistRepository,
            userDataRepository = preferencesDataSource,
            player = player,
        )
    }

    @Test
    fun testUiStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            ArtistsScreenUiState.Loading,
            subject.uiState.value
        )
    }

    @Test
    fun uiStateIsSuccessWhenAllFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        val artists = listOf(
            Artist(
                id = 1,
                name = "Drake",
                trackCount = 2,
                artworkUri = null,
            ),
            Artist(
                id = 2,
                name = "Alicia Keys",
                trackCount = 3,
                artworkUri = null,
            ),
            Artist(
                id = 3,
                name = "Zedd",
                trackCount = 1,
                artworkUri = null,
            )
        )
        preferencesDataSource.sendUserData(
            emptyUserData.copy(
                sortArtistsBy = SortArtistsBy.ARTIST_NAME
            )
        )
        artistsRepository.sendArtists( artists )
        playlistRepository.sendPlaylists( emptyList() )
        songsRepository.sendSongs( emptyList() )

        assertEquals(
            ArtistsScreenUiState.Success(
                artists = artists.sortArtists( by = SortArtistsBy.ARTIST_NAME, reverse = false ),
                sortArtistsBy = SortArtistsBy.ARTIST_NAME,
                sortArtistsInReverse = false,
                playlists = emptyList(),
                songs = emptyList()
            ),
            subject.uiState.value
        )
    }

}