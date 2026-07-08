package com.squad.musicmatters.feature.album

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakeAlbumsRepository
import com.squad.musicmatters.core.testing.repository.FakePlaylistRepository
import com.squad.musicmatters.core.testing.repository.FakePreferencesDataSource
import com.squad.musicmatters.core.testing.repository.FakeSongsMetadataRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.feature.album.navigation.AlbumRoute
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
class AlbumScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var albumsRepository: FakeAlbumsRepository
    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var preferencesDataSource: FakePreferencesDataSource
    private lateinit var playlistRepository: FakePlaylistRepository
    private lateinit var metadataRepository: FakeSongsMetadataRepository
    private lateinit var subject: AlbumScreenViewModel

    @Before
    fun setUp() {
        albumsRepository = FakeAlbumsRepository()
        songsRepository = FakeSongsRepository()
        player = FakeMusicMattersPlayer()
        playlistRepository = FakePlaylistRepository()
        preferencesDataSource = FakePreferencesDataSource()
        metadataRepository = FakeSongsMetadataRepository()
        subject = AlbumScreenViewModel(
            savedStateHandle = SavedStateHandle(
                route = AlbumRoute( albumId = albums[0].id ),
            ),
            albumsRepository = albumsRepository,
            songsRepository = songsRepository,
            player = player,
            playlistRepository = playlistRepository,
            preferencesDataSource = preferencesDataSource,
            songsMetadataRepository = metadataRepository
        )
    }

    @Test
    fun testStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            AlbumScreenUiState.Loading,
            subject.uiState.value
        )
    }

    @Test
    fun testUiStateIsSuccessWhenAllRequiredFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        val songs = listOf(
            testSong( id = "song-id-1", albumId = albums.first().id ),
            testSong( id = "song-id-2", albumId = albums.first().id ),
            testSong( id = "song-id-3", albumId = albums.first().id ),
            testSong( id = "song-id-4", albumId = albums.first().id ),
            testSong( id = "song-id-5", albumId = albums.last().id ),
        )
        albumsRepository.sendAlbums( albums )
        songsRepository.sendSongs( songs )
        preferencesDataSource.sendUserData(
            emptyUserData.copy(
                currentlyPlayingSongId = "song-id-2"
            )
        )
        metadataRepository.sendMetadata( emptyList() )
        playlistRepository.sendPlaylists( emptyList() )

        assertEquals(
            AlbumScreenUiState.Success(
                album = albums.first(),
                songsInAlbum = songs.filter { it.id != "song-id-5" },
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

private val albums = listOf(
    Album(
        id = 0L,
        title = "Views",
        trackCount = 2,
        artist = "Drake",
        artworkUri = ""
    ),
    Album(
        id = 1L,
        title = "Scorpion",
        trackCount = 4,
        artist = "Drake",
        artworkUri = ""
    ),
    Album(
        id = 2L,
        title = "More Life",
        trackCount = 5,
        artist = "Drake",
        artworkUri = ""
    )
)

