package com.squad.musicmatters.feature.playlists

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.SortPlaylistsBy
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakePlaylistsRepository
import com.squad.musicmatters.core.testing.repository.FakeUserPreferencesRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlaylistsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var playlistsRepository: FakePlaylistsRepository
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var userPreferencesRepository: FakeUserPreferencesRepository
    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var subject: PlaylistsScreenViewModel

    @Before
    fun setUp() {
        playlistsRepository = FakePlaylistsRepository()
        player = FakeMusicMattersPlayer()
        userPreferencesRepository = FakeUserPreferencesRepository()
        songsRepository = FakeSongsRepository()
        subject = PlaylistsScreenViewModel(
            player = player,
            playlistsRepository = playlistsRepository,
            songsRepository = songsRepository,
            userPreferencesRepository = userPreferencesRepository
        )
    }

    @Test
    fun testUiStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            PlaylistsScreenUiState.Loading,
            subject.uiState.value
        )
    }

    @Test
    fun uiStateIsSuccessWhenAllRequiredFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        val playlists = listOf(
            Playlist(
                id = "playlist-1",
                title = "Playlist 1",
                songIds = emptySet(),
            ),
            Playlist(
                id = "playlist-2",
                title = "Playlist 2",
                songIds = emptySet(),
            ),
            Playlist(
                id = "playlist-3",
                title = "Playlist 3",
                songIds = emptySet(),
            )
        )
        playlistsRepository.sendPlaylists( playlists )
        songsRepository.sendSongs( emptyList() )
        userPreferencesRepository.sendUserData( emptyUserData )

        assertEquals(
            PlaylistsScreenUiState.Success(
                playlists = playlists,
                sortPlaylistsBy = SortPlaylistsBy.TITLE,
                sortPlaylistsInReverse = false,
                songs = emptyList()
            ),
            subject.uiState.value
        )
    }
}