package com.squad.musicmatters.feature.artist

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakeArtistsRepository
import com.squad.musicmatters.core.testing.repository.FakePlaylistRepository
import com.squad.musicmatters.core.testing.repository.FakePreferencesDataSource
import com.squad.musicmatters.core.testing.repository.FakeSongsMetadataRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.feature.artist.navigation.ArtistRoute
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
import kotlin.collections.emptyList

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
class ArtistScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var artistsRepository: FakeArtistsRepository
    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var preferencesDataSource: FakePreferencesDataSource
    private lateinit var playlistRepository: FakePlaylistRepository
    private lateinit var metadataRepository: FakeSongsMetadataRepository
    private lateinit var subject: ArtistScreenViewModel

    @Before
    fun setUp() {
        artistsRepository = FakeArtistsRepository()
        songsRepository = FakeSongsRepository()
        preferencesDataSource = FakePreferencesDataSource()
        playlistRepository = FakePlaylistRepository()
        metadataRepository = FakeSongsMetadataRepository()
        player = FakeMusicMattersPlayer()
        subject = ArtistScreenViewModel(
            savedStateHandle = SavedStateHandle(
                route = ArtistRoute( artistId = artists.first().id )
            ),
            artistsRepository = artistsRepository,
            songsRepository = songsRepository,
            preferencesDataSource = preferencesDataSource,
            playlistRepository = playlistRepository,
            songsMetadataRepository = metadataRepository,
            player = player
        )
    }

    @Test
    fun testStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            ArtistScreenUiState.Loading,
            subject.uiState.value
        )
    }

    @Test
    fun testUiStateIsSuccessWhenAllRequiredFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        val songs = listOf(
            testSong( id = "song-id-1", artistId = artists.first().id ),
            testSong( id = "song-id-2", artistId = artists.first().id ),
            testSong( id = "song-id-3", artistId = artists.first().id ),
            testSong( id = "song-id-4", artistId = artists.first().id ),
            testSong( id = "song-id-5", artistId = artists.last().id ),
        )
        artistsRepository.sendArtists( artists )
        songsRepository.sendSongs( songs )
        preferencesDataSource.sendUserData(
            emptyUserData.copy( currentlyPlayingSongId = "song-id-2" )
        )
        metadataRepository.sendMetadata( emptyList() )
        playlistRepository.sendPlaylists( emptyList() )

        assertEquals(
            ArtistScreenUiState.Success(
                artist = artists.first(),
                songsByArtist = songs.filter { it.id != "song-id-5" },
                sortSongsBy = DefaultPreferences.SORT_SONGS_BY,
                sortSongsInReverse = false,
                currentlyPlayingSongId = "song-id-2",
                favoriteSongIds = emptySet(),
                playlists = emptyList(),
                songsMetadata = emptyList()
            ),
            subject.uiState.value,
        )
    }

}

private val artists = listOf(
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