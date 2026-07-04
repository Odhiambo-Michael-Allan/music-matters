package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.testing.repository.FakeSongsRepository
import com.squad.musicmatters.core.testing.songs.testSong
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AlbumsRepositoryImplTest {

    private lateinit var songsRepository: FakeSongsRepository
    private lateinit var subject: AlbumsRepository

    @Before
    fun setUp() {
        songsRepository = FakeSongsRepository()
        subject = AlbumsRepositoryImpl( songsRepository = songsRepository )
    }

    @Test
    fun testFetchAlbums() = runTest {
        var albums = subject.fetchAlbums().first()
        assertTrue( albums.isEmpty() )

        val testSongs = listOf(
            testSong(
                id = "song-id-1",
                albumId = 1,
                albumTitle = "Views",
            ),
            testSong(
                id = "song-id-2",
                albumId = 2,
                albumTitle = "More Life"
            ),
            testSong(
                id = "song-id-3",
                albumId = 1,
                albumTitle = "Views",
            ),
            testSong(
                id = "song-id-4",
                albumId = 1,
                albumTitle = "Views"
            ),
            testSong(
                id = "song-id-5",
                albumId = 1,
                albumTitle = "Views"
            ),
            testSong(
                id = "song-id-6",
                albumId = 3,
                albumTitle = "Scorpion"
            )
        )
        songsRepository.sendSongs( testSongs )
        albums = subject.fetchAlbums().first()
        assertEquals( 6, albums.size )

        val views = subject.fetchAlbumWithId( 3 ).first()
        assertEquals( 3, views.trackCount )
        assertEquals( "Views", views.title )

        val scorpion = subject.fetchAlbumWithId( 3 ).first()
        assertEquals( 1, scorpion.trackCount )
        assertEquals( "Scorpion", scorpion.title )

        val moreLife = subject.fetchAlbumWithId( 2 ).first()
        assertEquals( 1, moreLife.trackCount )
        assertEquals( "More Life", moreLife.title )

    }

}