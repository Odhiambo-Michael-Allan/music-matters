package com.squad.musicmatters.feature.search

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.SearchFilter
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakePlaylistsRepository
import com.squad.musicmatters.core.testing.repository.FakeSearchRepository
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var searchRepository: FakeSearchRepository
    private lateinit var userDataRepository: FakeUserDataRepository
    private lateinit var playlistsRepository: FakePlaylistsRepository
    private lateinit var subject: SearchScreenViewModel

    @Before
    fun setUp() {
        player = FakeMusicMattersPlayer()
        searchRepository = FakeSearchRepository()
        userDataRepository = FakeUserDataRepository()
        playlistsRepository = FakePlaylistsRepository()
        subject = SearchScreenViewModel(
            searchRepository = searchRepository,
            userDataRepository = userDataRepository,
            playlistsRepository = playlistsRepository,
            player = player,
        )
    }

    @Test
    fun testStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            SearchScreenUiState.Loading,
            subject.uiState.value
        )
    }

    @Test
    fun testUiStateIsSuccessWhenAllRequiredFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        val results = buildMap {
            put( SearchFilter.SONGS, songs )
            put( SearchFilter.ALBUMS, albums )
            put( SearchFilter.ARTISTS, artists )
            put( SearchFilter.GENRES, genres )
            put( SearchFilter.PLAYLISTS, playlists )
        }
        userDataRepository.sendUserData( emptyUserData )
        searchRepository.sendResults( results )

        assertEquals(
            SearchScreenUiState.Success(
                songs = songs,
                albums = albums,
                artists = artists,
                genres = genres,
                playlists = playlists,
            ),
            subject.uiState.value,
        )
    }

}

private val songs = listOf(
    testSong( id = "1", title = "song 1" ),
    testSong( id = "2", title = "song 2" ),
    testSong( id = "3", title = "song 3" ),
    testSong( id = "4", title = "--4" )
)

private val albums = listOf(
    Album(
        id = 1,
        title = "album 1",
        trackCount = 0,
        artworkUri = null,
    ),
    Album(
        id = 2,
        title = "album 2",
        trackCount = 0,
        artworkUri = null,
    ),
    Album(
        id = 3,
        title = "album 3",
        trackCount = 0,
        artworkUri = null,
    ),
    Album(
        id = 4,
        title = "--4",
        trackCount = 0,
        artworkUri = null,
    )
)
private val artists = listOf(
    Artist(
        id = 1,
        name = "artist 1",
        artworkUri = null,
        trackCount = 0,
    ),
    Artist(
        id = 2,
        name = "artist 2",
        artworkUri = null,
        trackCount = 0,
    ),
    Artist(
        id = 3,
        name = "--3",
        artworkUri = null,
        trackCount = 0,
    ),
    Artist(
        id = 4,
        name = "artist 4",
        artworkUri = null,
        trackCount = 0,
    )
)
private val genres = listOf(
    Genre(
        id = 1,
        name = "Genre 1",
        numberOfTracks = 0,
    ),
    Genre(
        id = 2,
        name = "Genre 2",
        numberOfTracks = 0,
    ),
    Genre(
        id = 3,
        name = "--3",
        numberOfTracks = 0,
    ),
    Genre(
        id = 4,
        name = "Genre 4",
        numberOfTracks = 0,
    ),
)
private val playlists = listOf(
    Playlist(
        id = "1",
        title = "--1",
        songIds = emptySet()
    ),
    Playlist(
        id = "2",
        title = "playlist 2",
        songIds = emptySet()
    ),
    Playlist(
        id = "3",
        title = "playlist 3",
        songIds = emptySet()
    ),
    Playlist(
        id = "4",
        title = "playlist 4",
        songIds = emptySet()
    ),
)