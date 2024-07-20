package com.odesa.musicMatters.core.data.database.dao

import com.odesa.musicMatters.core.data.TestDatabase
import com.odesa.musicMatters.core.data.database.model.SongPlayCountEntry
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SongPlayCountEntryDaoTest : TestDatabase() {

    private lateinit var dao: SongPlayCountEntryDao

    @Before
    fun setUp() {
        dao = database.songPlayCountEntryDao()
    }

    @Test
    fun testInsertSongPlayCountEntry() = runTest {
        dao.insertAll(
            SongPlayCountEntry(
                songId = testSongs.first().id
            ),
            SongPlayCountEntry(
                songId = testSongs.last().id
            )
        )
        assertEquals( 2, dao.fetchEntries().size )
        assertNotNull( dao.getPlayCountBySongId( testSongs.first().id ) )
        assertNotNull( dao.getPlayCountBySongId( testSongs.last().id ) )
        assertEquals( 1, dao.getPlayCountBySongId( testSongs.first().id )!!.numberOfTimesPlayed )
        assertEquals( 1, dao.getPlayCountBySongId( testSongs.last().id )!!.numberOfTimesPlayed )
    }

    @Test
    fun testIncrementSongPlayCount() = runTest {
        dao.insertAll(
            SongPlayCountEntry(
                songId = testSongs.first().id
            ),
            SongPlayCountEntry(
                songId = testSongs.last().id
            )
        )
        for ( i in 1..3 )
            dao.incrementPlayCount( testSongs.first().id )
        for ( i in 1..2 )
            dao.incrementPlayCount( testSongs.last().id )
        assertEquals( 4, dao.getPlayCountBySongId( testSongs.first().id )!!.numberOfTimesPlayed )
        assertEquals( 3, dao.getPlayCountBySongId( testSongs.last().id )!!.numberOfTimesPlayed )
    }

    @Test
    fun testDeleteEntry() = runTest {
        testSongs.forEach {
            dao.insert(
                SongPlayCountEntry(
                    songId = it.id
                )
            )
        }
        dao.deleteEntryWithSongId( testSongs.first().id )
        assertEquals(
            testSongs.size - 1,
            dao.fetchEntries().size
        )
    }
}