package com.squad.musicmatters.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.squad.musicmatters.core.database.dao.PlaylistDao
import com.squad.musicmatters.core.database.dao.PlaylistEntryDao
import com.squad.musicmatters.core.database.model.PlaylistEntity
import com.squad.musicmatters.core.database.model.PlaylistEntryEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID

class PlaylistDaoTest {

    private lateinit var database: MusicMattersDatabase
    private lateinit var playlistDao: PlaylistDao
    private lateinit var playlistEntryDao: PlaylistEntryDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MusicMattersDatabase::class.java
        ).build()
        playlistDao = database.playlistDao()
        playlistEntryDao = database.playlistEntryDao()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun testDeletePlaylist() = runTest {
        val playlist1 = PlaylistEntity(
            id = UUID.randomUUID().toString(),
            title = "Playlist-1"
        )
        val playlist2 = PlaylistEntity(
            id = UUID.randomUUID().toString(),
            title = "Playlist-2"
        )
        playlistDao.insertAll( playlist1, playlist2 )
        assertEquals( 2, playlistDao.fetchPlaylists().first().size )
        playlistDao.delete( playlist2 )
        assertEquals( 1, playlistDao.fetchPlaylists().first().size )
        playlistDao.delete( playlist1 )
        assertTrue(  playlistDao.fetchPlaylists().first().isEmpty() )
    }

    @Test
    fun testRenamePlaylist() = runTest {
        val id1 = UUID.randomUUID().toString()
        val playlist1 = PlaylistEntity(
            id = id1,
            title = "Playlist-1"
        )
        val playlist2 = PlaylistEntity(
            id = UUID.randomUUID().toString(),
            title = "Playlist-2"
        )
        playlistDao.insertAll( playlist1, playlist2 )
        playlistDao.update(
            playlist1.copy(
                title = "Playlist-One"
            )
        )
        assertEquals(
            "Playlist-One",
            playlistDao.fetchPlaylistWithId( id1 ).first()?.playlistEntity?.title
        )
    }

    @Test
    fun testFetchPlaylists() = runTest {
        val id1 = "playlist-id-1"
        val id2 = "playlist-id-2"

        val playlist1 = PlaylistEntity(
            id = id1,
            title = "Playlist 1"
        )
        val playlist2 = PlaylistEntity(
            id = id2,
            title = "Playlist 2"
        )
        playlistDao.insertAll( playlist1, playlist2 )
        ( 1..9 ).map {
            if ( it % 2 == 0 ) PlaylistEntryEntity( playlistId = id2, songId = it.toString() )
            else PlaylistEntryEntity( playlistId = id1, songId = it.toString() )
        }.forEach {
            playlistEntryDao.insert( it )
        }

        val populatedPlaylist1 = playlistDao.fetchPlaylistWithId( id1 ).first()
        val populatedPlaylist2 = playlistDao.fetchPlaylistWithId( id2 ).first()
        assertEquals( 5, populatedPlaylist1?.entries?.size )
        assertEquals( 4, populatedPlaylist2?.entries?.size )

        val populatedPlaylists = playlistDao.fetchPlaylists().first()
        assertEquals(
            9,
            populatedPlaylists.first().entries.size.plus(
                populatedPlaylists.last().entries.size
            )
        )
    }
}