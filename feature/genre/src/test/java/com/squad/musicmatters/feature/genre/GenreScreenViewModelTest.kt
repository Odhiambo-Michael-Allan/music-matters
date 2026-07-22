package com.squad.musicmatters.feature.genre

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakePlaylistsRepository
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsMetadataRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import com.squad.musicmatters.feature.genre.navigation.GenreRoute
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
class GenreScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val genreId = 1L
    private val genreName = "Rap/HipHop"
    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var preferencesDataSource: FakeUserDataRepository
    private lateinit var playlistRepository: FakePlaylistsRepository
    private lateinit var metadataRepository: FakeSongsMetadataRepository
    private lateinit var subject: GenreScreenViewModel

    @Before
    fun setUp() {
        songsRepository = FakeSongsRepository()
        player = FakeMusicMattersPlayer()
        preferencesDataSource = FakeUserDataRepository()
        playlistRepository = FakePlaylistsRepository()
        metadataRepository = FakeSongsMetadataRepository()
        subject = GenreScreenViewModel(
            savedStateHandle = SavedStateHandle(
                route = GenreRoute(
                    genreId = genreId,
                    genreName = genreName
                )
            ),
            songsRepository = songsRepository,
            playlistsRepository = playlistRepository,
            songsMetadataRepository = metadataRepository,
            player = player,
            userDataRepository = preferencesDataSource
        )
    }

    @Test
    fun testUiStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            GenreScreenUiState.Loading,
            subject.uiState.value
        )
    }

    @Test
    fun testUiStateIsSuccessWhenAllRequiredFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" ),
        )
        val metadata = listOf(
            testSongMetadata( songId = "song-id-1", genreName = genreName ),
            testSongMetadata( songId = "song-id-2", genreName = genreName ),
            testSongMetadata( songId = "song-id-3", genreName = "" ),
            testSongMetadata( songId = "song-id-4", genreName = genreName ),
            testSongMetadata( songId = "song-id-5", genreName = genreName ),
        )
        songsRepository.sendSongs( songs )
        metadataRepository.sendMetadata( metadata )
        preferencesDataSource.sendUserData(
            emptyUserData.copy( currentlyPlayingSongId = "song-id-4" )
        )
        playlistRepository.sendPlaylists( emptyList() )

        assertEquals(
            GenreScreenUiState.Success(
                genreName = genreName,
                songsInGenre = songs.filter { it.id != "song-id-3" },
                sortSongsBy = DefaultPreferences.SORT_SONGS_BY,
                sortSongsInReverse = false,
                currentlyPlayingSongId = "song-id-4",
                favoriteSongIds = emptySet(),
                playlists = emptyList(),
                songsMetadata = metadata
            ),
            subject.uiState.value
        )
    }

}

private fun testSongMetadata(
    songId: String,
    codec: String = "",
    bitsPerSample: Long = 0,
    bitrate: Long = 0,
    samplingRate: Float = 0f,
    genreName: String = ""
) = SongMetadata(
    songId = songId,
    codec = codec,
    bitsPerSample = bitsPerSample,
    bitrate = bitrate,
    samplingRate = samplingRate,
    genreName = genreName,
)
