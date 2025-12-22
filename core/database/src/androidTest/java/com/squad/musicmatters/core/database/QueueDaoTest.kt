package com.squad.musicmatters.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.squad.musicmatters.core.database.dao.QueueDao
import com.squad.musicmatters.core.database.model.QueueEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class QueueDaoTest {

    private lateinit var db: MusicMattersDatabase
    private lateinit var queueDao: QueueDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            MusicMattersDatabase::class.java
        ).build()
        queueDao = db.queueDao()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun queueEntitiesAreFetchedCorrectly() = runTest {
        val queueEntities = listOf(
            QueueEntity(
                songId = "song-id-1",
                positionInQueue = 2
            ),
            QueueEntity(
                songId = "song-id-2",
                positionInQueue = 0
            ),
            QueueEntity(
                songId = "song-id-3",
                positionInQueue = 1,
            )
        )
        queueDao.upsertQueueEntities( queueEntities )

        assertEquals(
            listOf(
                "song-id-2",
                "song-id-3",
                "song-id-1"
            ),
            queueDao.fetchQueueEntitiesSortedByPosition().first().map { it.songId }
        )
    }

    @Test
    fun entriesAreDeletedCorrectly() = runTest {
        val queueEntities = listOf(
            QueueEntity(
                songId = "song-id-1",
                positionInQueue = 2
            ),
            QueueEntity(
                songId = "song-id-2",
                positionInQueue = 0
            ),
            QueueEntity(
                songId = "song-id-3",
                positionInQueue = 1,
            )
        )
        queueDao.upsertQueueEntities( queueEntities )

        queueDao.deleteEntryWithId( "song-id-1" )
        assertEquals( 2, queueDao.fetchQueueEntitiesSortedByPosition().first().size )

        queueDao.clearQueue()
        assertTrue( queueDao.fetchQueueEntitiesSortedByPosition().first().isEmpty() )
    }
}