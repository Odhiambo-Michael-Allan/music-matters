package com.odesa.musicMatters.core.data.database.dao

import com.odesa.musicMatters.core.data.TestDatabase
import com.odesa.musicMatters.core.data.database.model.SongPlayCountEntry
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class SongPlayCountEntryDaoTest : TestDatabase() {

    private lateinit var songPlayCountEntryDao: SongPlayCountEntryDao

    @Before
    fun setUp() {
        songPlayCountEntryDao = database.songPlayCountEntryDao()
    }

    @Test
    fun testInsertSongPlayCountEntry() = runTest {
        songPlayCountEntryDao.insertAll(
            SongPlayCountEntry(
                songId = testSongs.first().id
            ),
            SongPlayCountEntry(
                songId = testSongs.last().id
            )
        )
        assertEquals( 2, songPlayCountEntryDao.fetchEntries().size )
        assertNotNull( songPlayCountEntryDao.getPlayCountBySongId( testSongs.first().id ) )
        assertNotNull( songPlayCountEntryDao.getPlayCountBySongId( testSongs.last().id ) )
        assertEquals( 1, songPlayCountEntryDao.getPlayCountBySongId( testSongs.first().id )!!.numberOfTimesPlayed )
        assertEquals( 1, songPlayCountEntryDao.getPlayCountBySongId( testSongs.last().id )!!.numberOfTimesPlayed )
    }

    @Test
    fun testIncrementSongPlayCount() = runTest {
        songPlayCountEntryDao.insertAll(
            SongPlayCountEntry(
                songId = testSongs.first().id
            ),
            SongPlayCountEntry(
                songId = testSongs.last().id
            )
        )
        for ( i in 1..3 )
            songPlayCountEntryDao.incrementPlayCount( testSongs.first().id )
        for ( i in 1..2 )
            songPlayCountEntryDao.incrementPlayCount( testSongs.last().id )
        assertEquals( 4, songPlayCountEntryDao.getPlayCountBySongId( testSongs.first().id )!!.numberOfTimesPlayed )
        assertEquals( 3, songPlayCountEntryDao.getPlayCountBySongId( testSongs.last().id )!!.numberOfTimesPlayed )
    }

}