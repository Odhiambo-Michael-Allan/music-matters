package com.squad.musicmatters.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.squad.musicmatters.core.database.dao.SongAdditionalMetadataDao
import com.squad.musicmatters.core.database.model.SongAdditionalMetadataEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class SongAdditionalMetadataDaoTest {

    private lateinit var database : MusicMattersDatabase
    private lateinit var dao: SongAdditionalMetadataDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MusicMattersDatabase::class.java,
        ).build()
        dao = database.songAdditionalMetadataDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun testInsert() = runTest {
        val songsAdditionalMetadataList = listOf( "song-id-1", "song-id-2" ).map {
            SongAdditionalMetadataEntity(
                songId = it,
                codec = "",
                genre = "",
            )
        }
        dao.insertAll( songsAdditionalMetadataList )
        assertNotNull( dao.fetchAdditionalMetadataForSongWithId( "song-id-1" ) )
        assertNotNull( dao.fetchAdditionalMetadataForSongWithId( "song-id-2" ) )
        assertNull( dao.fetchAdditionalMetadataForSongWithId( "song-id-3" ) )
    }

    @Test
    fun testUpdate() = runTest {
        val songsAdditionalMetadataList = listOf( "song-id-1", "song-id-2" ).map {
            SongAdditionalMetadataEntity(
                songId = it,
                codec = "",
                genre = "",
            )
        }
        dao.insertAll( songsAdditionalMetadataList )
        dao.insert(
            SongAdditionalMetadataEntity(
                songId = "song-id-1",
                codec = "mp3",
                bitrate = 44000L,
                genre = "Pop"
            )
        )
        assertEquals(
            44000L,
            dao.fetchAdditionalMetadataForSongWithId( "song-id-1" )!!.bitrate
        )
    }

    @Test
    fun testObserveTable() = runTest {
        val songId1 = "song-id-1"
        val songId2 = "song-id-2"
        assertTrue( dao.fetchEntries().first().isEmpty() )
        dao.insertAll(
            listOf( songId1, songId2 ).map {
                SongAdditionalMetadataEntity(
                    songId = it,
                    codec = "",
                    genre = "",
                )
            }
        )
        assertEquals( 2, dao.fetchEntries().first().size )
    }

    @Test
    fun testDelete() = runTest {
        val songId1 = "song-id-1"
        val songId2 = "song-id-2"
        listOf( songId1, songId2 ).forEach {
            dao.insert(
                SongAdditionalMetadataEntity(
                    songId = it,
                    codec = "",
                    genre = ""
                )
            )
        }
        dao.deleteEntryWithId( songId1 )
        assertEquals(
            1,
            dao.fetchEntries().first().size
        )
    }
}