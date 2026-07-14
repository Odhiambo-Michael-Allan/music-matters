package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.SearchRepository
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.SearchFilter
import com.squad.musicmatters.core.testing.repository.FakeAlbumsRepository
import com.squad.musicmatters.core.testing.repository.FakeArtistsRepository
import com.squad.musicmatters.core.testing.repository.FakeGenresRepository
import com.squad.musicmatters.core.testing.repository.FakePlaylistsRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SearchRepositoryImplTest {

    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var albumsRepository: FakeAlbumsRepository
    private lateinit var artistsRepository: FakeArtistsRepository
    private lateinit var genresRepository: FakeGenresRepository
    private lateinit var playlistsRepository: FakePlaylistsRepository
    private lateinit var subject: SearchRepository

    @Before
    fun setUp() {
        songsRepository = FakeSongsRepository()
        albumsRepository = FakeAlbumsRepository()
        artistsRepository = FakeArtistsRepository()
        genresRepository = FakeGenresRepository()
        playlistsRepository = FakePlaylistsRepository()
        subject = SearchRepositoryImpl(
            songsRepository = songsRepository,
            albumsRepository = albumsRepository,
            artistsRepository = artistsRepository,
            genresRepository = genresRepository,
            playlistsRepository = playlistsRepository,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun testSearchQueryIsEmptyOrBlank_noResultsAreReturned() = runTest {
        songsRepository.sendSongs( songs )
        albumsRepository.sendAlbums( albums )
        artistsRepository.sendArtists( artists )
        genresRepository.sendGenres( genres )
        playlistsRepository.sendPlaylists( playlists )

        var results = subject.search(
            query = "",
            selectedSearchFilter = SearchFilter.ALL,
            userData = emptyUserData
        ).first()

        assertTrue( results.isEmpty() )

        results = subject.search(
            query = " ",
            selectedSearchFilter = SearchFilter.ALL,
            userData = emptyUserData,
        ).first()

        assertTrue( results.isEmpty() )
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