package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.testDoubles.TestQueueDao
import com.squad.musicmatters.core.database.model.QueueEntity
import com.squad.musicmatters.core.testing.repository.TestSongsRepository
import com.squad.musicmatters.core.testing.songs.testSong
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class QueueRepositoryImplTest {

    private lateinit var queueDao: TestQueueDao
    private lateinit var songsRepository: TestSongsRepository
    private lateinit var subject: QueueRepository

    @Before
    fun setUp() {
        queueDao = TestQueueDao()
        songsRepository = TestSongsRepository()
        subject = QueueRepositoryImpl(
            queueDao = queueDao,
            songsRepository = songsRepository
        )
    }

    @Test
    fun testSongsInQueueAreFetchedCorrectly() = runTest {
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" ),
            testSong( id = "song-id-4" ),
            testSong( id = "song-id-5" )
        )
        val queueEntities = listOf(
            QueueEntity( songId = "song-id-1", positionInQueue = 3 ),
            QueueEntity( songId = "song-id-2", positionInQueue = 0 ),
            QueueEntity( songId = "song-id-3", positionInQueue = 2 ),
            QueueEntity( songId = "song-id-4", positionInQueue = 1 ),
        )
        songsRepository.sendSongs( songs )
        queueDao.upsertQueueEntities( queueEntities )

        val songsInQueue = subject
            .fetchSongsInQueueSortedByPosition()
            .first()
            .map { it.id }

        assertEquals(
            listOf(
                "song-id-2",
                "song-id-4",
                "song-id-3",
                "song-id-1"
            ),
            songsInQueue
        )
    }

    @Test
    fun testUpsertSong() = runTest {
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" )
        )
        songsRepository.sendSongs( songs )
        songs.forEachIndexed { index, song ->
            subject.upsertSong(
                song = song,
                posInQueue = index
            )
        }

        assertEquals(
            3,
            subject.fetchSongsInQueueSortedByPosition().first().size
        )
    }

    @Test
    fun testSaveQueue() = runTest {
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" )
        )
        songsRepository.sendSongs( songs )
        subject.saveQueue( songs )

        assertEquals(
            3,
            subject.fetchSongsInQueueSortedByPosition().first().size
        )
    }

    @Test
    fun testRemoveSongWithIdFromQueue() = runTest {
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" )
        )
        songsRepository.sendSongs( songs )
        subject.saveQueue( songs )

        subject.removeSongWithId( id = "song-id-2" )

        assertEquals(
            2,
            subject.fetchSongsInQueueSortedByPosition().first().size
        )
    }

    @Test
    fun testClearQueue() = runTest {
        val songs = listOf(
            testSong( id = "song-id-1" ),
            testSong( id = "song-id-2" ),
            testSong( id = "song-id-3" )
        )
        songsRepository.sendSongs( songs )
        subject.saveQueue( songs )

        assertEquals( 3, subject.fetchSongsInQueueSortedByPosition().first().size )
        subject.clearQueue()
        assertTrue( subject.fetchSongsInQueueSortedByPosition().first().isEmpty() )
    }

}