package com.squad.musicmatters.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.squad.musicmatters.core.database.dao.SongPlayCountEntryDao
import com.squad.musicmatters.core.database.model.SongPlayCountEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class SongPlayCountEntryDaoTest {

    private lateinit var database: MusicMattersDatabase

    private lateinit var dao: SongPlayCountEntryDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MusicMattersDatabase::class.java,
        ).build()
        dao = database.songPlayCountEntryDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun testInsertSongPlayCountEntry() = runTest {
        val songId1 = "song-id-1"
        val songId2 = "song-id-2"
        listOf(
            SongPlayCountEntity(
                songId = songId1,
            ),
            SongPlayCountEntity(
                songId = songId2,
                numberOfTimesPlayed = 3
            )
        ).forEach { dao.upsertEntity( it ) }
        assertEquals(
            listOf( "song-id-2", "song-id-1" ),
            dao.fetchEntriesSortedByPlayCount().first().map { it.songId }
        )
        assertEquals( 2, dao.fetchEntriesSortedByPlayCount().first().size )
        assertNotNull( dao.getPlayCountBySongId( songId1 ) )
        assertNotNull( dao.getPlayCountBySongId( songId2 ) )
        assertEquals( 1, dao.getPlayCountBySongId( songId1 )!!.numberOfTimesPlayed )
        assertEquals( 3, dao.getPlayCountBySongId( songId2 )!!.numberOfTimesPlayed )
    }

    @Test
    fun testIncrementSongPlayCount() = runTest {
        val songId1 = "song-id-1"
        val songId2 = "song-id-2"
        listOf(
            SongPlayCountEntity(
                songId = songId1
            ),
            SongPlayCountEntity(
                songId = songId2
            )
        ).forEach { dao.upsertEntity( it ) }
        for ( i in 1..3 )
            dao.incrementPlayCount( songId1 )
        for ( i in 1..2 )
            dao.incrementPlayCount( songId2 )
        assertEquals( 4, dao.getPlayCountBySongId( songId1 )!!.numberOfTimesPlayed )
        assertEquals( 3, dao.getPlayCountBySongId( songId2 )!!.numberOfTimesPlayed )
    }

    @Test
    fun testDeleteEntry() = runTest {
        val songId1 = "song-id-1"
        val songId2 = "song-id-2"
        listOf( songId1, songId2 ).forEach {
            dao.upsertEntity(
                SongPlayCountEntity(
                    songId = it
                )
            )
        }
        dao.deleteEntryWithSongId( songId1 )
        assertEquals(
            1,
            dao.fetchEntriesSortedByPlayCount().first().size
        )
    }
}