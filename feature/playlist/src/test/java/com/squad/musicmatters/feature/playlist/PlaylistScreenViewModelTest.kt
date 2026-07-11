package com.squad.musicmatters.feature.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakePlaylistsRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsMetadataRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.feature.playlist.navigation.PlaylistRoute
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
class PlaylistScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var playlistsRepository: FakePlaylistsRepository
    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var userPreferencesRepository: FakeUserDataRepository
    private lateinit var metadataRepository: FakeSongsMetadataRepository
    private lateinit var subject: PlaylistScreenViewModel

    @Before
    fun setUp() {
        playlistsRepository = FakePlaylistsRepository()
        songsRepository = FakeSongsRepository()
        player = FakeMusicMattersPlayer()
        userPreferencesRepository = FakeUserDataRepository()
        metadataRepository = FakeSongsMetadataRepository()
        subject = PlaylistScreenViewModel(
            savedStateHandle = SavedStateHandle(
                route = PlaylistRoute(
                    playlistId = playlists[0].id
                )
            ),
            playlistsRepository = playlistsRepository,
            songsRepository = songsRepository,
            player = player,
            userDataRepository = userPreferencesRepository,
            songsMetadataRepository = metadataRepository
        )
    }

    @Test
    fun testStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            PlaylistScreenUiState.Loading,
            subject.uiState.value
        )
    }

    @Test
    fun testUiStateIsSuccessWhenAllRequiredFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        playlistsRepository.sendPlaylists( playlists )
        songsRepository.sendSongs( songs )
        userPreferencesRepository.sendUserData(
            emptyUserData.copy(
                currentlyPlayingSongId = "song-id-3"
            )
        )
        metadataRepository.sendMetadata( emptyList() )

        assertEquals(
            PlaylistScreenUiState.Success(
                playlist = playlists.first(),
                songsInPlaylist = listOf(
                    testSong( id = "song-id-1" ),
                    testSong( id = "song-id-3" ),
                    testSong( id = "song-id-4" )
                ),
                songsMetadata = emptyList(),
                sortSongsBy = SortSongsBy.TITLE,
                sortSongsInReverse = false,
                currentlyPlayingSongId = "song-id-3",
                playlists = playlists,
                favoriteSongIds = emptySet()
            ),
            subject.uiState.value
        )
    }

}

val songs = listOf(
    testSong( id = "song-id-1" ),
    testSong( id = "song-id-2" ),
    testSong( id = "song-id-3" ),
    testSong( id = "song-id-4" ),
    testSong( id = "song-id-5" ),
)

private val playlists = listOf(
    Playlist(
        id = "playlist-1",
        title = "",
        songIds = setOf( "song-id-1", "song-id-3", "song-id-4" )
    ),
    Playlist(
        id = "playlist-2",
        title = "",
        songIds = emptySet()
    ),
    Playlist(
        id = "playlist-3",
        title = "",
        songIds = emptySet()
    ),
    Playlist(
        id = "playlist-4",
        title = "",
        songIds = emptySet()
    )
)