package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.PlayHistoryRepository
import com.squad.musicmatters.core.data.testDoubles.TestPlayHistoryDao
import com.squad.musicmatters.core.database.dao.PlayHistoryDao
import com.squad.musicmatters.core.database.model.PlayHistoryEntity
import com.squad.musicmatters.core.testing.repository.TestSongsRepository
import com.squad.musicmatters.core.testing.songs.testSong
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant

class PlayHistoryRepositoryImplTest {

    private lateinit var playHistoryDao: PlayHistoryDao
    private lateinit var songsRepository: TestSongsRepository
    private lateinit var subject: PlayHistoryRepository

    @Before
    fun setUp() {
        playHistoryDao = TestPlayHistoryDao()
        songsRepository = TestSongsRepository()
        subject = PlayHistoryRepositoryImpl(
            playHistoryDao = playHistoryDao,
            songsRepository = songsRepository,
        )
    }

    @Test
    fun testSongsInPlayHistoryAreSortedCorrectly() = runTest {
        val testSongs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" ),
        )
        songsRepository.sendSongs( testSongs )
        listOf(
            PlayHistoryEntity(
                songId = "song-id-3",
                timePlayed = Instant.now().minusSeconds( 5 )
            ),
            PlayHistoryEntity(
                songId = "song-id-2",
                timePlayed = Instant.now().minusSeconds( 4 )
            ),
            PlayHistoryEntity(
                songId = "song-id-4",
                timePlayed = Instant.now().minusSeconds( 3 )
            ),
            PlayHistoryEntity(
                songId = "song-id-1",
                timePlayed = Instant.now().minusSeconds( 2 )
            ),
            PlayHistoryEntity(
                songId = "song-id-5",
                timePlayed = Instant.now().minusSeconds( 1 )
            )
        ).forEach {
            playHistoryDao.upsertPlayHistoryEntity( it )
        }

        val songs = subject.fetchSongsSortedByTimePlayed().first()

        assertEquals(
            listOf(
                "song-id-5",
                "song-id-1",
                "song-id-4",
                "song-id-2",
                "song-id-3"
            ),
            songs.map { it.id }
        )
    }

    @Test
    fun testSongsAreAddedCorrectly() = runTest {
        listOf(
            PlayHistoryEntity(
                songId = "song-id-3",
                timePlayed = Instant.now().minusSeconds( 5 )
            ),
            PlayHistoryEntity(
                songId = "song-id-2",
                timePlayed = Instant.now().minusSeconds( 4 )
            ),
            PlayHistoryEntity(
                songId = "song-id-4",
                timePlayed = Instant.now().minusSeconds( 3 )
            ),
            PlayHistoryEntity(
                songId = "song-id-1",
                timePlayed = Instant.now().minusSeconds( 2 )
            ),
            PlayHistoryEntity(
                songId = "song-id-5",
                timePlayed = Instant.now().minusSeconds( 1 )
            )
        ).forEach {
            subject.upsertSongWithId(
                songId = it.songId,
                timePlayed = it.timePlayed
            )
        }

        assertEquals(
            5,
            playHistoryDao
                .fetchPlayHistoryEntitiesSortedByTimePlayed()
                .first()
                .size
        )
    }

    @Test
    fun testSongIsCorrectlyDeletedFromPlayHistory() = runTest {
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" ),
        )
        songsRepository.sendSongs( songs )

        listOf(
            PlayHistoryEntity(
                songId = "song-id-3",
                timePlayed = Instant.now().minusSeconds( 5 )
            ),
            PlayHistoryEntity(
                songId = "song-id-2",
                timePlayed = Instant.now().minusSeconds( 4 )
            ),
            PlayHistoryEntity(
                songId = "song-id-4",
                timePlayed = Instant.now().minusSeconds( 3 )
            ),
            PlayHistoryEntity(
                songId = "song-id-1",
                timePlayed = Instant.now().minusSeconds( 2 )
            ),
            PlayHistoryEntity(
                songId = "song-id-5",
                timePlayed = Instant.now().minusSeconds( 1 )
            )
        ).forEach {
            subject.upsertSongWithId(
                songId = it.songId,
                timePlayed = it.timePlayed
            )
        }

        subject.deleteSongWithId( "song-id-1" )

        assertEquals(
            listOf(
                "song-id-5",
                "song-id-4",
                "song-id-2",
                "song-id-3",
            ),
            subject.fetchSongsSortedByTimePlayed().first().map { it.id }
        )
        assertEquals(
            listOf(
                "song-id-5",
                "song-id-4",
                "song-id-2",
                "song-id-3"
            ),
            playHistoryDao
                .fetchPlayHistoryEntitiesSortedByTimePlayed()
                .first()
                .map { it.songId }
        )

    }

}