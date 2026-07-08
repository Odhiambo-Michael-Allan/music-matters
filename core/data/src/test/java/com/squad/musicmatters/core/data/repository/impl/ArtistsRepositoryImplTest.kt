package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.ArtistsRepository
import com.squad.musicmatters.core.model.SortArtistsBy
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.songs.testSong
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ArtistsRepositoryImplTest {

    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var subject: ArtistsRepository

    @Before
    fun setUp() {
        songsRepository = FakeSongsRepository()
        subject = ArtistsRepositoryImpl( songsRepository = songsRepository )
    }

    @Test
    fun testFetchArtists() = runTest {
        songsRepository.sendSongs( emptyList() )
        var artists = subject.fetchArtists().first()
        assertTrue( artists.isEmpty() )

        val testSongs = listOf(
            testSong(
                id = "song-id-1",
                albumId = 1,
                albumTitle = "Views",
                artistId = 1,
                artistTitle = "Drake"
            ),
            testSong(
                id = "song-id-2",
                albumId = 2,
                albumTitle = "More Life",
                artistId = 1,
                artistTitle = "Drake"
            ),
            testSong(
                id = "song-id-3",
                albumId = 1,
                albumTitle = "Views",
                artistId = 3,
                artistTitle = "Lil Wayne"
            ),
            testSong(
                id = "song-id-4",
                albumId = 1,
                albumTitle = "Views",
                artistId = 4,
                artistTitle = "Sia",
            ),
            testSong(
                id = "song-id-5",
                albumId = 1,
                albumTitle = "Views",
                artistId = 1,
                artistTitle = "Drake"
            ),
            testSong(
                id = "song-id-6",
                albumId = 3,
                albumTitle = "Scorpion",
                artistId = 1,
                artistTitle = "Drake"
            )
        )
        songsRepository.sendSongs( testSongs )
        artists = subject.fetchArtists( sortArtistsBy = SortArtistsBy.ARTIST_NAME ).first()
        assertEquals( 3, artists.size )

        assertEquals(
            "Drake",
            artists.first().name
        )

        val drake = subject.fetchArtistWithId( 1 ).first()
        assertEquals( 4, drake.trackCount )
        assertEquals( "Drake", drake.name )

        val sia = subject.fetchArtistWithId( 4 ).first()
        assertEquals( 1, sia.trackCount )
        assertEquals( "Sia", sia.name )
    }

}