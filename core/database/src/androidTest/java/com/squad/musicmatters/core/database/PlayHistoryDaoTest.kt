package com.squad.musicmatters.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.squad.musicmatters.core.database.dao.PlayHistoryDao
import com.squad.musicmatters.core.database.model.PlayHistoryEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant

class PlayHistoryDaoTest {

    private lateinit var subject: PlayHistoryDao
    private lateinit var db: MusicMattersDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            MusicMattersDatabase::class.java
        ).build()
        subject = db.playHistoryDao()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun testPlayHistoryEntitiesAreFetchedCorrectly() = runTest {
        val entities = listOf(
            PlayHistoryEntity(
                songId = "song-id-1",
                timePlayed = Instant.now().minusSeconds( 5 )
            ),
            PlayHistoryEntity(
                songId = "song-id-2",
                timePlayed = Instant.now().minusSeconds( 4 )
            ),
            PlayHistoryEntity(
                songId = "song-id-3",
                timePlayed = Instant.now().minusSeconds( 3 )
            ),
            PlayHistoryEntity(
                songId = "song-id-4",
                timePlayed = Instant.now().minusSeconds( 2 )
            ),
            PlayHistoryEntity(
                songId = "song-id-5",
                timePlayed = Instant.now().minusSeconds( 1 )
            )
        )
        entities.forEach {
            subject.upsertPlayHistoryEntity( it )
        }

        val historyEntities = subject.fetchPlayHistoryEntitiesSortedByTimePlayed().first()
        assertEquals( 5, historyEntities.size )
        assertEquals(
            listOf(
                "song-id-5",
                "song-id-4",
                "song-id-3",
                "song-id-2",
                "song-id-1"
            ),
            historyEntities.map { it.songId }
        )
    }

    @Test
    fun testPlayHistoryEntityIsDeletedCorrectly() = runTest {
        val entities = listOf(
            PlayHistoryEntity(
                songId = "song-id-1",
                timePlayed = Instant.now().minusSeconds( 5 )
            ),
            PlayHistoryEntity(
                songId = "song-id-2",
                timePlayed = Instant.now().minusSeconds( 4 )
            ),
            PlayHistoryEntity(
                songId = "song-id-3",
                timePlayed = Instant.now().minusSeconds( 3 )
            ),
            PlayHistoryEntity(
                songId = "song-id-4",
                timePlayed = Instant.now().minusSeconds( 2 )
            ),
            PlayHistoryEntity(
                songId = "song-id-5",
                timePlayed = Instant.now().minusSeconds( 1 )
            )
        )
        entities.forEach {
            subject.upsertPlayHistoryEntity( it )
        }

        var historyEntities = subject.fetchPlayHistoryEntitiesSortedByTimePlayed().first()
        assertEquals( 5, historyEntities.size )

        subject.deletePlayHistoryEntityWithSongId( "song-id-2" )
        historyEntities = subject.fetchPlayHistoryEntitiesSortedByTimePlayed().first()

        assertEquals( 4, historyEntities.size )
        assertEquals(
            listOf(
                "song-id-5",
                "song-id-4",
                "song-id-3",
                "song-id-1"
            ),
            historyEntities.map { it.songId }
        )
    }

}