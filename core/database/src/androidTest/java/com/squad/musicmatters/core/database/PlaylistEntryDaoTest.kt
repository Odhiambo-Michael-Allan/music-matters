package com.squad.musicmatters.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.squad.musicmatters.core.database.dao.PlaylistDao
import com.squad.musicmatters.core.database.dao.PlaylistEntryDao
import com.squad.musicmatters.core.database.model.PlaylistEntity
import com.squad.musicmatters.core.database.model.PlaylistEntryEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class PlaylistEntryDaoTest {

    private lateinit var database: MusicMattersDatabase
    private lateinit var playlistDao: PlaylistDao
    private lateinit var playlistEntryDao: PlaylistEntryDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            MusicMattersDatabase::class.java,
        ).build()
        playlistDao = database.playlistDao()
        playlistEntryDao = database.playlistEntryDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun testAddSongsToPlaylistWithGivenId() = runTest {
        playlistDao.insert(
            PlaylistEntity(
                id = "playlistId",
                title = ""
            )
        )
        playlistEntryDao.insertAll(
            testSongIds.map {
                PlaylistEntryEntity(
                    playlistId = "playlistId",
                    songId = it
                )
            }
        )
        assertEquals(
            testSongIds.size,
            playlistEntryDao.fetchEntriesForPlaylistWithId( "playlistId" ).first().size
        )
    }

    @Test
    fun testRemoveSongFromPlaylistWithId() = runTest {
        val playlistId = "playlistId"
        playlistDao.insert(
            PlaylistEntity(
                id = playlistId,
                title = ""
            )
        )
        testSongIds.forEach {
            playlistEntryDao.insert(
                PlaylistEntryEntity(
                    playlistId = playlistId,
                    songId = it
                )
            )
        }
        assertEquals(
            testSongIds.size,
            playlistEntryDao.fetchEntriesForPlaylistWithId( playlistId ).first().size
        )
        playlistEntryDao.deleteEntry(
            playlistId = playlistId,
            songId = testSongIds.first()
        )
        assertEquals(
            testSongIds.size.minus( 1 ),
            playlistEntryDao.fetchEntriesForPlaylistWithId( playlistId ).first().size
        )
        playlistEntryDao.deleteEntry(
            playlistId = playlistId,
            songId = testSongIds[1]
        )
        assertEquals(
            testSongIds.size.minus( 2 ),
            playlistEntryDao.fetchEntriesForPlaylistWithId( playlistId ).first().size
        )
    }
}

private val testSongIds = listOf( "song-1", "song-2", "song-3", "song-4", "song-5", "song-6" )