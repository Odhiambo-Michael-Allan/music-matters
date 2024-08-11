package com.odesa.musicMatters.core.data.database.dao

import com.odesa.musicMatters.core.data.TestDatabase
import com.odesa.musicMatters.core.data.database.model.SongAdditionalMetadata
import com.odesa.musicMatters.core.datatesting.songs.additionalMetadata.songsAdditionalMetadataList
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith( RobolectricTestRunner::class )
class SongAdditionalMetadataDaoTest : TestDatabase() {

    private lateinit var dao: SongAdditionalMetadataDao

    @Before
    fun setUp() {
        dao = database.songAdditionalMetadataDao()
    }

    @Test
    fun testInsert() = runTest {
        dao.insertAll( songsAdditionalMetadataList )
        assertNotNull( dao.fetchAdditionalMetadataForSongWithId( testSongs.first().id ) )
        assertNotNull( dao.fetchAdditionalMetadataForSongWithId( testSongs.last().id ) )
        assertNull( dao.fetchAdditionalMetadataForSongWithId( testSongs[1].id ) )
    }

    @Test
    fun testUpdate() = runTest {
        dao.insertAll( songsAdditionalMetadataList )
        dao.insert(
            SongAdditionalMetadata(
                songId = testSongs.first().id,
                codec = "mp3",
                bitrate = 44000L,
                genre = "Pop"
            )
        )
        assertEquals(
            44000L,
            dao.fetchAdditionalMetadataForSongWithId( testSongs.first().id )!!.bitrate
        )
    }

    @Test
    fun testObserveTable() = runTest {
        assertTrue( dao.observeEntries().first().isEmpty() )
        dao.insertAll( songsAdditionalMetadataList )
        assertEquals( 2, dao.observeEntries().first().size )
    }

    @Test
    fun testDelete() = runTest {
        testSongs.forEach {
            dao.insert(
                SongAdditionalMetadata(
                    songId = it.id,
                    codec = "",
                    genre = ""
                )
            )
        }
        dao.deleteEntryWithId( testSongs.first().id )
        assertEquals(
            testSongs.size - 1,
            dao.observeEntries().first().size
        )
    }
}