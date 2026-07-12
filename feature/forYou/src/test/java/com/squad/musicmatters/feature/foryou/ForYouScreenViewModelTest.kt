package com.squad.musicmatters.feature.foryou

import com.squad.castify.core.testing.rules.MainDispatcherRule
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.testing.connection.FakeMusicMattersPlayer
import com.squad.musicmatters.core.testing.repository.FakeAlbumsRepository
import com.squad.musicmatters.core.testing.repository.FakeArtistsRepository
import com.squad.musicmatters.core.testing.repository.FakePlaylistsRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.repository.FakeUserDataRepository
import com.squad.musicmatters.core.testing.repository.FakePlayHistoryRepository
import com.squad.musicmatters.core.testing.repository.FakeMostPlayedSongsRepository
import com.squad.musicmatters.core.testing.repository.emptyUserData
import com.squad.musicmatters.core.testing.songs.testSong
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ForYouScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var albumsRepository: FakeAlbumsRepository
    private lateinit var artistsRepository: FakeArtistsRepository
    private lateinit var userDataRepository: FakeUserDataRepository
    private lateinit var playHistoryRepository: FakePlayHistoryRepository
    private lateinit var mostPlayedSongsRepository: FakeMostPlayedSongsRepository
    private lateinit var player: FakeMusicMattersPlayer
    private lateinit var subject: ForYouScreenViewModel

    @Before
    fun setUp() {
        songsRepository = FakeSongsRepository()
        albumsRepository = FakeAlbumsRepository()
        artistsRepository = FakeArtistsRepository()
        userDataRepository = FakeUserDataRepository()
        playHistoryRepository = FakePlayHistoryRepository()
        player = FakeMusicMattersPlayer()
        mostPlayedSongsRepository = FakeMostPlayedSongsRepository()
        subject = ForYouScreenViewModel(
            songsRepository = songsRepository,
            albumsRepository = albumsRepository,
            artistsRepository = artistsRepository,
            playHistoryRepository = playHistoryRepository,
            mostPlayedSongsRepository = mostPlayedSongsRepository,
            player = player,
        )
    }

    @Test
    fun testUiStateIsInitiallyLoading() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        assertEquals(
            ForYouScreenUiState.Loading,
            subject.uiState.value
        )
    }

    @Test
    fun testUiStateIsSuccessWhenAllRequiredFlowsEmit() = runTest {
        backgroundScope.launch( UnconfinedTestDispatcher() ) { subject.uiState.collect() }

        val songs = listOf(
            testSong( id = "song-id-1", albumId = 11, artistId = 111, dateModified = 1 ),
            testSong( id = "song-id-2", albumId = 11, artistId = 111, dateModified = 2 ),
            testSong( id = "song-id-3", albumId = 22, artistId = 222, dateModified = 3 ),
            testSong( id = "song-id-4", albumId = 33, artistId = 333, dateModified = 4 ),
            testSong( id = "song-id-5", albumId = 44, artistId = 444, dateModified = 5 ),
            testSong( id = "song-id-6", albumId = 55, artistId = 555, dateModified = 6 ),
            testSong( id = "song-id-7", albumId = 55, artistId = 555, dateModified = 7 ),
            testSong( id = "song-id-8", albumId = 55, artistId = 555, dateModified = 8 ),
            testSong( id = "song-id-9", albumId = 55, artistId = 555, dateModified = 9 ),
            testSong( id = "song-id-10", albumId = 55, artistId = 555, dateModified = 10 )
        )
        val albums = listOf(
            testAlbum( id = 11 ),
            testAlbum( id = 22 ),
            testAlbum( id = 33 ),
            testAlbum( id = 44 ),
            testAlbum( id = 55 ),
        )
        val artists = listOf(
            testArtist( id = 111 ),
            testArtist( id = 222 ),
            testArtist( id = 333 ),
            testArtist( id = 444 ),
            testArtist( id = 555 ),
        )
        songsRepository.sendSongs( songs )
        albumsRepository.sendAlbums( albums )
        artistsRepository.sendArtists( artists )
        playHistoryRepository.sendSongs( songs )
        mostPlayedSongsRepository.sendSongs( songs )

        assertEquals(
            ForYouScreenUiState.Success(
                recentlyAddedSongs = songs,
                suggestedAlbums = albums,
                mostPlayedSongs = songs,
                suggestedArtists = artists,
                recentlyPlayedSongs = songs,
            ),
            subject.uiState.value,
        )
    }
}

private fun testAlbum(
    id: Long,
    title: String = "",
    artist: String? = null,
    artworkUri: String? = null,
    trackCount: Int = 0,
) = Album(
    id = id,
    title = title,
    artist = artist,
    artworkUri = artworkUri,
    trackCount = trackCount,
)

private fun testArtist(
    id: Long,
    name: String = "",
    artworkUri: String? = null,
    trackCount: Int = 0,
) = Artist(
    id = id,
    name = name,
    artworkUri = artworkUri,
    trackCount = trackCount,
)