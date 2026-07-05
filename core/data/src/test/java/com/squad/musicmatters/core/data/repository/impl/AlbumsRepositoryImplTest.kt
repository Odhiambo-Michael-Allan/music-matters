package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.model.SortAlbumsBy
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
        songsRepository.sendSongs( emptyList() )
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
        albums = subject.fetchAlbums(
            sortAlbumsBy = SortAlbumsBy.ALBUM_NAME,
        ).first()
        assertEquals( 3, albums.size )

        assertEquals(
            "More Life",
            albums.first().title
        )

        val views = subject.fetchAlbumWithId( 1 ).first()
        assertEquals( 4, views.trackCount )
        assertEquals( "Views", views.title )

        val scorpion = subject.fetchAlbumWithId( 3 ).first()
        assertEquals( 1, scorpion.trackCount )
        assertEquals( "Scorpion", scorpion.title )

        val moreLife = subject.fetchAlbumWithId( 2 ).first()
        assertEquals( 1, moreLife.trackCount )
        assertEquals( "More Life", moreLife.title )

    }

}